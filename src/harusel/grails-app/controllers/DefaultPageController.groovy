import domain.user.AppUser
import security.UserContextHolder

/**
 * This controller is responsible for forwarding on default page for the given user role.
 */
class DefaultPageController {
    public static def DEFAULT_CONTROLLER = "employee"

    // CR: major dkranchev 02-Mar-2010 List of roles should be hardcoded. 
    public static def ROLE_DEFAULT_CONTROLLER_SORTED_BY_PRIORITY = [
        "HR": "home",
        "PM": "home",
        "USER": "employee"
    ]

    def index = {
        AppUser user = UserContextHolder.contextUser
        log.debug("Searching home page for ${user.login}")
        def userRoles = user.roles.collect {it.name};
        def defaultController = ROLE_DEFAULT_CONTROLLER_SORTED_BY_PRIORITY.find {role, controller ->
            userRoles.contains(role)
        }?.value;
        log.debug("Home page controller for ${user.login}: ${defaultController}")
        redirect controller: defaultController ?: DEFAULT_CONTROLLER;
    }
}
