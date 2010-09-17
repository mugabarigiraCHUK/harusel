package performanceReview

/**
 * Command for saving answer action
 * Validates given answer has question in DB
 */
class SaveAnswerCommand {
    Long questionId
    String valueName
    String value

    // CR: major dkranchev 02-Mar-2010 constrains rename to constraints. 
    static constrains = {
        // CR: major dkranchev 02-Mar-2010 Validator looks inoptimal.
        questionId(nullable: false, validator: {
            Question.get(questionId) != null;
        })
    }

    def String toString() {
        "answer: questionId=${questionId}, name=${valueName}, value=${value}"
    }


}
