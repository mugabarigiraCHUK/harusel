package domain.user

/**
 * User role: PM, HR, etc...
 */
class Role implements Serializable {

    static hasMany = [
        rights: RoleRight,
    ]

    static mapping = {
        rights lazy: false, fetch: 'join'
        cache true
        columns {
            rights cache: true
        }
    }

    String name
    String description
    /**
     * The Ldap group name corresponded the role
     */
    String ldapName


    def Role() {
    }
}
