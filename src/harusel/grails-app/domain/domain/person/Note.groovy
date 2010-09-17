package domain.person

import domain.user.AppUser

/**
 * Comment on person.
 */
class Note implements Serializable {

    static belongsTo = [
        user: AppUser, // Creator
        person: Person
    ]

    /**
     * Creation date
     */
    Date date

    /**
     * Note text
     */
    String text

    /**
     * Returns note text.
     */
    String toString() {
        text
    }
}
