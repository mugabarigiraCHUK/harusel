import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.commons.lang.text.StrSubstitutor

includeTargets << grailsScript("_GrailsPackage")

target(setupLdap: "Creates LdapUser class definition for updated ldap scheme") {
    depends(compile, createConfig)

    def templateLdapUser = new File("${basedir}/src/templates/LdapUser.template")
    def ldapUser = new File("${basedir}/grails-app/ldap/ldap/LdapUser.groovy")
    if (ldapUser.exists()) {
        ldapUser.delete()
    }
    ldapUser.createNewFile()

    def substitutor = new StrSubstitutor(ConfigurationHolder.config.ldapMapping);
    def buffer = new StringBuffer();
    templateLdapUser.readLines().each {line ->
        buffer << substitutor.replace(line) << "\n"
    }
    ldapUser.write(buffer.toString())
}

setDefaultTarget(setupLdap)
