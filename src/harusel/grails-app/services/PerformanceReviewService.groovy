import domain.user.AppUser
import performanceReview.Answer
import performanceReview.Form
import performanceReview.Question

/**
 * Service for performance review related activities
 */
class PerformanceReviewService {

    /**
     * Gathers all answers and associates with corresponding question id
     */
    Map getAnswersByQuestionIdMap(AppUser user) {
        def answersByQuestionIdMap = [:]
        def answers = getUnpublishedAnswers(user)
        answers.each {answer ->
            answersByQuestionIdMap[answer.question.id] = answer;
        }
        return answersByQuestionIdMap
    }

    /**
     * Returns tree of question where roots are question groups
     */
    def getFormFor(AppUser user) {
        user.refresh();

        user.assignedTemplate
        def questions = user.assignedTemplate?.questions;

        if (questions == null) {
            questions = [];
        }
        def allQuestions = [] as Set;
        def answeredQuestions = getUnpublishedAnswers(user).collect {it.question}

        allQuestions.addAll(questions)
        allQuestions.addAll(answeredQuestions)

        return toTree(allQuestions);
    }

    /**
     * Returns map for answers of [questionId: answer]
     */
    def getUnpublishedAnswers(def user) {
        user.refresh();
        def form = getUnpublishedForm(user)
        if(form){
            return Answer.findAllByForm(form, [cache:true])
        }
        return [] as List
    }

    /**
     * Finds unpublished PR form if exists otherwise null
     */
    Form getUnpublishedForm(def user) {
        user.refresh();
        List<Form> forms = Form.findAllByUserAndPublished(user, false, [cache: true]) as List
        if (!forms) {
            return null
        }
        if (forms.size() > 1) {
            log.error("Too many unpublished PR form for user ${user.login}");
        }
        return forms[0].refresh()
    }

    /**
     * Finds unpublished PR form is exists otherwise creates new form
     */
    Form getActiveForm(def user) {
        user.refresh();
        def form = getUnpublishedForm(user)
        if (!form) {
            form = new Form(user: user, modificationDate: new Date());
            form.save();
            user.refresh();
        }
        return form
    }

    /**
     * Saves given answer to form
     */
    def saveAnswer(user, questionId, valueName, value) {
        user.discard();
        log.debug("Trying to save answer [$valueName = $value : where question id=$questionId]...")
        Form form = getActiveForm(user)
        //form.modificationDate = new Date();
        def question = Question.get(questionId)
        Answer answer = Answer.findByQuestionAndForm(question, form, [cache:true]);
        if (!answer) {
            answer = new Answer(question: Question.get(questionId), form: form)
        } else {
            log.debug("Answer is changed. old value was <${answer[valueName]}>")
        }
        def dataMap = [:]
        dataMap[valueName] = value
        answer.properties = dataMap
        if(!answer.validate()){
            log.info("Answer <$answer> is invalid: " + answer.errors.allErrors)
            return false;
        }
        def result = answer.save();
        //form.save()
        log.debug("Save answers, status: $result")
        return result
    }

    /**
     * Publicate form with current answers. If there were no answers, create them with zero values
     */
    def publicateCurrentForm(user) {
        user.refresh();

        log.debug("Trying to publicate form of user ${user.login}...")
        Form form = getActiveForm(user)
        form.modificationDate = new Date();
        form.published = true;

        def questions = user.assignedTemplate?.questions;
        if (questions == null) {
            log.warn("Trying publicate, when no assigned template")
        } else {
            log.debug("Finding not answered questions...")
            def answeredQuestionIdList = Answer.findAllByForm(form, [cache:true]).collect { it.question.id }
            questions?.findAll {!answeredQuestionIdList.contains(it.id)}?.each {question ->
                log.debug("\ttake default answer for question: ${question.text}")
                new Answer(question: question, form: form).save();
            }
        }
        form.save(flush: true);
         if (form.hasErrors()) {
                log.error("Errors for Form")
                form.errors.each {
                    log.error(it);
                }
            }
        log.debug("Form publication is complete")
        return form
    }

/**
 * Make tree from list of nodes choosing node.parent as roots
 */
    private def toTree(def list) {
        def roots = [:];
        list.each {node ->
            if (node.parent) {
                roots[node.parent] = roots[node.parent] ?: [] as SortedSet;
                roots[node.parent].add(node);
            }
        }
        return roots.sort {it.key};
    }

}
