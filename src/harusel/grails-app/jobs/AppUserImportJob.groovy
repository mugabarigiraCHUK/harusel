import service.AppUserService

/**
 * Application users import job. This job triggers user import.
 */
class AppUserImportJob {

    static triggers = {
        cron name: 'appUserImport', cronExpression: '0 15 * * * ?', startDelay: 600000
    }

    AppUserService appUserService

    /**
     * Triggers application users import
     */
    def execute() {
        appUserService.importUsers()
    }
}
