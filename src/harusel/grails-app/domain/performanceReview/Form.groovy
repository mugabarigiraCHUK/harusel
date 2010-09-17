package performanceReview

import domain.user.AppUser

// CR: major dkranchev 02-Mar-2010 Javadoc missed.
/**
 * Filled PR questionnaire
 */
class Form implements Serializable {
    static constraints = {
    }

    static belongsTo = [
        user: AppUser
    ]

    static mapping = {
        user joinTable: false
    }

    Date modificationDate
    boolean published
    AppUser user

    def Form() {
    }
}
