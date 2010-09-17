package migration

/**
 * Validator for file name of Excel file
 */
public class MigrationCommand {
    String fileName
    String documentsDir

    // CR: normal dkranchev 02-Mar-2010 Sun code conventions violation. 
    static constraints = {
        fileName(nullable: false, blank: false, validator: {String val ->
            File file = new File(val);
            file.canRead() ? true : "migration.file.corrupted"
        })

        documentsDir(nullable: false, blank: false, validator: {String val ->
            File file = new File(val);
            if (!file.canRead()) {
                return "migration.dir.corrupted"
            }
            return file.isDirectory() ? true : "migration.dir.notDir"
        })


    }

}