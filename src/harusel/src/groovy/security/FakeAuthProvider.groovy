package security

import domain.user.AppUser
import domain.user.Role
import org.apache.commons.codec.digest.DigestUtils
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserImpl
import org.springframework.security.BadCredentialsException
import org.springframework.security.GrantedAuthority
import org.springframework.security.GrantedAuthorityImpl
import org.springframework.security.providers.UsernamePasswordAuthenticationToken
import org.springframework.security.providers.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.userdetails.UserDetails

/**
 * Authentication provider that load user from local database - not from LDAP
 *
 */
class FakeAuthProvider extends AbstractUserDetailsAuthenticationProvider {
    private static final Logger log = Logger.getLogger(FakeAuthProvider.class)

    def appUserService

    /**
     * Checks password hash stored in the session with password in authentication token.
     */
    protected void additionalAuthenticationChecks(UserDetails details,
        UsernamePasswordAuthenticationToken authentication) {

        if (details.password != DigestUtils.md5Hex(authentication.credentials)) {
            throw new BadCredentialsException(details.username)
        }
    }

    /**
     * Retrieves user from LDAP,
     * checks credentials,
     * updates local copy of user data,
     * returns user details.
     */
    protected UserDetails retrieveUser(String login, UsernamePasswordAuthenticationToken authentication) {
        AppUser.withTransaction {
            log.debug("Trying to retrieve user \"$login\"...")
            def password = authentication.credentials?.toString()


            AppUser localUser = AppUser.findByLogin(login, [cache: true])

            if (!localUser) {
                localUser = new AppUser(login: login, fullName: login, disabled: false)
                log.debug("Can't authenticate \"$login\", so create it")
//                throw new BadCredentialsException(login)
            }

            log.debug("User \"$login\" is authenticated.")

            def userRole = login.split("_")[0]?.toUpperCase()
            def roleLdapName = ConfigurationHolder.config.userRolesDescription[userRole]?.ldapName
            if (!roleLdapName) {
                log.debug("Can't recognize user role for \"$login\"")
                throw new BadCredentialsException(login)
            }
            def role = Role.findByLdapName(roleLdapName)
            if (!role) {
                log.debug("Can't find user role by ldapName \"$roleLdapName\"")
                throw new BadCredentialsException(login)
            }
            localUser.addToRoles(role).save()
            log.debug("User '$login' with role ${role.name}")
            def authorities = role.rights.collect {roleRight ->
                log.debug("\thas right \"${roleRight.authority}\"")
                new GrantedAuthorityImpl(roleRight.authority)
            }
            def userDetails = new AppUser();
            userDetails.setAssignedTemplate(localUser.assignedTemplate)
            userDetails.setFullName(localUser.getFullName())
            userDetails.setLogin(localUser.getLogin())
            userDetails.setEmail(localUser.getEmail())
            userDetails.setDisabled(localUser.getDisabled())
            userDetails.setManager(localUser.getManager())
            userDetails.setRoles(new HashSet([role]))


            log.debug("Retrieving user \"$login\" is completed.")
            return new GrailsUserImpl(userDetails.login, DigestUtils.md5Hex(password), true, true, true, true,
                authorities.toArray(new GrantedAuthority[authorities.size()]), userDetails)
        }
    }
}
