package domain.event

import domain.person.Person
import domain.person.ReadFlag
import domain.user.AppUser
import security.UserContextHolder
import service.PersonOpinionsCache

/**
 * Base class for person events
 */
class PersonEvent implements Serializable {
    static auditable = [handlersOnly: true]

    static belongsTo = [
        user: AppUser, // Application user who made the action
        // CR: normal dkranchev 02-Mar-2010 Rename property to targetPerson?
        person: Person, // Target person
    ]

    static constraints = {
        user nullable: true
    }

    AppUser user
    Person person

    /**
     * Event date
     */
    Date date = new Date()

    def beforeInsert = {
        if (!user) {
            user = UserContextHolder.contextUser
        }
        PersonOpinionsCache.instance.remove(person.id)
    }

    def onSave = {
        ReadFlag.resetFor(person, UserContextHolder.contextUser)
        PersonOpinionsCache.instance.remove(person.id)
    }

    def PersonEvent() {
    }
}
