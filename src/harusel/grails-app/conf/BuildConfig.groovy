grails.project.plugins.dir = "./plugins"

grails.war.destFile = "hrtool.war"

coverage {
    xml = true
    enabledByDefault = false

    exclusions = [
        "**/org/grails/plugins/**",
        "**/grails/ldap/**",
    ]
}
