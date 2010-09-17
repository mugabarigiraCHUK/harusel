package service

import domain.Criterion
import domain.PersonSource
import domain.Stage
import domain.Vacancy
import domain.VacancyCriterion
import domain.event.CreateEvent
import domain.event.DocumentAddedEvent
import domain.event.DocumentRemovedEvent
import domain.event.MakeDecisionEvent
import domain.event.NoteEvent
import domain.event.ScoreSheetEvent
import domain.event.StageChangedEvent
import domain.filter.FilterAction
import domain.person.Document
import domain.person.DocumentType
import domain.person.Note
import domain.person.Person
import domain.person.ScorePoint
import domain.person.ScoreSheet
import domain.user.AppUser
import domain.user.Role
import domain.user.RoleRight
import org.apache.commons.lang.math.RandomUtils
import org.apache.commons.lang.time.DateUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import performanceReview.Answer
import performanceReview.Form
import performanceReview.Question
import performanceReview.Template

class InitDataService {
    def perspectives

    def authenticateService
    AppUserService appUserService

    def sessionFactory


    def rights = [:]
    def roles = [:]
    def criteria = []
    def vacancies = [:]
    def sources = [:]
    def stages = [:]
    def documentTypes = [:]
    def reviewQuestions = [:]

    /**
     * If application runs first time, populates database by init data.
     */
    def ensureDataInitialized() {
        initProductionData()
        initToolbar()
//        appUserService.importUsers()
        sessionFactory.getCurrentSession().flush();

//        if (Environment.current != Environment.PRODUCTION || ConfigurationHolder.config.createDevelopmentData) {
//            initNonProductionData()
//            if (ConfigurationHolder.config.createDummyPersons) {
//                createDummyPersons();
//            }
//            sessionFactory.getCurrentSession().flush();
//        }
    }

    private def initToolbar() {
        ConfigurationHolder.config.toolbarButtons.each {button ->
            def stagesId = []
            button.disabledOnStages.each {stageCodeName ->
                def stage = stages[stageCodeName]
                if (!stage) {
                    log.error("Can't find stage with key $stageCodeName")
                    return
                }
                stagesId << stage.id
            }
            button.disabledOnStages = stagesId
            def stage = stages[button.targetStage];
            if (!stage) {
                log.error("Can't find stage with key ${button.targetStage}")
                return
            }
            button.targetStage = stage.id
        }
    }
    /**
     * Adds required configuration to database.
     */
    def initProductionData() {
        log.info('Initializing production data')
        initRoles()
        createSystemUsers()
        initVacancyCriteriaTree()
        initVacancies()
        initSources()
        initStages()
        initPersonFilters()
        initDocTypes()
        initPerformanceReviewQuestions()
        log.info('Initializing production data complete')
    }

    /**
     * Creates performance review questions if DB is empty
     */
    private def initPerformanceReviewQuestions() {
    }

    /**
     * create doc types
     */
    private def initDocTypes() {
        ConfigurationHolder.config.docTypes.each {type ->
            def theType = DocumentType.findByTypeCode(type.typeCode)
            if (!theType) {
                theType = new DocumentType(name: type.typeCode, typeCode: type.typeCode, privileged: true)
                theType.save()
                if (theType.hasErrors()) {
                    log.error("Can't create document type ${type.typeCode}")
                    return
                }
            }
            documentTypes[type.typeCode] = theType;
        }
    }

    /*
    * create filters for person list
    */

    private def initPersonFilters() {
        log.debug("Deleting old filters...");
//        FilterAction.executeUpdate("delete FilterAction")
        log.debug("Creating new filters...");
        ConfigurationHolder.config.personFilters.each {filterDefinition ->
            filterDefinition.includedStageIdList = []
            def query = "stage in("
            query += filterDefinition.includedStageKeys.collect {
                filterDefinition.includedStageIdList << stages[it].id
                def stage = stages[it]
                if (!stage) {
                    log.error("Can't find stage ${it}!");
                    return;
                }
                return String.valueOf(stage.id) + "L"
            }.join(",")
            query += ")"
            def filter = FilterAction.findByName(filterDefinition.name)
            if (!filter) {
                filter = new FilterAction(name: filterDefinition.name, description: filterDefinition.description, active: true,
                    query: query)
                if (!filter.save()) {
                    log.error("Can't create filter ${filterDefinition.name}")
                    return
                }
                log.debug("Filter '${filterDefinition.name}' was created")
            } else {
                filter.query = query
                filter.active = true
                log.debug("Filter '${filterDefinition.name}' was found and updated")
                filter.save()
            }
            filterDefinition.id = filter.id
        }
        def activeFiltersList = ConfigurationHolder.config.personFilters.findAll { it.id }.collect { it.id }

        def deactivatedFilters = FilterAction.withCriteria {
            not {
                'in'('id', activeFiltersList)
            }
        }
        deactivatedFilters.each { it.active = false }
        deactivatedFilters*.save()
    }

    private def initStages() {
        log.debug("Initializing stages...")
        ConfigurationHolder.config.stageNameMap.each {String codeName ->
            def theStage = Stage.findByCodeName(codeName)
            if (!theStage) {
                log.debug("... creating stage $codeName...");
                theStage = new Stage(codeName: codeName).save()
                if (theStage.hasErrors()) {
                    log.error("Can't create stage $codeName!")
                    return
                }
            }
            stages[codeName] = theStage
        }
        log.debug("Initializing stages is complete.")
    }

    /**
     * create default sources
     */
    private def initSources() {
        if (PersonSource.count() == 0) {
            ConfigurationHolder.config.sourcesToCreateOnEmptyDB.each {name ->
                sources[name] = new PersonSource(name: name, active: true).save()
            }
            assert sources.size() == PersonSource.count(): "Can't create PersonSource records"
        }
    }

    /**
     * Create vacancies
     */
    private def initVacancies() {
        if (Vacancy.count() == 0) {
            ConfigurationHolder.config.vacanciesToCreateOnEmptyDB.each {name ->
                def vacancy = new Vacancy(name: name, active: true)
                Criterion.list().each { vacancy.addToCriteria(new VacancyCriterion(vacancy: vacancy, criterion: it)) }
                vacancy.save()
                if (vacancy.hasErrors()) {
                    log.error("Can't save vacancy $name")
                }
                vacancies[name] = vacancy
            }
        }
    }

    // create criteria

    private def initVacancyCriteriaTree() {
    }

    private def createSystemUsers() {
        def users = [
            new AppUser(login: 'application', fullName: 'Application', email: ''),
        ].findAll { !AppUser.findByLogin(it.login) };
        saveWithErrorDump(users)
    }

    private def initRoles() {
        // TODO: should we remove obsolete roles&rights or not??
        initRights()
        log.debug("Initializing user's roles...");
        ConfigurationHolder.config.userRolesDescription.each {roleName, entry ->
            Role theRole = Role.findByName(roleName)
            if (!theRole) {
                theRole = new Role(name: roleName, description: entry.description, ldapName: entry.ldapName);
            } else {
                theRole.ldapName = entry.ldapName
                theRole.description = entry.description
            }
            installRoleRights(theRole, authenticateService.securityConfig.security.rules[roleName])
            theRole.save();
            if (theRole.hasErrors()) {
                log.error("Can't create role ${roleName}")
                return;
            } else {
                log.debug("Role ${roleName} had been created successfully.")
            }
            entry.id = theRole.id
            roles[roleName] = theRole;
        }
        log.debug("Roles initializing is complete.")
    }

    private def initRights() {
        log.debug("Initializing rights...")
        authenticateService.securityConfig.security.authorities.each {authority, name ->
            def roleRight = RoleRight.findByAuthority(authority)
            if (!roleRight) {
                roleRight = new RoleRight(authority: authority, name: name).save()
                if (roleRight.hasErrors()) {
                    log.error("Can't save role right: $authority")
                } else {
                    log.debug("Right $name had been created successfully.")
                }
            }
            rights[authority] = roleRight;
        }
        log.debug("Rights initializing is complete.")
    }

    /**
     * Assigns provided rights to role.
     * @param role role object
     * @param rightsList list of user rights (may be empty)
     * @return role object
     */
    private Role installRoleRights(Role role, List rightsList) {
        log.debug("Filling role ${role.name} with rights...")
        role.rights?.clear()
        if (rightsList) {
            rightsList.each {
                assert rights."$it", "Right \'$it\' does not exist"
                role.addToRights(rights."$it")
                log.debug("... add right: $it.");
            }
        }
        log.debug("Filling role ${role.name} with rights is complete.")
        role
    }

    private def createDummyPersons() {
        log.debug("Creating bummy persons...")
        def vacancy = new Vacancy(name: "test vacancy", users: appUserService.getProjectManagers(), active: true).save()
        Criterion.list().each { vacancy.addToCriteria(new VacancyCriterion(vacancy: vacancy, criterion: it)) }
        23.times {index ->
            new Person(fullName: "Иванов Иван Иванович (i$index)", emails: ConfigurationHolder.config.notification.testEmail,
                stage: stages["newPerson"], vacancies: [vacancy]).save()
            log.debug("... person $index")
        }
        log.debug("Dummy persons are created.")
    }

    /**
     * Creates dummy data to operate with at development stage.
     */
    def initNonProductionData() {
        log.info('Initializing non-production data')
        createTemplates();
        createForms();
        List persons = createTestPersons()
        addEvents(persons[0]);
        addScore(persons[0]);
        log.info('Initializing non-production data complete')
    }

    /**
     * Create person list for test purposes
     */
    private List createTestPersons() {
        def persons = [
            new Person(
                fullName: 'Сычев Руслан',
                birthDate: '1985',
                emails: ConfigurationHolder.config.notification.testEmail,
                phones: '+ 7 913 762 6727; +7 383 332 8909',
                stage: stages["newPerson"],
                vacancies: ['C/C++ developer', 'C/C++ architect', 'Java developer'].collect { vacancies[it] },
                source: sources['Иван Сухоруков'],
            ),
            new Person(
                fullName: 'User1 for mark addition tests',
                birthDate: '1985',
                emails: 'ruslan_sychev@gmail.com; syslik@ngs.ru',
                phones: '+ 7 913 762 6727; +7 383 332 8909',
                stage: stages["newPerson"],
                vacancies: ['C/C++ developer'].collect { vacancies[it] },
                source: sources['Иван Сухоруков'],
            ),
            new Person(
                fullName: 'User2 for mark addition tests',
                birthDate: '1985',
                emails: 'ruslan_sychev@gmail.com; syslik@ngs.ru',
                phones: '+ 7 913 762 6727; +7 383 332 8909',
                stage: stages["newPerson"],
                vacancies: ['C/C++ developer'].collect { vacancies[it] },
                source: sources['Иван Сухоруков'],
            ),
            new Person(
                fullName: 'Иванов Иван Иванович', birthDate: '1982', emails: 'iii@gmail.com', phones: '+ 7 923 124 2236',
                stage: stages["newPerson"], vacancies: vacancies['Manager'], source: sources['NGS']),
            new Person(
                fullName: 'Иванов Иван Петрович', birthDate: '1965', emails: 'iip@gmail.com', phones: '+ 7 923 745 2521',
                stage: stages["newPerson"], vacancies: vacancies['Manager'], source: sources['Мой круг']),
            new Person(
                fullName: 'Иванов Петр Иванович', birthDate: '1901', emails: 'ipi@gmail.com', phones: '+ 7 906 833 7532',
                stage: stages["newPerson"], vacancies: vacancies['Manager'], source: sources['Иван Сухоруков']),
            new Person(
                fullName: 'Иванов Петр Петрович', birthDate: '1812', emails: 'ipp@gmail.com', phones: '+ 7 923 123 7522',
                stage: stages["newPerson"], vacancies: vacancies['Manager'], source: sources['e-Rabota']),
            new Person(
                fullName: 'Петров Иван Иванович', birthDate: '0001', emails: 'pii@gmail.com', phones: '+ 7 913 136 2473',
                stage: stages["newPerson"], vacancies: vacancies['Manager'], source: sources['NGS']),
            new Person(
                fullName: 'Петров Иван Петрович', birthDate: '1234', emails: 'pip@gmail.com', phones: '+ 7 903 227 1233',
                stage: stages["newPerson"], vacancies: vacancies['Manager'], source: sources['Мой круг']),
            new Person(
                fullName: 'Петров Петр Иванович', birthDate: '2009', emails: 'ppi@gmail.com', phones: '+ 7 906 865 8833',
                stage: stages["newPerson"], vacancies: vacancies['Manager'], source: sources['Иван Сухоруков']),
            new Person(
                fullName: 'Петров Петр Петрович', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["newPerson"], vacancies: vacancies['Manager'], source: sources['e-Rabota']),

            new Person(
                fullName: 'user_for_enableParallelReviewTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["newPerson"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user_for_openingUnreadPersonTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["newPerson"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user_for_evaluationChangePossibilityTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["newPerson"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user_for_changingApplicantStageTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["newPerson"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user1_for_buttonsToChangeStageTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["newPerson"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user_for_addCommentTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["newPerson"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user2_for_buttonsToChangeStageTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["resumeWaiting"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user4_for_buttonsToChangeStageTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["questionnaireWaiting"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user5_for_buttonsToChangeStageTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["resumeReading"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user4_for_actionOfButtonsToChangeStateTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["resumeReading"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user6_for_buttonsToChangeStageTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["interview"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user7_for_buttonsToChangeStageTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["repeatedInterview"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user8_for_buttonsToChangeStageTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["infoWaiting"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user1_for_actionOfButtonsToChangeStateTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["resumeReading"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user2_for_actionOfButtonsToChangeStateTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["resumeReading"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user3_for_actionOfButtonsToChangeStateTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["resumeReading"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user3_for_buttonsToChangeStageTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["resumeReading"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user5_for_actionOfButtonsToChangeStateTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["answerWaiting"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
            new Person(
                fullName: 'user6_for_actionOfButtonsToChangeStateTest', birthDate: '2000', emails: 'ppp@gmail.com', phones: '+ 7 913 209 1326',
                stage: stages["companyDecisionWaiting"], vacancies: vacancies['Tester'], source: sources['e-Rabota']),
        ]

        persons.each {
            it.validate();
            if (it.hasErrors()) {
                System.out.println(it.errors);
            }
            it.save();
        }
        // end create dummy persons
        return persons
    }

    private def saveWithErrorDump(def entities) {
        if (!entities?.size()) {
            return
        }
        entities.each {
            if (!it.save()) {
                System.out.println(it.errors);
            }
        }
        def entityClass = entities[0].getClass();
        def savedEntitiesCount = entityClass.count();
        assert entities.size() == savedEntitiesCount: "Can't create ${entityClass} records (expected = ${entities.size()}, real = ${savedEntitiesCount}}"
    }

    private def addEvents(def person) {
        AppUser testpm1 = AppUser.findByLogin("testpm1");
        AppUser testpm2 = AppUser.findByLogin("testpm2");
        AppUser testhr1 = AppUser.findByLogin("testhr1");

        def documents = [
            new Document(
                date: new Date(),
                name: "document1.txt",
                type: documentTypes['questionnaire'],
                data: "data".bytes,
                user: testpm1,
                person: person),
            new Document(
                date: new Date(),
                name: "+загрузи меня+.txt",
                type: documentTypes['resumeRu'],
                data: "data".bytes,
                user: testhr1,
                person: person),
            new Document(
                date: new Date(),
                name: "document2.txt",
                type: documentTypes['resumeEn'],
                data: "data".bytes,
                user: testpm1,
                person: person)]
        documents.each {doc ->
            doc.save()
            if (doc.hasErrors()) {
                log.error("Can't save doc ${doc.name}")
                return
            }

        }
        person.refresh();
        def notes = [
            new Note(date: new Date(), text: "note text 1", user: testpm1, person: person).save(),
            new Note(date: new Date(), text: "big note text 2 his snippet shows the different types of strings in Groovy and plays with them. Listen to the audio to get the", user: testpm1, person: person).save(),
            new Note(date: new Date(), text: "some comment from eko", user: testpm2, person: person).save(),
            new Note(date: new Date(), text: "some comment from maskensky", user: testpm2, person: person).save(),
            new Note(date: new Date(), text: "some comment from yuki", user: testhr1, person: person).save(),
        ]
        notes.each {note ->
            person.addToNotes(note)
        }
        def sheets = [//new ScoreSheet(name: "score 1", date: new Date(), comment: "some comment 1", testpm1: testpm1, person: person),
            new ScoreSheet(name: "score 2", date: new Date(), comment: "some comment 2", user: testpm1, person: person,)];
        sheets.each {sheet ->
            sheet.save();
            if (sheet.hasErrors()) {
                log.error("Can't save sheet ${sheet.name}")
            }
        }
        ScorePoint point = new ScorePoint(value: 1, comment: testpm1.hashCode() + "comment ", score: sheets[0], criterion: Criterion.get(1));
        point.save();
        def stages = Stage.list(max: 2);
        [
            new CreateEvent(date: new Date(), user: testpm1, person: person),
            new DocumentAddedEvent(date: new Date(), user: testpm1, person: person,
                document: documents[0]
            ),
            new DocumentRemovedEvent(date: new Date(), user: testpm1, person: person,
                document: documents[1]
            ),
            new NoteEvent(date: new Date(), user: testpm1, person: person, note: notes[0]),
            new NoteEvent(date: new Date(), user: testpm1, person: person, note: notes[1]),
            new NoteEvent(date: new Date(), user: testpm2, person: person, note: notes[2]),
            new NoteEvent(date: new Date(), user: testpm2, person: person, note: notes[3]),
            new NoteEvent(date: new Date(), user: testhr1, person: person, note: notes[4]),
            new ScoreSheetEvent(date: new Date(), user: testpm1, person: person, score: sheets[0]),
            new StageChangedEvent(date: new Date(), user: testpm1, person: person, comment: "some coment today", to: stages[0], from: stages[1]),
            new StageChangedEvent(date: DateUtils.addHours(new Date(), -1), user: testpm1, person: person, comment: "some coment today -1 Hours", to: stages[0], from: stages[1]),
            new StageChangedEvent(date: DateUtils.addHours(new Date(), -2), user: testpm1, person: person, comment: "some coment today -2 Hours", to: stages[1], from: stages[0]),
            new StageChangedEvent(date: DateUtils.addHours(new Date(), -3), user: testpm1, person: person, comment: "some coment today -3 Hours", to: stages[0], from: stages[1]),
            new StageChangedEvent(date: DateUtils.addHours(new Date(), -4), user: testpm1, person: person, comment: "some coment today -4 Hours", to: stages[1], from: stages[0]),
            new StageChangedEvent(date: DateUtils.addHours(new Date(), -5), user: testpm1, person: person, comment: "some coment today -5 Hours", to: stages[0], from: stages[1]),
            new StageChangedEvent(date: DateUtils.addDays(new Date(), -1), user: testpm1, person: person, comment: "some coment esterday", to: stages[1], from: stages[0]),
            new StageChangedEvent(date: DateUtils.addDays(new Date(), -2), user: testpm1, person: person, comment: "some coment 2 day ago", to: stages[0], from: stages[1]),
            new StageChangedEvent(date: DateUtils.addDays(new Date(), -3), user: testpm1, person: person, comment: "some coment 3 day ago", to: stages[1], from: stages[0]),
            new StageChangedEvent(date: DateUtils.addDays(new Date(), -4), user: testpm1, person: person, comment: "some coment 4 day ago", to: stages[0], from: stages[1]),
            new StageChangedEvent(date: DateUtils.addDays(new Date(), -7), user: testpm1, person: person, comment: "some coment last weak", to: stages[1], from: stages[0]),
            new StageChangedEvent(date: DateUtils.addDays(new Date(), -14), user: testpm1, person: person, comment: "some coment 2 weak ago", to: stages[0], from: stages[1]),
            new StageChangedEvent(date: DateUtils.addDays(new Date(), -21), user: testpm1, person: person, comment: "some coment 3 weak ago", to: stages[1], from: stages[0]),
            new StageChangedEvent(date: DateUtils.addMonths(new Date(), -1), user: testpm1, person: person, comment: "some coment last monts", to: stages[0], from: stages[1]),
            new StageChangedEvent(date: DateUtils.addMonths(new Date(), -3), user: testpm1, person: person, comment: "some coment 2 monts ago", to: stages[1], from: stages[0]),
            new StageChangedEvent(date: DateUtils.addMonths(new Date(), -2), user: testpm1, person: person, comment: "some coment 3 monts ago", to: stages[0], from: stages[1]),
            new StageChangedEvent(date: DateUtils.addMonths(new Date(), -5), user: testpm1, person: person, comment: "some coment older", to: stages[1], from: stages[0]),

            new MakeDecisionEvent(date: DateUtils.addHours(new Date(), -1), user: testpm1, person: person, comment: "some new decision comment. ${'long ' * 30}text ...", beforeDecisionStage: stages[1], decidedStage: stages[0]),
            new MakeDecisionEvent(date: DateUtils.addHours(new Date(), -2), user: testpm1, person: person, comment: "some old decision comment. ${'long ' * 30}text ...", beforeDecisionStage: stages[0], decidedStage: stages[1]),
        ].each {event ->
            event.save();
            if (event.hasErrors()) {
                log.error("Can't save event ${event.class.getName()}")
            }
        }

        person.requiredDecisionsFlag = true;
        person.save();
        if (person.hasErrors()) {
            log.error("Can't save person after update [${person.fullName}]")
        }
    }

    private def addScore(def person) {
        for (AppUser user: AppUser.list()) {
            ScoreSheet scoreSheet = new ScoreSheet();
            scoreSheet.setUser(user);
            scoreSheet.setPerson(person);
            scoreSheet.setDate(new Date());
            scoreSheet.setName("Собеседование ");
            scoreSheet.setComment("total comment");
            scoreSheet.save();

            def criterions = Criterion.getAll();
            def scorePoints = [];
            criterions.each {Criterion criteria ->
                if (!criteria.isReadOnly()) {
                    ScorePoint point = new ScorePoint(value: 1, comment: user.hashCode() + "comment ${criteria.getName()}", score: scoreSheet, criterion: criteria);
                    scorePoints << point;
                    point.save();
                }
            }

            scoreSheet.save();
            person.save();
        }
    }

    private def createTemplates() {
        [
            "С++ разработчик",
            "Ява разработчик",
            "Тестировщик",
            "С# разработчик",
            "Ява архитекторы",
            "Администраторы",
            "Менеджеры",
        ].each {
            log.debug("Create PR template $it")
            Template template = new Template(name: it, questions: Question.findAllByParentIsNotNull([cache: true]),).save()
        }
    }

    def createForms() {
    }
}
