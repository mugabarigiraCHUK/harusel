package security

import domain.user.AppUser
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserImpl
import org.springframework.security.BadCredentialsException
import org.springframework.security.GrantedAuthority
import org.springframework.security.GrantedAuthorityImpl
import org.springframework.security.userdetails.UserDetails
import org.springframework.security.userdetails.UserDetailsService
import service.AppUserService

/**
 * @since 14-Jul-2010 11:00:28
 * @version
 */
class HRToolUserDetailsService implements UserDetailsService {

    AppUserService appUserService

    private static final Log log = LogFactory.getLog(HRToolUserDetailsService.class)

    UserDetails loadUserByUsername(String login) {

        AppUser userDetails = AppUser.findByLogin(login, [cache: true])

        if (!userDetails) {
            throw new BadCredentialsException("Cannot find user: " + login);
        }

        def authorities = userDetails.collectAuthorities().collect {String authority ->
            log.debug("\thas right \"$authority\"")
            new GrantedAuthorityImpl(authority)
        }

        log.debug("Retrieving user \"$login\" is completed.")
        return new GrailsUserImpl(userDetails.login, "NO_PASSWORD", true, true, true, true,
                authorities.toArray(new GrantedAuthority[authorities.size()]), userDetails)
    }
}
