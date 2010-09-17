package service

import domain.Vacancy
import domain.user.AppUser
import domain.user.Role
import ldap.LdapUser
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserImpl
import org.springframework.security.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import util.SecurityTools

/**
 * Provides functionality related to users.
 */
class AppUserService {
    /**
     * Closure arranges
     */
    Closure getRoleFilter() {
        return {
            or {
                Role.list([cache: true]).each {
                    eq(ConfigurationHolder.config.ldapMapping.memberOf, it.ldapName)
                }
            }
        }
    }

    /**
     * Searches LDAP user by login name.
     * Doesn't synchronizes user details if found.
     * Returns null if user not found or user is disabled.
     */
    LdapUser findLdapUser(String login) {
        def user = (LdapUser) LdapUser.find {
            and {
                // filter by login
                eq(ConfigurationHolder.config.ldapMapping.login, login)
                // filter by roles
                def roleFilter = getRoleFilter()
                roleFilter.delegate = delegate
                roleFilter()
            }
        }

        return user?.disabled ? null : user
    }

    /**
     * Method finds all LDAP users, that have at least one HRTool application role
     * and synchronizes their data in the local database.
     * Method cleans role list for users, that no longer have role in the LDAP.
     */
    void importUsers() {
        log.debug('Importing users from LDAP')

        // collect local users
        def localUsers = [:]
        AppUser.list([cache: true]).each {user ->
            localUsers[user.login] = user
        }

        // import/update current LDAP users
        try {
            LdapUser.findAll(getRoleFilter()).each {ldapUser ->
                log.debug("Importing $ldapUser.login ($ldapUser.fullName) from LDAP")
                localUsers.remove(ldapUser.login)
                updateLocalUser(ldapUser)
            }
        } catch (org.springframework.ldap.CommunicationException e) {
            log.error("Cannot connect to LDAP server: " + e.getMessage(), e)
        }

        // drop roles in removed users
        localUsers.each {login, user ->
            log.debug("Clearing $login ($user.fullName) roles")
            if (user.roles) {
                user.roles.clear()
                user.save(flush: true)
            }
        }

        log.debug('User import is completed')
    }

    /**
     * Synchronizes user details in local database.
     * Returns application user domain instance.
     */
    AppUser updateLocalUser(LdapUser ldapUser) {
        def login = ldapUser.login
        log.debug("Start updating local user ${login}...")
        def localUser = AppUser.findByLogin(login, [cache: true]) ?: new AppUser()
        if (localUser.id) {
            log.debug("user $login was found in local DB")
            if (localUser.disabled ^ ldapUser.isDisabled()) {
                log.debug("...user ${login} has been ${localUser.disabled ? 'activated' : 'disabled'}...")
            }
        } else {
            log.debug("user $login is new")
        }
        localUser.login = login
        localUser.email = ldapUser.email
        localUser.fullName = ldapUser.fullName ?: login
        localUser.disabled = ldapUser.isDisabled();
        localUser.roles?.clear()
        ldapUser.memberOf.collect { Role.findByLdapName(it, [cache: true]) }.each {role ->
            if (role) {
                localUser.addToRoles(role)
            }
        };
        localUser.save(flush: true)
        log.debug("Update local user $login is complete.")
    }

    @Transactional(readOnly = true)
    def getProjectManagers() {
        return AppUser.createCriteria().listDistinct {
            and {
                roles {
                    eq('id', ConfigurationHolder.config.userRolesDescription.PM.id)
                }
                eq("disabled", false)
            }
            order('fullName', 'asc')
            cache true
        }
    }

    /**
     * Loads list of all employees from database.
     * @return List of employees.
     */
    @Transactional(readOnly = true)
    def getEmployers() {
        List result = AppUser.createCriteria().listDistinct {
            and {
                roles {
                    eq('id', ConfigurationHolder.config.userRolesDescription.USER.id)
                }
                eq("disabled", false)
            }
            order('fullName', 'asc')
            cache true
        }
        return result
    }

    @Transactional(readOnly = true)
    def getEmployersForPM(String pmUserLogin) {
        return AppUser.withCriteria {
            manager {
                eq('login', pmUserLogin)
            }
            order('fullName', 'asc')
            cache true
        }
    }

    GrailsUserImpl getCurrentUser() {
        SecurityContextHolder?.context?.authentication?.principal as GrailsUserImpl
    }

    /**
     * Loads all active vacancies for the given user. If user is HR then all active vacancies are returned.
     * @param user User for which vacancies should be loaded.
     * @return List of vacancies related to the given user.
     */
    @Transactional(readOnly = true)
    List<Vacancy> getAppUserVacancies(AppUser user) {
        if (SecurityTools.isHR(user)) {
            return Vacancy.findAllByActive(true)
        }
        return Vacancy.executeQuery("select distinct vacancy from Vacancy as vacancy, AppUser as user where :user in elements(vacancy.users) and vacancy.active = :active", [user: user, active: true])
    }
}
