package performanceReview

import domain.user.AppUser

// CR: normal dkranchev 02-Mar-2010 rename to ManagerComments?
/**
 * Manager comments on performance review.
 */
class Comments implements Serializable{
    AppUser manager
    String comment

    static belongsTo = [answer: Answer]
    static mapping = {
        answer joinTable: false
    }
    static constraints = {
        // TODO: Discuss about increasing limit. 
        comment(nullable: true, blank: true, maxSize:255)
    }
}
