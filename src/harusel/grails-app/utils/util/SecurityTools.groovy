package util

import domain.user.AppUser

/**
 * Provides security-related tools
 * @since 22-Jun-2010 19:28:59
 * @version 1.0
 */
class SecurityTools {

    private static final String PROJECT_MANAGER = 'PM'
    private static final String HR = 'HR'


    private SecurityTools() {

    }
    /**
     * Tests  if user is project manager.
     * @param user User to be checked.
     * @return <code>true</code> if user is project manager, <code>false</code> otherwise.
     */
    static boolean isProjectManager(AppUser user) {
        def userRoles = user.roles.collect {it.name};
        userRoles.contains(PROJECT_MANAGER)
    }

    /**
     * Tests  if user is HR.
     * @param user User to be checked.
     * @return <code>true</code> if user is HR, <code>false</code> otherwise.
     */
    static boolean isHR(AppUser user) {
        def userRoles = user.roles.collect {it.name};
        userRoles.contains(HR)
    }
}

