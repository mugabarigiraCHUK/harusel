package domain.person

import domain.PersonSource
import domain.Stage
import domain.Vacancy
import domain.event.ChangedProperty
import org.apache.commons.validator.EmailValidator
import security.UserContextHolder
import service.PersonOpinionsCache

/**
 * Candidate or company employee
 */
class Person implements Serializable {

    static transients = ['copy']

    // CR: major dkranchev 02-Mar-2010 Javadoc missed.
    static auditable = ['fullName', 'birthDate', 'emails', 'phones', 'source']

    static hasMany = [
        documents: Document,
        notes: Note,
        scores: ScoreSheet,
        vacancies: Vacancy,
    ]

    static mapping = {
        cache true
        columns {
            notes cache: true
            scores cache: true
            vacancies cache: true, lazy: false, fetch: 'join'
            documents cache: true, fetch: 'join'
        }
        notes joinTable: false, cascade: "delete"
        documents joinTable: false, cascade: "delete"
        scores joinTable: false, cascade: "delete"
        stage index: 'stage_id_idx'
        source index: 'source_id_idx'
        vacancies index: 'vacancy_idx', cascade: "delete"
    };

    static constraints = {
        source(nullable: true)
        fullName(blank: false)
        phones(nullable: true)
        emails(nullable: true, validator: {value ->
            if (value?.trim()) {
                def list = value.tokenize(";").collect { it.trim() }
                def validator = EmailValidator.instance;
                if (list.findAll { !validator.isValid(it) }) {
                    return "invalid.email.error"
                }
            }
            return true
        })
        birthDate(blank: true, nullable: true, matches: /\d{4}((\-\d\d?){2})?/)
    }

    static searchable = {
        fullName boost: 2.0, spellCheck: 'include', termVector: "yes"
        emails index: 'no'
        phones index: 'no'
        documents component: true
        stage component: true
    }


    boolean requiredDecisionsFlag

/**
 * Full name in form: lastName firstName [midName]
 */
    String fullName

/**
 * Birth date in format 'YYYY' or 'YYYY-MM-DD'
 */
    String birthDate

/**
 * Comma or semicolon delimited email list
 */
    // CR: major dkranchev 02-Mar-2010 only ; in code supported.
    // CR: normal dkranchev 02-Mar-2010 why not list of e-mails?
    String emails

/**
 * Comma or semicolon delimited phone list
 */
    // CR: major dkranchev 02-Mar-2010 phones are not validated.
    // CR: normal dkranchev 02-Mar-2010 why not list of phones?
    String phones

/**
 * Candidate source
 */
    PersonSource source

/**
 * Candidate stage
 */
    Stage stage

/**
 * Creates entity backup.
 */
    def copyProperies() {
        def copy = [vacancies: vacancies ? new HashSet(vacancies) : new HashSet()]
        properties.each {key, value ->
            if (auditable.contains(key)) {
                copy[key] = value
            }
        }
        copy
    }

/**
 * Marks person as read by creator. Add
 */
    def onSave = {
        ReadFlag.setFor(this, UserContextHolder.contextUser)
        if (id) {
            PersonOpinionsCache.instance.remove(id)
        }
    }

/**
 * returns map of updated auditable properties.
 */
    def getUpdatedProperties(def copy) {
        if (!copy) {
            log.warn('Entity wasn\'t backed up')
            return
        }
        copy = copy.clone()

        // collect changes
        def changes = []

        // compare vacancies
        def oldVacancies = copy.remove('vacancies')
        if (vacancies != oldVacancies) {
            changes << new ChangedProperty(
                propertyName: 'vacancies',
                newValue: vacancies ? vacancies.join(', ') : '')
        }

        // compare other properties
        copy.each {key, value ->
            def newValue = properties[key]
            if (value != newValue) {
                changes << new ChangedProperty(
                    propertyName: key,
                    newValue: newValue ? newValue.toString() : '')
            }
        }

        changes
    }

/**
 * Returns person's full name.
 */
    String toString() {
        fullName
    }

}
