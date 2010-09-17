package service

import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import performanceReview.Answer
import performanceReview.Question
import performanceReview.Template

/**
 * Service which provides methods to operate with templates.
 * @see Template
 */
class TemplateService {
    public static String NEW_QUESTION_ID = "new"

    private static final Log log = LogFactory.getLog(TemplateService.class)


    boolean transactional = true

    /**
     * Deletes template by id.
     * @param templateId Id of template to be removed.
     * @return <code>true</code> if template was removed, <code>false</code> otherwise.
     */
    def delete(Long templateId) {
        return Template.executeUpdate("DELETE from Template where id = :id", [id: templateId]) > 0;
    }

    /**
     * Saves template.
     * @param templateId Id of template to be updated.
     * @param groups question groups.
     */
    boolean save(Long templateId, def groups) {
        updateQuestions(groups)
        return updateTemplate(templateId, groups)
    }

    /**
     * Update questions - text, index. and remove deleted question
     */
    private void updateQuestions(def updatedQuestionsTree) {
        log.debug("Updating questions ...")
        updateTree(updatedQuestionsTree, null);
        log.debug("Updating questions is complete.")
    }

    /**
     * Walks through JSON tree and updates changed questions
     */
    private void updateTree(def tree, def parent) {
        tree?.eachWithIndex {item, index ->
            if (StringUtils.equals(item.id, NEW_QUESTION_ID)) {
                if (!item.isDeleted) {
                    log.debug("\tCreate new question '${item.text}'");
                    createNewQuestion(item, parent, index);
                    updateTree(item.children, item.entity);
                }
            } else {
                if (!item.isDeleted) {
                    log.debug("\tUpdate question id=${item.id} (${item.text})");
                    updateQuestion(item, parent, index);
                    updateTree(item.children, item.entity);
                } else {
                    checkQuestionId(item.id);
                    Question question = Question.get(item.id as Long)
                    item.entity = question;
                    deleteQuestion(question, parent, index);
                }
            }
        }
    }

    /**
     * Creates new question entity and puts it to updatedItem with key entity, updates updatedItem.id with new id
     */
    private def createNewQuestion(def updatedItem, def parent, def index) {
        def newItem = new Question(text: updatedItem.text, childIndex: index, parent: parent).save();
        updatedItem.id = newItem.id;
        updatedItem.entity = newItem;
        parent?.addToChildren(newItem);
        parent?.save();
        return newItem;
    }

    /**
     * Update old question - text, index, parent
     */
    private def updateQuestion(def updatedItem, def parent, def index) {
        checkQuestionId(updatedItem.id)
        def originalItem = Question.get(Long.parseLong(updatedItem.id));
        if (!originalItem) {
            throw new IllegalArgumentException("Can't find question id=${updatedItem.id}");
        }
        updatedItem.entity = originalItem;
        if (originalItem.parent != null && (parent == null || parent.id != originalItem.parent.id)) {
            log.debug("\tChange questions parent...")
            originalItem.parent.removeFromChildren(originalItem);
        }
        originalItem.text = updatedItem.text;
        originalItem.childIndex = index;
        originalItem.parent = parent;
        originalItem.save()

        parent?.addToChildren(originalItem);
        parent?.save();
        return originalItem;
    }

    /**
     * Check if question id is valid.
     * @param questionId Question ID to check.
     * @return if question id is <code>null</code> or not long then     {@link IllegalArgumentException}     is thrown.
     */
    private def checkQuestionId(questionId) {
        if (questionId == null || !questionId.isLong()) {
            throw new IllegalArgumentException("Question id=${questionId} should be long");
        }
    }

    private def deleteQuestion(Question question, Question parent, int index) {
        if (!question) {
            return;
        }
        question.children.collect {it}.eachWithIndex {child, childIndex ->
            deleteQuestion(child, question, childIndex)
        };
        log.debug("Trying delete question id=${question.id} (${question.text})..");
        if (!question.children && !hasAnswers(question)) {
            log.debug("\t...there are no any answers or children for the question so delete it.")
            question.parent?.removeFromChildren(question);
            question.parent?.save();
            question.delete();
        } else {
            log.debug("\t...there are answers and/or children. Mark as deleted.")
            question.childIndex = index;
            question.isDeleted = true;
            question.save();
            if (question.parent != parent) {
                question.parent?.removeFromChildren(question);
                question.parent?.save();
                question.parent = parent;
            }
        }
    }

    /**
     * Find selected question in the questions tree
     */
    private def retrieveSelectedQuestions(def tree, def resultQuestionsList) {
        tree?.each {question ->
            if ((question.selected as Boolean) && !(question.isDeleted as Boolean)) {
                resultQuestionsList.add(question.entity);
            }
            retrieveSelectedQuestions(question.children, resultQuestionsList);
        }
        return resultQuestionsList;
    }

    private boolean hasAnswers(Question question) {
        return Answer.countByQuestion(question, [cache: true]) > 0
    }

    /**
     * Update given template with new JSON object tree received from client
     */
    private boolean updateTemplate(Long templateId, def tree) {
        Template template = Template.get(templateId)
        log.debug("Updating template id=${template.id} (${template.name}) ...");
        Collection<Question> originalQuestions = template.questions.collect {it};
        Collection<Question> selectedQuestions = retrieveSelectedQuestions(tree, []).findAll { it.parent != null };
        originalQuestions.each {question ->
            if (!selectedQuestions.find {it.id == question.id}) {
                template.removeFromQuestions(question)
                log.debug("\tUnselect question: ${question.id} (${question.text})")
            } else {
                selectedQuestions.remove(question);
            }
        }
        selectedQuestions.each {
            log.debug("\tSelect question: ${it.id} (${it.text})");
            template.addToQuestions(it);
        }
        return !template.save(flush: true).hasErrors()
    }

}
