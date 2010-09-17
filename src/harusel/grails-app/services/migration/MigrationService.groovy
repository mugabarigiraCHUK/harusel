package migration

import domain.Vacancy
import jxl.Cell
import jxl.Sheet
import jxl.Workbook
import migration.CandidateList
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger

/**
 * Migration Service
 */
class MigrationService {
    static def log = Logger.getLogger(CandidateList.class)

    static final String REQUEST_LABEL = "Должностные требования"
    static final String INSTRUCTION_LABEL = "Должностные инструкции"
    def personService

    /**
     * Import procedure from Excel file
     *
     * @param filename path to Excel workbook file
     */
    def importFromExcel(String fileName, String documentsDir) {
        log.info("Migration $fileName is started...")
        Workbook workbook = Workbook.getWorkbook(new File(fileName))

        migrateVacancies(workbook)
        new CandidateList(personService).migrate(workbook, documentsDir)

        log.info("Migration $fileName is compete!")
    }

    private def migrateVacancies(Workbook book) {
        def vacancies = [:]
        Sheet sheet = book.getSheet('Должности')
        for (int rowIndex = 1; rowIndex < sheet.getRows(); rowIndex++) {
            Cell[] row = sheet.getRow(rowIndex)
            def name = row[1].getContents()
            if (Vacancy.findByName(name)) {
                continue
            }
            def description = getVacancyDescription(row)
            vacancies[name] = new Vacancy(name: name, description: description, active: true)
        }
        vacancies.values()*.save()
        return vacancies
    }

    private def getVacancyDescription(def row) {
        def request = row[3].getContents()
        def instruction = row[4].getContents()
        request = StringUtils.isBlank(request) ? "" : "$REQUEST_LABEL: $request"
        instruction = StringUtils.isBlank(instruction) ? "" : "$INSTRUCTION_LABEL: $instruction"
        if (request != '') {
            instruction = "\n" + instruction
        }
        return "$request $instruction"
    }

}



