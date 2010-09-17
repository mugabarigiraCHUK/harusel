import gldapo.Gldapo
import service.InitDataService
import org.codehaus.groovy.grails.commons.ApplicationHolder

class BootStrap {
    InitDataService initDataService

    def init = {servletContext ->
        log.info("Initializing GLDAPO")
        Gldapo.initialize()

        log.info("Initializing application data")
        initDataService.ensureDataInitialized()

        log.info("Initialization completed.")
    }

    def destroy = {
    }
}
