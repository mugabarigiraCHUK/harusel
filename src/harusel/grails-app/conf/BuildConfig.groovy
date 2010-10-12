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

grails.project.dependency.resolution = {
  inherits "global" // inherit Grails' default dependencies
  log "warn" // log level of Ivy resolver, either 'error',
  // 'warn', 'info', 'debug' or 'verbose'
  repositories {
    grailsPlugins()
    grailsHome()
    grailsCentral()
    mavenCentral()

  }
  dependencies {
    // specify dependencies here under either 'build', 'compile',
    // 'runtime', 'test' or 'provided' scopes.
    runtime 'mysql:mysql-connector-java:5.1.13'
  }
}
