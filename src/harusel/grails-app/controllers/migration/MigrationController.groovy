package migration

import migration.MigrationCommand
import org.codehaus.groovy.grails.plugins.springsecurity.Secured

@Secured(['ROLE_HR_ALLOWED'])
class MigrationController {
    def migrationService

    def index = {
        render view: "migrateForm", model: [command: [:]]
    }

    def migrate = {MigrationCommand command ->
        if (command.hasErrors()) {
            render view: "migrateForm", model: [command: command]
            return
        }
        migrationService.importFromExcel(command.fileName, command.documentsDir)
        // CR: major dkranchev 02-Mar-2010 Localization missed. 
        request.message = "Migration is complete."
        render view: "migrateForm", model: [command: command]
    }
}
