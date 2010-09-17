package migration

import domain.event.CreateEvent
import domain.person.Document
import domain.person.DocumentType
import jxl.Cell
import jxl.Sheet
import jxl.Workbook
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import security.UserContextHolder

/**
 * Class is responsible for document migration. As source is gotten Excel sheet and predefined folder with files
 */
public class DocumentMigrator {

    static log = Logger.getLogger(DocumentMigrator.class)

    /**
     * The list of suffixes in document name to point on its type as ENG Resume
     */
    static suffixesForEnglishResume = [
        "eng", "altran", "анг", "Альтран"
    ]

    /**
     * The list of suffixes in document name to point on its type as questionnaire
     */
    static suffixesForQuestionnaire = [
        "anketa", "анкета", "собеседование"
    ]

    // DocumentType's are gotten once in constructor and used for defining type 
    DocumentType cv_en
    DocumentType cv_ru
    DocumentType questionnaire

    // cash of person instances from previous migration step  
    def personMapByFullName
    // helper service
    def personService
    /**
     * Candidates files are placed in this folder
     */
    String documentsDir

    /**
     * Constructor
     * @params personMapByFullName map of person object with fullName as key
     */
    def DocumentMigrator(def personMapByFullName, def personService, def documentsDir) {
        this.personService = personService
        this.personMapByFullName = personMapByFullName
        this.cv_en = DocumentType.findByTypeCode("cv_en")
        this.cv_ru = DocumentType.findByTypeCode("cv_ru")
        this.questionnaire = DocumentType.findByTypeCode("questionnaire")
        this.documentsDir = documentsDir
    }

    /**
     * Main function to start migration process
     */
    def migrateDocuments(Workbook book) {
        log.debug("Trying read sheet with candidates files...")
        Sheet sheet = book.getSheet('Кондидаты-Файлы')
        for (int rowIndex = 1; rowIndex < sheet.getRows(); rowIndex++) {
            Cell[] row = sheet.getRow(rowIndex)
            def person = personMapByFullName[row[1].contents]
            if (person == null) {
                log.warn("Person [${row[1].contents}] is unknown. Files are skipped...")
                continue
            }
            def fileName = row[4].contents
            if (StringUtils.isBlank(fileName)) {
                log.warn("File's name is empty")
                continue
            }
            def document = getDocument(fileName)
            if (document == null) {
                continue
            }
            document.user = findPersonCreator(person)
            document.date = new Date()
            personService.addDocumentByUser(document.user, person, document, document.date)
            log.debug("Document is added.")

            // flush and clean up the session
            person.save(flush: true, validate: false)
        }
    }

    /**
     * Read document file with given file name from predefined folder
     */
    private def getDocument(String fileName) {
        def file = new File(documentsDir + fileName)
        if (log.isDebugEnabled()) {
            log.debug("Trying to get file: <${file.getAbsolutePath()}>")
        }
        def document = new Document(name: fileName, removed: !file.canRead())
        if (!document.removed) {
            log.debug("\tFile exist...")
            document.data = FileUtils.readFileToByteArray(file)
            document.date = new Date(file.lastModified())
        } else {
            log.debug("\tThere is no file $fileName. Set deleted flag ...")
            return null
        }
        document.type = getDocumentTypeByName(fileName)
        return document
    }

    /**
     * The name of documents has complicated form, this function tries dedicate type of documents by suffix | prefix
     * like Altran, resume...
     */
    private def getDocumentTypeByName(String fileName) {
        DocumentType type = cv_ru
        if (hasSuffixFromList(fileName, suffixesForEnglishResume)) {
            type = cv_en
        } else if (hasSuffixFromList(fileName, suffixesForQuestionnaire)) {
            type = questionnaire
        }
        if (log.isDebugEnabled()) {
            log.debug("Document type for $fileName is detected as $type")
        }
        return type
    }


    private hasSuffixFromList(String fileName, def suffixList) {
        null != suffixList.find {suffix -> fileName.toLowerCase().contains(suffix) }
    }

    /**
     * There is no information in Excel who added document.
     * It's assumed user created person in system is responsible for files
     */
    private def findPersonCreator(def person) {
        def user = CreateEvent.withCriteria {
            and {
                eq("person.id", person.id)
            }
        }?.get(0).user

        return user ?: UserContextHolder.contextUser
    }
}