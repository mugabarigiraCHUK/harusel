package migration

import domain.Stage
import domain.Vacancy
import domain.person.Person
import domain.user.AppUser
import java.text.DateFormat
import java.text.SimpleDateFormat
import jxl.Cell
import jxl.Sheet
import jxl.Workbook
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import security.UserContextHolder

/**
 * Class used for migration candidates
 */
class CandidateList {
    static def log = Logger.getLogger(CandidateList.class)
    static DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    static String COMMENT_TO_IGNORE = "Создано автоматически"
    static String CONTACT_SPLITTER = ";"

    private def book
    private def personMapByFullName
    private def users
    private def stageMapping

    def personService

    def CandidateList(def personService) {
        this.personService = personService;
        def stages = Stage.list()
        stageMapping = ["Резюме принято к рассмотрению": stages.find { it.name == "Рассмотрение резюме"},
            "Отложен": stages.find { it.name == "Отложен"},
            "Отклонен": stages.find { it.name == "Отклонен"},
            "В коротком списке на вакансию": stages.find { it.name == "Отложен"},
            "Предложили работать": stages.find { it.name == "Ожидание согласия кандидата"},
            "Принят на испытательный срок": stages.find { it.name == "Ожидаем выхода на работу"},
            "Не прошел испытательный срок": stages.find { it.name == "Отклонен"},
            "Принят на работу": stages.find { it.name == "Ожидаем выхода на работу"},
            "Участвует в проекте": stages.find { it.name == "Ожидаем выхода на работу"},
            "Приглашен на собеседование": stages.find { it.name == "Приглашен на собеседование"},
        ]
    }

    /**
     * Main method for migration.
     * @param workbook - Excel workbook
     */
    def migrate(Workbook workbook, def documentsDir) {
        book = workbook

        personMapByFullName = [:]
        users = AppUser.list()
        readCandidates()
        readContacts()
        readDocuments(documentsDir)
        readSolves()
        readComments()
        readSuccessCandidates()
    }

    private def readCandidates() {
        def stageNew = Stage.findByName('Новый')
        forEachRowsOnSheet('Кондидаты') {row ->
            def fullName = row[1].getContents()
            if (StringUtils.isBlank(fullName)) {
                log.warn("Row ${row[0].row} has blank fullName column")
                return
            }
            def vacancyName = row[5].getContents()
            def vacancy = null
            if (!StringUtils.isBlank(vacancyName)) {
                vacancy = Vacancy.findByName(vacancyName, [cache: true])
                if (!vacancy) {
                    log.warn("Can't find vacancy [$vacancyName]")
                }
            }
            def person = personMapByFullName[fullName]
            if (!person) {
                person = Person.findByFullName(fullName, [cache: true])
                if (!person) {
                    person = new Person(fullName: fullName, stage: stageNew)
                }
                personMapByFullName[fullName] = person
            }
            if (vacancy) {
                person.addToVacancies(vacancy).save(flush: true, validate: false)
            }
            person.save(flush: true, validate: false)
            if (person.hasErrors()) {
                log.error("Errors for Person")
                person.errors.each {
                    log.error(it);
                }
            }
        }
    }

    private def readContacts() {
        // it's assumed sheet is sorted by full name
        def personContacts = []
        def fullName = null
        forEachRowsOnSheet('Кондидаты-Контакты') {row ->
            def nextFullName = row[1].contents
            if (!StringUtils.equals(nextFullName, fullName)) {
                organizePersonContacts(fullName, personContacts)
                fullName = nextFullName
                personContacts = []
            }
            personContacts.add(row[2].contents)
        }
        organizePersonContacts(fullName, personContacts)
    }

    private def organizePersonContacts(String fullName, List personContacts) {
        if (personContacts.size() == 0) {
            return
        }
        def person = personMapByFullName[fullName]
        if (person == null) {
            log.warn("Person [$fullName] is unknown, but there are contacts for him")
            return
        }
        personContacts.each {contact ->
            addContactToPerson(contact, person)
        }
        person.save(flush: true, validate: false)
        if (person.hasErrors()) {
            log.error("Errors for Person")
            person.errors.each {
                log.error(it)
            }
        }
    }

    private def addContactToPerson(String contact, Person person) {
        def emailMatcher = contact =~ /[\w|.]+@\w+\.\w+/
        if (emailMatcher) {
            def emails = person.emails ? person.emails.split(CONTACT_SPLITTER).toList() : []
            emailMatcher.each { emails << it }
            person.emails = emails.join(CONTACT_SPLITTER)
            if (log.isDebugEnabled()) {
                log.debug("Email detected: ${contact}")
            }
            // string for phone contains digits about >50% of all text
        } else if ((contact =~ /[\d|(|)|-|\s]/).size() > contact.length() / 2) {
            List phones = person.phones ? person.phones.split(CONTACT_SPLITTER).toList() : []
            phones.add(contact)
            person.phones = phones.join(CONTACT_SPLITTER)
            if (log.isDebugEnabled()) {
                log.debug("Phone detected: ${contact}")
            }
        } else {
            // todo: otherwise, contact string contains address. where the system should store it is unknown.
            if (log.isDebugEnabled()) {
                log.warn("Unknown contact type: '${contact}' of person ${person.fullName}")
            }
        }
    }

    private def readSolves() {
        def dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        forEachRowsOnSheet('Кондидаты-Решение') {row ->
            def person = personMapByFullName[row[1].contents]
            if (person == null) {
                log.warn("Person [${row[1].contents}] is unknown")
                return
            }
            def stage = stageMapping[row[4].contents]
            def date = dateFormatter.parse(row[2].contents)
            def comments = row[5].contents
            def user = getUserByShortName(row[6].contents)
            personService.setStage(user, person, stage, comments, date, false)
            person.save(flush: true, validate: false)
        }
    }

    private def getUserByShortName(def shortName) {
        def user = users.find {user ->
            // it's assumed that family is the first word in short name
            user.fullName.toUpperCase().contains(shortName.toUpperCase().split(" ")[0].trim())
        }
        if (log.isDebugEnabled()) {
            log.debug("User ${user} is detected for short name $shortName")
        }
        return user
    }

    private def readDocuments(def documentsDir) {
        new DocumentMigrator(personMapByFullName, personService, documentsDir).migrateDocuments(book)
    }

    private def readComments() {
        readCommentsFromSheet('Документы-РегистрацияКондидата')
        readCommentsFromSheet('Документы-ОценкаКондидата')
    }

    private def readCommentsFromSheet(String sheetName) {
        forEachRowsOnSheet(sheetName) {row ->
            def comments = row[7].contents
            def person = personMapByFullName[row[1].contents]
            if (isEmptyComments(comments) || person == null) {
                return
            }
            def user = getUserByShortName(row[8].contents)
            def date = dateFormatter.parse(row[5].contents)
            personService.addCommentByUser(user, person, comments, date)
            person.save(flush: true, validate: false)
        }
    }

    private boolean isEmptyComments(comments) {
        StringUtils.isBlank(comments) || comments == COMMENT_TO_IGNORE
    }

    private def readSuccessCandidates() {
        forEachRowsOnSheet("Документы-ПриемНаРаботу") {row ->
            def person = personMapByFullName[row[1].contents]
            if (person == null) {
                log.warn("Person with name ${row[1].contents} is unknown! line: ${row[1].row}")
                return
            }
            def comments = row[7].contents
            if (isEmptyComments(comments)) {
                comments = null
            }
            def user = getUserByShortName(row[8].contents)
            if (user == null) {
                user = UserContextHolder.contextUser
            }
            def date = dateFormatter.parse(row[5].contents)
            def stageSuccess = stageMapping["Принят на работу"]
            if (person.stage.id == stageSuccess.id) {
                personService.addCommentByUser(user, person, comments, date)
            } else {
                personService.setStage(user, person, stageSuccess, comments, date, false)
            }
            person.save(flush: true, validate: false)
        }
    }

    private def forEachRowsOnSheet(String sheetName, Closure closure) {
        log.debug("Trying open sheet Sheets[$sheetName] ...")
        Sheet sheet = book.getSheet(sheetName)
        log.debug("Sheets[$sheetName] is opened.")
        for (int rowIndex = 1; rowIndex < sheet.getRows(); rowIndex++) {
            Cell[] row = sheet.getRow(rowIndex)
            if (log.isDebugEnabled()) {
                log.debug("Sheets[$sheetName].Rows[$rowIndex] is gotten. Trying to process it...")
            }
            closure.call(row)
            log.debug("Sheets[$sheetName].Row[$rowIndex] is complete.")
        }
        log.debug("Sheets[$sheetName] is complete.")
    }
}
