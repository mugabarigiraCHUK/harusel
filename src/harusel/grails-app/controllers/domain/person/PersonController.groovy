package domain.person

import common.PrettyTime
import domain.Stage
import domain.Vacancy
import domain.event.CreateEvent
import domain.event.GenericDataEvent
import domain.event.PersonEvent
import grails.converters.JSON
import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.AuthorizeTools
import org.codehaus.groovy.grails.plugins.springsecurity.Secured
import org.springframework.context.i18n.LocaleContextHolder
import security.UserContextHolder
import service.CriterionService
import service.FilterService
import service.NotificationService
import service.PersonService

@Secured(['ROLE_HR_ALLOWED', 'ROLE_PM_ALLOWED'])
class PersonController {
    public static final String NEW_PERSON_STAGE_CODE = 'newPerson'

    /**
     * Default size of person list.
     */
    private static final int DEFAULT_PERSON_LIST_SIZE = 15

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    CriterionService criterionService
    FilterService filterService
    PersonService personService
    NotificationService notificationService

    def messageSource
    def sessionFactory

    def index = { redirect(action: filter, params: params) }

    def save = {
        params.emails = fixEmails(params.emails)
        def person = new Person(params)
        if (!person?.source?.id) {
            person.source = null
        }
        request.getParameterValues('vacancies.id').each {vacancyId ->
            if (vacancyId) {
                def vacancy = vacancyId as Long
                person.addToVacancies(Vacancy.get(vacancy))
            }
        }

        person.validate()
        if (!person.hasErrors() && person.save()) {
            new CreateEvent(person: person, user: UserContextHolder.contextUser).save()
            flash.message = message(code: "common.label.updated")
            session.newPerson = null

            render(view: "generic", model: [person: person])
        }
        else {
            render(view: 'generic', model: [person: person])
        }
    }

    def generic = {
        def person = Person.get(params.id)
        ReadFlag.withTransaction {
            ReadFlag.setFor(person, UserContextHolder.contextUser)
        }
        render(view: "person", model: [person: person])
    }

    def view = {
        def person = Person.get(params.id)
        ReadFlag.withTransaction {
            ReadFlag.setFor(person, UserContextHolder.contextUser)
        }

        def filterDetails = filterService.getFiltersList();
        def selectMsg = messageSource.getMessage("home.operations.select", null,
            LocaleContextHolder.locale)
        def removeMsg = messageSource.getMessage("home.operations.remove", null,
            LocaleContextHolder.locale)
        def subscribeMsg = messageSource.getMessage("home.operations.subscribe", null,
            LocaleContextHolder.locale)

        // CR: major dkranchev 02-Mar-2010 This map should be inversed. code -> view.
        def candidateOperations = [
            "$selectMsg": "",
            "$removeMsg": "remove",
            "$subscribeMsg": "subscribe"
        ]

        def model = [
            filters: filterDetails, candidateOperations: candidateOperations,
            toolbarButtons: ConfigurationHolder.config.toolbarButtons,
            person: person
        ]

        render(view: "view", model: model)
    }

    def timeline = {
        Person person = Person.get(params.id)
        assert person != null: "Can't find person id=${params.id}"

        def pageStep = ConfigurationHolder.config.person.timeline.events.count.step
        params.max = params.max ? params.max.toInteger() : pageStep
        def personEvents = PersonEvent.withCriteria {
            eq("person.id", person.id)
            order("date", "desc")
        }

        def currentSize = Math.min(params.max, personEvents.size());

        def events = personEvents.subList(0, currentSize)
        render(view: "timeline", model: [
            person: person,
            eventsTotalCount: personEvents.size(),
            pageStep: pageStep,
            currentSize: currentSize,
            max: params.max,
            events: events,
            prettyTime: new PrettyTime(),
        ])

    }


    def notes = {
        Person personInstance = Person.get(params.id)
        def notes = personInstance?.notes?.sort {it.date.time}?.reverse()
        render(view: "notes", model: [person: personInstance, notes: notes, prettyTime: new PrettyTime()])
    }

    def add = {
        final Stage stageNewPerson = Stage.findByCodeName(NEW_PERSON_STAGE_CODE, [cache: true])
        Person person = new Person(stage: stageNewPerson)
        person.properties = params
        session.newPerson = person
        render(view: "person", model: [person: person])
    }

    def cancel = {
        flash.message = message(code: "common.label.canceled")
        redirect(action: edit, params: params)
    }


    def edit = {
        def person = Person.get(params.id)
        person = person ? person : session.newPerson
        if (!person) {
            flash.message = message(code: "common.label.notFound")
            redirect(action: filter)
        }
        else {
            render(view: "generic", model: [person: person])
        }
    }

    def update = {
        def person = Person.get(params.id)
        if (person) {
            if (params.version) {
                def version = params.version.toLong()
                if (person.version > version) {
                    person.errors.rejectValue(
                        "version",
                        "person.optimistic.locking.failure",
                        "Another user has updated this Person while you were editing.")
                    render(view: "generic", model: [person: person])
                    return
                }
            }
            def personPropertiesBeforeUpdate = person.copyProperies()
            person.vacancies.clear()
            params.emails = fixEmails(params.emails)
            person.properties = params
            request.getParameterValues('vacancies.id').each {vacancyId ->
                if (vacancyId) {
                    def vacancy = vacancyId as Long
                    person.addToVacancies(Vacancy.get(vacancy))
                }
            }
            if (!person.source?.id) {
                person.source = null
            }
            if (!person.hasErrors() && person.save()) {
                if (!params.trivialChanges) {
                    def changes = person.getUpdatedProperties(personPropertiesBeforeUpdate)
                    if (changes) {
                        def event = new GenericDataEvent(person: person, user: UserContextHolder.contextUser);
                        changes.each {changedProperty ->
                            event.addToChangedProperties(changedProperty)
                        }
                        event.save()

                        try {
                            String applicationURL = buildApplicationURL(request)

                            notificationService.sendNotification(event, applicationURL)
                        } catch (Exception e) {
                            log.warn("Cannot send notification: " + e.getMessage(), e);
                        }

                    }

                }
                flash.message = message(code: "common.label.updated")
            }
            render(view: "generic", model: [person: person])
        } else {
            // TODO: ???
            flash.message = message(code: "common.label.notFound")
            redirect(controller: "person", action: "filter")
        }
    }

    private def buildApplicationURL(HttpServletRequest request) {
        request.scheme + "://" + request.serverName + ":" + request.serverPort + request.contextPath
    }

    def unsubscribe = {

        def person = Person.get(params.id)
        ReadFlag.withTransaction {
            ReadFlag.setFor(person, UserContextHolder.contextUser)
        }

        def filterDetails = filterService.getFiltersList()
        def selectMsg = messageSource.getMessage("home.operations.select", null,
            LocaleContextHolder.locale)
        def removeMsg = messageSource.getMessage("home.operations.remove", null,
            LocaleContextHolder.locale)
        def subscribeMsg = messageSource.getMessage("home.operations.subscribe", null,
            LocaleContextHolder.locale)

        // CR: major dkranchev 02-Mar-2010 This map should be inversed. code -> view.
        def candidateOperations = [
            "$selectMsg": "",
            "$removeMsg": "remove",
            "$subscribeMsg": "subscribe"
        ]

        def model = [
            filters: filterDetails, candidateOperations: candidateOperations,
            toolbarButtons: ConfigurationHolder.config.toolbarButtons,
            person: person
        ]

        personService.unsubscribe(UserContextHolder.contextUser, person.id)

        render(view: "unsubscribe", model: model)
    }

    def filter = {
        params.max = Math.min(params.max?.isInteger() ? params.max.toInteger() : DEFAULT_PERSON_LIST_SIZE, DEFAULT_PERSON_LIST_SIZE)
        params.offset = params.offset?.isInteger() ? params.offset.toInteger() : 0;

        def criteriaId = params.id.toLong()
        def model = personService.getPersons(criteriaId, params.vacancyId?.isLong() ? params.vacancyId.toLong() : null, params)

        render(view: "list", model: [personList: model.personList, personListSize: model.totalCount,
            filterId: params.id, params: params])
    }

    /**
     * Renders dialog for change person stage by HR
     */
    def directChangeStageDialog = {
        [command: new ChangeStageCommand()]
    }

    /**
     * Dialog for make decisions (PM) or change stage (HR) - 6 toolbar buttons
     */
    def changeStateNoteDialog = {
        def personList = Person.getAll(request.getParameterValues("personIdList")*.toLong())
        [unsubscribedReviewers: personService.getUnsubscribedObserversForAll(personList), command: new ChangeStageCommand()]
    }

    /**
     * This method process setting of stage by HR and saving decisions was made by PM
     */
    def setStage = {ChangeStageCommand stageCommand ->
        stageCommand.personIdList = request.getParameterValues("personIdList")*.toLong()
        stageCommand.userIdListToSubscribe = request.getParameterValues("userIdListToSubscribe")*.toLong()
        def personList = Person.getAll(stageCommand.personIdList)
        if (stageCommand.hasErrors()) {
//            render view: stageCommand.dialogViewName, model: [command: stageCommand,
//                unsubscribedReviewers: personService.getUnsubscribedObserversForAll(personList),]
            def errorObject = new HashMap()
            errorObject.errors = []
            stageCommand.errors.each {
                errorObject.errors << message(code: "changeStageCommand." + it.fieldError.field + "." + it.fieldError.code)
            }
            return render(errorObject as JSON)
        }
        def targetStage = findTargetStage(stageCommand)
        Date date = new Date()
        def events = []

        // CR: major dkranchev 02-Mar-2010 Create class with constants for all roles.
        if (AuthorizeTools.ifNotGranted("ROLE_SET_STAGE")) {
            // decision was made
            personList.each {person ->
                events << personService.saveDecision(UserContextHolder.contextUser, person, targetStage, stageCommand.comment, date)
            }
            targetStage = personList.first().stage
        } else {
            // HR set new stage!
            personList.each {person ->
                events << personService.setStage(UserContextHolder.contextUser, person, targetStage, stageCommand.comment, date, stageCommand.trackDecisions)
            }
        }
        if (!stageCommand.trivialChanges) {
            events.each {event -> notificationService.sendNotification(event) }
        }
        // TODO: checker - compose Email
        // TODO: checker - unsubscribe
        // TODO: checker - subscribe all
        // TODO: checker - subscribe specified users
        // CR: major dkranchev 02-Mar-2010 non localizable, non user-friendly.


        render wrapTargetStage(targetStage) as JSON
    }

    private def wrapTargetStage(Stage targetStage) {
        def object = new HashMap()
        object.id = targetStage.id
        object.name = message(code: "stage.${targetStage.codeName}")
        return object
    }

    private def findTargetStage(ChangeStageCommand stageCommand) {
        def stageId;
        if (stageCommand.actionName) {
            stageId = ConfigurationHolder.config.toolbarButtons.find { it.actionName == stageCommand.actionName }?.targetStage
        } else if (stageCommand.stageId) {
            stageId = stageCommand.stageId
        }
        if (!stageId) {
            throw new IllegalArgumentException("Can't recognise target stage");
        }

        return Stage.get(stageId as Long)
    }

    private String fixEmails(String emails) {
        return emails.replace(",", ";").replace(" ", ";").replaceAll(/;+/, ";").split(";").join("; ")
    }

}
