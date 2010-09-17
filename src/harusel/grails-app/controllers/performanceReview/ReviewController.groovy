package performanceReview

import common.DatePeriodCommand
import domain.user.AppUser
import grails.converters.JSON
import javax.servlet.http.HttpServletRequest
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.time.DateFormatUtils
import org.apache.commons.lang.time.DateUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException
import security.UserContextHolder
import util.SecurityTools

/**
 * Controller for performance review-related operations.
 */
@Secured(['ROLE_HR_ALLOWED', 'ROLE_PM_ALLOWED'])
class ReviewController {
    private static final Log log = LogFactory.getLog(ReviewController.class)


    private static final long MILLIS_PER_MONTH = 30 * DateUtils.MILLIS_PER_DAY

    // TODO: Extract to configuration parameter.
    private static int DEFAULT_AREA_WIDTH = 378

    public static String UNCHECKED_TREE_NODE = "unchecked"
    public static String UNDETERMINED_TREE_NODE = "undetermined"
    public static String CHECKED_TREE_NODE = "checked"

    static final String DATE_PATTERN = "d.MM.yyyy"

    private static def messageCodeOnActionMap = [
        cancel: 'review.template.cancel'
    ]


    def appUserService
    def reviewService
    def templateService

    def index = { }

    /**
     * Adds message to list of messages in request object
     */
    private def addMessage(message) {
        if (!flash.messages) {
            flash.messages = []
        }
        flash.messages << message
    }

    private def addError(message) {
        if (!flash.errors) {
            flash.errors = []
        }
        flash.errors << message
    }

    /**
     * Checks are there any messages to be displayed
     */
    private def checkForMessages() {
        def messageCode = false
        if (params.req) {
            messageCode = messageCodeOnActionMap[params.req]
        }
        if (messageCode) {
            addMessage(message(code: messageCode))
        }
        if (flash.message) {
            addMessage(flash.message)
        }
    }

    @Secured(['ROLE_HR_ALLOWED'])
    def assignedTemplates = {
        def templateList = Template.list()
        def employers = appUserService.employers
        List managers = appUserService.projectManagers
        checkForMessages()
        [users: employers, templates: templateList, managers: managers]
    }

    // CR: major dkranchev 02-Mar-2010 Business logic should be in service.
    @Secured(['ROLE_HR_ALLOWED'])
    def assign = {
        if (!params.assignments) {
            flash.errors = [message(code: "error.message.saveForm.badRequest")]
            // CR: normal dkranchev 02-Mar-2010 Non-localizable non-userfriendly exception.
            throw new IllegalArgumentException("bad request. the assignments are expected")
        }

        def assignments = JSON.parse(params.assignments);
        checkAssignments(assignments);

        assignments.each {entry ->
            def user = AppUser.get(entry.userId as Long);
            if (!user) {
                flash.errors = [message(code: "error.message.saveForm.badRequest")]
                // CR: normal dkranchev 02-Mar-2010 Non-localizable non-userfriendly exception.
                throw new IllegalArgumentException("bad request. there is no user $entry.userId");
            }
            reviewService.assignToUserManagerAndTemplate(user, entry.managerId, entry.templateId)
        }

        render message(code: 'review.template.assigned')
    }

    /**
     * Check both manager id and template id are set or unset
     */
    private void checkAssignments(assignments) {
        def error = assignments.find {
            it.managerId && !it.templateId || !it.managerId && it.templateId
        }
        if (error) {
            flash.errors = [message(code: "error.message.saveForm.badRequest")]
            // CR: normal dkranchev 02-Mar-2010 Non-localizable non-userfriendly exception.
            throw IllegalArgumentException("bad assignments: ${error.managerId} - ${error.templateId}");
        }
    }

    @Secured(['ROLE_HR_ALLOWED'])
    def editTemplates = {
        [templates: Template.list()]
    }

    @Secured(['ROLE_HR_ALLOWED'])
    def questions = {
        Template formTemplate = Template.get(params.id)
        if (!formTemplate) {
            flash.errors = [message(code: "error.message.template.notFound", args: [params.id])];
            throw new IllegalArgumentException("bad form id");
        }
        def selectedIdList = formTemplate.questions.collect {it.id};
        def model = getTemplateModel(reviewService.getVisibleQuestions(), selectedIdList);
        String rootCheckedClass = defineRootCheckedClass(model)
        checkForMessages()
        [model: model, rootCheckedClass: rootCheckedClass];
    }

    /**
     * Defines super root checker status
     */
    // CR: major dkranchev 02-Mar-2010 Business logic should be in service.

    private String defineRootCheckedClass(List model) {
        def rootCheckedClass = UNCHECKED_TREE_NODE;
        if (model) {
            if (model.find { it.checkedClass == UNCHECKED_TREE_NODE }) {
                rootCheckedClass = model.find {
                    it.checkedClass == CHECKED_TREE_NODE || it.checkedClass == UNDETERMINED_TREE_NODE
                } ? UNDETERMINED_TREE_NODE : UNCHECKED_TREE_NODE;
            } else {
                rootCheckedClass = model.find {
                    it.checkedClass == UNDETERMINED_TREE_NODE
                } ? UNDETERMINED_TREE_NODE : CHECKED_TREE_NODE;
            }
        }
        return rootCheckedClass
    }

    /**
     * Prepare questions tree model for rendering.
     */
    // CR: major dkranchev 02-Mar-2010 Business logic should be in service.

    private def getTemplateModel(def questionList, def selectedIdList) {
        def model = []
        def questionsTree = createTree(questionList)
        questionsTree?.each {rootId, children ->
            def root = Question.get(rootId);
            def rootNode = [
                id: root.id,
                text: root.text,
                checkedClass: UNCHECKED_TREE_NODE,
                children: [],
                isDeleted: root.isDeleted
            ];
            def allSelected = true;
            def hasSelected = false;
            children?.each {question ->
                def node = [
                    id: question.id,
                    text: question.text,
                    isDeleted: question.isDeleted
                ]
                if (selectedIdList.contains(question.id)) {
                    node.checkedClass = CHECKED_TREE_NODE;
                    hasSelected = true;
                    allSelected &= true;
                } else if (question.isDeleted) {
                    node.checkedClass = "";
                } else {
                    node.checkedClass = UNCHECKED_TREE_NODE;
                    allSelected = false;
                }
                rootNode.children.add(node);
            }
            if (allSelected && hasSelected) {
                rootNode.checkedClass = CHECKED_TREE_NODE;
            } else if (hasSelected) {
                rootNode.checkedClass = UNDETERMINED_TREE_NODE;
            }
            model.add(rootNode);
        }
        return model;
    }

    // CR: major dkranchev 02-Mar-2010 Business logic should be in service.

    private def createTree(questionList) {
        def questionsTree = [:];
        questionList?.each {question ->
            def parent = question.parent;
            if (parent) {
                questionsTree[parent.id] = questionsTree[parent.id] ?: [] as SortedSet;
                questionsTree[parent.id].add(question);
            } else {
                questionsTree[question.id] = questionsTree[question.id] ?: [] as SortedSet;
            }
        }
        return questionsTree.sort { it.key };
    }

    @Secured(['ROLE_HR_ALLOWED'])
    def saveTemplate = {
        if (!params.groups || !params.id) {
            flash.errors = [message(code: "error.message.saveTemplate.badRequest")];
            // CR: normal dkranchev 02-Mar-2010 Non-localizable non-userfriendly exception.
            throw new IllegalArgumentException("Bad request. the JSON groups and template id had been expected.");
        }
        def groups = JSON.parse(params.groups);
        def templateId = params.id as Long;
        try {
            templateService.save(templateId, groups)
            flash.message = message(code: 'review.template.was.saved')
        }
        catch (DataIntegrityViolationException e) {
            log.warn(e)
            flash.errors = message(code: 'review.template.was.not.saved')
        }

        render 'ok'
    }

    /**
     * Collect all questions Id in list
     */
    private def retrieveQuestionsId(def list, def accumulatorList) {
        list?.inject(accumulatorList) {result, question ->
            result.add(question.entity.id);
            retrieveQuestionsId(question.children, result);
        }
        return accumulatorList;
    }


    @Secured(['ROLE_HR_ALLOWED'])
    def addTemplate = {
        def templateName = StringUtils.trimToNull(params.name);
        // CR: normal dkranchev 02-Mar-2010 Why validation is not used?
        if (!templateName) {
            // CR: normal dkranchev 02-Mar-2010 Non-localizable non-userfriendly exception.
            render "Bad name";
            log.warn("Bad template name on creation: ${params.name}")
            return;
        }
        def template = new Template(name: templateName);
        if (!template.save(flush: true)) {
            log.warn(template.errors);
            // CR: normal dkranchev 02-Mar-2010 Non-localizable non-userfriendly exception.
            render "Can't save new template"
            return;
        }
        render message(code: 'review.template.added');
    }

    def employerFormsReview = {
        def users = []
        if (SecurityTools.isProjectManager(UserContextHolder.contextUser)) {
            users = appUserService.getEmployersForPM(UserContextHolder.contextUser.login);
        }
        else if (SecurityTools.isHR(UserContextHolder.contextUser)) {
            users = appUserService.getEmployers()
        }

        def timelineBuilder = TimelineBuilder.createBuilder(users, 16, DEFAULT_AREA_WIDTH);
        def userTimelineMap = [:];
        if (timelineBuilder) {
            users.each {user ->
                userTimelineMap[user] = timelineBuilder.createTimelineFor(Form.findAllByUserAndPublished(user, true, [cache: true]));
            }
        } else {
            users.each {user ->
                userTimelineMap[user] = []
            }
        }
        userTimelineMap = userTimelineMap.sort { StringUtils.upperCase(it.key.fullName)}
        checkForMessages()
        [
            userTimelineMap: userTimelineMap,
            timelineWidth: timelineBuilder != null ? timelineBuilder.areaWidth : DEFAULT_AREA_WIDTH,
            dateFromInit: new Date(System.currentTimeMillis() - MILLIS_PER_MONTH),
        ];
    }

    def reviewForm = {
        Form form = Form.get(params.id);
        if (!form) {
            flash.errors = [message(code: "error.message.form.notFound", args: [params.id])];
            throw new IllegalArgumentException("Form with id=${params.id} can't be found");
        }

        List formFiller = []
        if (SecurityTools.isProjectManager(UserContextHolder.contextUser)) {
            formFiller = AppUser.executeQuery("select a from AppUser a, Form f where a.manager = :manager and f.user.id = a.id and f.id = :formId",
                [manager: UserContextHolder.contextUser, formId: form.id])
        }
        else if (SecurityTools.isHR(UserContextHolder.contextUser)) {
            formFiller = AppUser.executeQuery("select a from AppUser a, Form f where f.user.id = a.id and f.id = :formId",
                [formId: form.id])
        }

        if (formFiller == null || formFiller.isEmpty()) {
            flash.error = message(code: "error.message.review.form.denied")
            return redirect(view: "review")
        }
        checkForMessages()
        return [
            commentsByGroupMap: createReviewFormModel(form),
            userName: formFiller.get(0).fullName,
            publishDate: DateFormatUtils.format(form.modificationDate, DATE_PATTERN),
            formId: form.id,
        ]
    }

    // CR: major dkranchev 02-Mar-2010 Business logic should be in service.

    private def createReviewFormModel(def form) {
        if (!form.published) {
            flash.errors = [message(code: "error.message.form.notPublished")];
            throw new IllegalArgumentException("form is not published yet");
        }

        def commentsByQuestionGroupMap = [:];
        Answer.findAllByForm(form, [cache: true]).each {Answer answer ->
            def question = answer.question;
            assert question.parent != null, "parent is null in question ${question.text}"
            def questionList = commentsByQuestionGroupMap[question.parent]
            if (!questionList) {
                questionList = commentsByQuestionGroupMap[question.parent] = [];
            }
            def managerComment = Comments.findByAnswerAndManager(answer, UserContextHolder.contextUser, [cache: true])
            def comments = [
                questionIndex: question.childIndex,
                answerId: answer.id,
                questionText: question.text,
                relevance: answer.relevance,
                satisfaction: answer.satisfaction,
                userComment: answer.comment,
                managerComment: managerComment?.comment,
            ]
            questionList << comments
        }

        commentsByQuestionGroupMap.each {key, list -> commentsByQuestionGroupMap[key] = list.sort {it.questionIndex}}
        commentsByQuestionGroupMap = commentsByQuestionGroupMap.sort {it.key};

        return commentsByQuestionGroupMap
    }

    def saveComments = {
        // TODO: Move logic to service.
        Map answerIdToCommentsMap = groupCommentsByAnswerId(request)
        def hasErrors = false;
        answerIdToCommentsMap.each {answerId, commentObject ->
            def answer = Answer.get(answerId as Long);
            if (!answer) {
                log.warn("There is no answer with id=${answerId}");
                return;
            }
            def comment = getManagerComments(answer, UserContextHolder.contextUser);
            bindData(comment, commentObject as Map);

            if (!comment.validate()) {
                hasErrors = true;
            }
            else {
                comment.save(flush: true);
            }
        }
        if (hasErrors) {
            addError(message(code: "performanceReview.message.commentsIsNotSaved"))
        }
        else {
            addMessage(message(code: "performanceReview.message.commentsIsSaved"))
        }
        redirect action: reviewForm, params: [id: params.formId];
    }

    private def getManagerComments(Answer answer, AppUser manager) {
        def comment = Comments.findByAnswerAndManager(answer, manager, [cache: true])
        comment = comment ?: new Comments(manager: UserContextHolder.contextUser, answer: answer)
        return comment
    }

    private Map groupCommentsByAnswerId(HttpServletRequest request) {
        def answerIdToCommentsMap = [:]
        request.parameterMap.each {key, value ->
            // find all names like comment_{answerId}
            def commentDescription = key.split("_").collect {it.trim()};
            if (commentDescription.size() != 2) {
                return
            }
            def commentName = commentDescription[0];
            def answerId = commentDescription[1] as Long
            answerIdToCommentsMap[answerId] = answerIdToCommentsMap[answerId] ?: [:];
            answerIdToCommentsMap[answerId][commentName] = value;
        }
        return answerIdToCommentsMap
    }


    @Secured(['ROLE_HR_ALLOWED', 'ROLE_PM_ALLOWED'])
    def export = {DatePeriodCommand exportPeriod ->
        if (exportPeriod.hasErrors()) {
            log.error("Export period has errors.")
            return render("bad request");
        }

        def answersList = reviewService.getPublicatedAnswersForPeriod(exportPeriod.dateFrom, exportPeriod.dateTo, UserContextHolder.contextUser)

        render(contentType: "application/binary", encoding: "UTF-8") {
            performanceReview {
                for (answer in answersList) {
                    result(
                        date: DateFormatUtils.format(answer.form.modificationDate, DATE_PATTERN),
                        manager: answer.form.user.manager?.fullName ?: message(code: "performanceReview.unknownManager"),
                        category: answer.question.parent?.text ?: 'bad test data, ignore it',
                        question: answer.question.text,
                        relevance: answer.relevance,
                        satisfaction: answer.satisfaction,
                    )
                }
            }
        }
    }
}
