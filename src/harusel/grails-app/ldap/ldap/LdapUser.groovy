package ldap

import gldapo.schema.annotation.GldapoNamingAttribute
import gldapo.schema.annotation.GldapoSynonymFor

/**
 * Ldap user presentation
 * NOTE: this is auto generated class. DON'T change it manually.
 * You can set ldap attributes mapping on user properties by initializing map <code>ldapMapping</code>
 * in Config.groovy file, and run grails script <code>setup-ldap</code>
 */
class LdapUser {

    @GldapoNamingAttribute
    String cn

    @GldapoSynonymFor("sAMAccountName")
    String login

    @GldapoSynonymFor("name")
    String fullName

    @GldapoSynonymFor("mail")
    String email

    @GldapoSynonymFor("memberOf")
    Set memberOf

    @GldapoSynonymFor("userAccountControl")
    Integer accountState

    boolean isDisabled() {
        accountState & 2
    }

    String toString() {
        "[$login, $fullName, $email, $memberOf]"
    }
}

