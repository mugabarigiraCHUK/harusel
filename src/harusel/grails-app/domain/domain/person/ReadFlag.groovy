package domain.person

import domain.user.AppUser

/**
 * Flag notifies that certain user had viewed recent person data changes.
 */
class ReadFlag implements Serializable {

    def ReadFlag() {
    }

    static belongsTo = [
            user: AppUser,
            person: Person,
    ]

    /**
     * Checks if person has been read by context user.
     * Returns null for 'unread' persons, ReadFlag instance otherwise.
     * Shoudl be called from transactional method.
     */
    static testFor(Person person, AppUser user) {
            ReadFlag.findByUserAndPerson(user, person, [cache:true])
    }

    /**
     * Marks person as 'read' by context user.
     */
    static setFor(Person person, AppUser user) {
        withTransaction {
            if (!testFor(person, user)) {
                new ReadFlag(user: user, person: person).save()
            }
        }
    }

    /**
     * Resets person 'read' flag for all users, except context owner.
     */
    static resetFor(Person person, AppUser user) {
        withTransaction {
            ReadFlag.executeUpdate('delete ReadFlag f where f.user <> ? and f.person = ?', [user, person])
        }
    }
}
