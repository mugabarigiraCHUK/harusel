package performanceReview

import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.springsecurity.Secured
import security.UserContextHolder

/**
 * Filling performance review forms by employers
 */
@Secured(['ROLE_HR_ALLOWED', 'ROLE_PM_ALLOWED', 'ROLE_EMPLOYEE_ALLOWED'])
class EmployeeController {
    def performanceReviewService

    def index = {
        def user = UserContextHolder.contextUser
        [
            user: user
        ]
    }

    def blankForm = {
        def user = UserContextHolder.contextUser
        Map questions = performanceReviewService.getFormFor(user)
        [
            canPublicate: questions,
            user: user,
            questions: questions,
            answersByQuestionIdMap: performanceReviewService.getAnswersByQuestionIdMap(user)
        ]
    }

    def publicateForm = {
        def user = UserContextHolder.contextUser
        user.refresh()
        performanceReviewService.publicateCurrentForm(user)
        render view: "successFormPublication"
    }

    def saveAnswer = {SaveAnswerCommand command ->
        if (!command.hasErrors()) {
            def user = UserContextHolder.contextUser
            if (!performanceReviewService.saveAnswer(user, command.questionId, command.valueName, command.value)) {
                render(
                    [
                        status: "failed",
                        message: message(code: "error.message.saveForm.badRequest")
                    ] as JSON
                )
            }
            else {
                render "ok";
            }
        } else {
            flash.errors = [message(code: "error.message.saveForm.badRequest")]
            throw new IllegalArgumentException("bad request on saving answer: ${command}");
        }
    }

}
