package domain

import domain.person.Person
import domain.user.AppUser

/**
 * Vacancy
 */
class Vacancy implements Serializable {

    static constraints = {
        name(blank: false)
        description(blank: true, nullable: true)
    }

    static belongsTo = [AppUser, Person]

    static hasMany = [
        criteria: VacancyCriterion, // vacancy-specific criteria
        users: AppUser, // interested users
    ]

    static mapping = {
        cache true
        columns {
            criteria cache: true
        }
    }

    /**
     * Vacancy name
     */
    String name

    /**
     * Detailed vacancy description
     */
    String description

    /**
     * If vacancy is active now
     */
    boolean active

    /**
     * Returns vacancy name. Used at least in the timeline.
     */
    String toString() {
        name
    }
}
