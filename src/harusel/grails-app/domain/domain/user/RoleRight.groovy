package domain.user

/**
 * Permission on some activity.
 */
class RoleRight implements Serializable {

    /**
     * Human readable right name
     */
    // CR: normal dkranchev 02-Mar-2010 Non localizable.
    String name

    // CR: normal dkranchev 02-Mar-2010 Unclear purpose.
    /**
     * Authority
     */
    String authority

    def RoleRight() {
    }
}
