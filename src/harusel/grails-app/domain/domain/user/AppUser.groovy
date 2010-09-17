package domain.user

import performanceReview.Template

/**
 * Application user. HR specialist, project manager or other authorized employee.
 * Used to cache LDAP data, like contact e-mail and real name
 */
class AppUser implements Serializable {

    static constraints = {
        // CR: normal dkranchev 02-Mar-2010 Is it true? I thought e-mail is required.
        email nullable: true
        assignedTemplate nullable: true
        manager nullable: true
    }

    static hasMany = [
        roles: Role, // user roles
//            forms: Form, // PR answers
    ]

    static mapping = {
        cache true
        columns {
            roles cache: true, lazy: false, fetch: "join", cascade: 'none'
            login index: 'AppUserLogin_Idx'
//            forms cache: 'read-write', batchSize: 10
        }
//        forms joinTable: false
    };

    /**
     * User's roles
     */
    Set roles
    /**
     * PR forms
     */
//    Set forms

    /**
     * Direct manager
     */
    AppUser manager

    /**
     * Performance Review template
     */
    Template assignedTemplate;
    /**
     * User login name as it is in LDAP
     */
    String login

    /**
     * User's full name
     */
    String fullName

    /**
     * User's e-mail address
     */
    String email

    /**
     * Disabled state
     */
    boolean disabled

    /**
     * Collects user rights.
     */
    Set<String> collectAuthorities() {
        if (disabled) {
            return [] as Set
        }
        def result = new HashSet()
        roles.each {role ->
            result.addAll(role.rights.collect { it.authority })
        }
        return result
    }

    /**
     * Returns full name of person is not empty otherwise login
     */
    def String toString() {
        return fullName ?: login;
    }


    def AppUser() {
    }
}
