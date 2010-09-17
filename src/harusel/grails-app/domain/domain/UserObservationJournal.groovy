package domain

import domain.person.Person
import domain.user.AppUser

/**
 * Tracker last event user see for given person
 */
class UserObservationJournal implements Serializable {
    /**
     * Date when user read info for given person
     */
    Date lastReadDate

    static belongsTo = [
        person: Person, //Candidate who info was readed by user
        user: AppUser, //User who saw info for given person
    ]

    static constraints = {
    }
}
