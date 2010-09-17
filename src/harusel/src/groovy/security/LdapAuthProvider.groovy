package security

import domain.user.AppUser
import org.apache.commons.codec.digest.DigestUtils
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserImpl
import org.springframework.security.BadCredentialsException
import org.springframework.security.GrantedAuthority
import org.springframework.security.GrantedAuthorityImpl
import org.springframework.security.providers.UsernamePasswordAuthenticationToken
import org.springframework.security.providers.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.userdetails.UserDetails

/**
 * Authentication provider that checks user credentials against LDAP
 */
class LdapAuthProvider extends AbstractUserDetailsAuthenticationProvider {
    private static final Logger log = Logger.getLogger(LdapAuthProvider.class)

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

            def ldapUser = appUserService.findLdapUser(login)
            if (!(password && ldapUser?.authenticate(password))) {
                log.debug("Can't authenticate \"$login\"")
                throw new BadCredentialsException(login)
            }

            AppUser localUser = AppUser.executeQuery("FROM AppUser a LEFT JOIN FETCH a.roles where login = :login",
                [login: login],
                [cache: true]).first()

            if (!localUser) {
                log.debug("Can't authenticate \"$login\"")
                localUser = appUserService.updateLocalUser(ldapUser)
            }

            log.debug("User \"$login\" is authenticated.")
            def authorities = localUser.collectAuthorities().collect {String authority ->
                log.debug("\thas right \"$authority\"")
                new GrantedAuthorityImpl(authority)
            }
            def userDetails = new AppUser();
            userDetails.setAssignedTemplate(localUser.assignedTemplate)
            userDetails.setFullName(localUser.getFullName())
            userDetails.setLogin(localUser.getLogin())
            userDetails.setEmail(localUser.getEmail())
            userDetails.setDisabled(localUser.getDisabled())
            userDetails.setManager(localUser.getManager())
            userDetails.setRoles(new HashSet(localUser.getRoles()))


            log.debug("Retrieving user \"$login\" is completed.")
            return new GrailsUserImpl(userDetails.login, DigestUtils.md5Hex(password), true, true, true, true,
                authorities.toArray(new GrantedAuthority[authorities.size()]), userDetails)
        }
    }
}
