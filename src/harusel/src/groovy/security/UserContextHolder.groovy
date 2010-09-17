package security

import domain.user.AppUser
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser
import org.springframework.security.GrantedAuthority
import org.springframework.security.context.SecurityContextHolder

/**
 * Utility class for context user tracking.
 */
// TODO: Use http session for user holding
public class UserContextHolder {
    private static final Log log = LogFactory.getLog(UserContextHolder.class)
    private static InheritableThreadLocal<AppUser> context = new InheritableThreadLocal<AppUser>()

    /**
     * Returns current context user. If user had been set explicitly, it will be returned,
     * otherwise it will be taken from security context holder.
     */
    static AppUser getContextUser() {
        AppUser user = context.get()
        if (!user) {
            // todo check, why we need to use 'getContext()' instead of 'context'
            def principal = SecurityContextHolder.getContext().authentication?.principal
            if (principal instanceof GrailsUser) {
                user = AppUser.findByLogin(principal.domainClass.login)
            }
        }
        else {
            log.debug("Reusing user from context");
        }
        if (!user) {
            user = AppUser.findByLogin('application', [cache: true])
        }

        user
    }

    /**
     * Returns authorities or null if there is no authenticated user.
     * WARNING: result doesn't depend on what was set in setContextUser method.
     */
    static GrantedAuthority[] getAuthorities() {
        SecurityContextHolder.getContext().authentication?.authorities
    }

    /**
     * Overrides current context
     */
    static void setContextUser(AppUser user) {
        log.debug("Trying to override context user for \"${user.login}\"")
        if (user) {
            context.set user
        } else {
            context.remove()
        }
    }
}
