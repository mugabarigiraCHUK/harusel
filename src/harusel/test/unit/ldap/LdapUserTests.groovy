package ldap

import gldapo.Gldapo
import gldapo.GldapoDirectoryRegistry
import gldapo.GldapoSchemaRegistry
import gldapo.GldapoTypeMappingRegistry
import grails.test.GrailsUnitTestCase
import ldap.LdapUser

public class LdapUserTests extends GrailsUnitTestCase {

    void testConnect() {
//        Gldapo.initialize(new URL("file:./grails-app/conf/Config.groovy"))
        Gldapo.initialize(
                directories: [
                        officeLocal: [
                                url: "ldap://localhost",
                                base: "dc=office,dc=local",
                                userDn: "cn=admin,dc=office,dc=local",
                                password: "root",
                        ],
                        office: [
                                url: "ldap://10.0.0.169",
                                base: "dc=office,dc=local",
                                userDn: "cn=testHR,cn=Users,dc=office,dc=local",
                                password: "testhr1",
                        ]
                ],
                schemas: [ldap.LdapUser],
                typeMappings: [:],
                settings: [:]
        )
        println "initialized!"
        assert (Gldapo.instance.directories instanceof GldapoDirectoryRegistry)
        assert (Gldapo.instance.schemas instanceof GldapoSchemaRegistry)
        assert (Gldapo.instance.typemappings instanceof GldapoTypeMappingRegistry)
        println "gldapo is ok"

        def matches = LdapUser.findAll(
                directory: "office",
                base: "cn=Users",
                filter: "(&(objectClass=user)(memberOf=^))"
        )

//        println matches
        println matches.size()

        matches.each {LdapUser it ->
            println "User = $it"
        }
    }
}
