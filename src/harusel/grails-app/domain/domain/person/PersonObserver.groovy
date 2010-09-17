package domain.person

import domain.person.Person
import domain.user.AppUser

/**
 * This entity is additional person flag.
 * Users who are subscribed on vacancy can unsubscribe from person by creating instance of this class and setting
 * ignorePersonUpdates to true.
 * Other users can also be unsubscribed from vacancy but be notified about person updates.
 *
 */
class PersonObserver implements Serializable {

    // CR: normal dkranchev 02-Mar-2010 bad formatting.
    static belongsTo = [user: AppUser, person: Person]

    boolean ignorePersonUpdates

    // CR: normal dkranchev 02-Mar-2010 remove?
    static constraints = {
    }

    def PersonObserver() {
    }
}
