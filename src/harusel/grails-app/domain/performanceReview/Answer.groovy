// CR: major dkranchev 02-Mar-2010 Do not use camel case in package names, rename package to review.
package performanceReview

/**
 * Answer for the question
 */
// CR: major dkranchev 02-Mar-2010 Javadoc missed.
class Answer implements Serializable {
    // CR: major dkranchev 02-Mar-2010 What constraints are applied to relevance?
    int relevance;
    // CR: major dkranchev 02-Mar-2010 What constraints are applied to satisfaction?
    int satisfaction;
    String comment
    Form form
    Question question

    static mapping = {
        form joinTable: false
    }

    static belongsTo = [Question, Form]

    static constraints = {
        // TODO: Discuss about increasing limit.
        comment(nullable: true, blank: true, maxSize: 255)
    }
}
