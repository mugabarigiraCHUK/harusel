package performanceReview

/**
 * Performance Review Template for collect specified questions
 */
// CR: major dkranchev 02-Mar-2010 Generic name. Rename e.g. to PerfReviewTemplate
class Template implements Serializable{
    /**
     * Template name
     */
    String name;

    // TODO: remove questions.
    static hasMany = [
        questions: Question,
    ]

    static belongsTo = [
        Question
    ]
}
