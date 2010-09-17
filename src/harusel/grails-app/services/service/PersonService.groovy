package service

import common.BeanNestedPropertiesForStrSubstitutor
import domain.Stage
import domain.Vacancy
import domain.event.DocumentAddedEvent
import domain.event.DocumentRemovedEvent
import domain.event.MakeDecisionEvent
import domain.event.NoteEvent
import domain.event.PersonEvent
import domain.event.StageChangedEvent
import domain.filter.FilterAction
import domain.person.Document
import domain.person.Note
import domain.person.Person
import domain.person.PersonObserver
import domain.person.ReadFlag
import domain.user.AppUser
import org.apache.commons.io.FilenameUtils
import org.apache.log4j.Logger
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.transaction.annotation.Transactional
import security.UserContextHolder
import util.SecurityTools

class PersonService {
    static Logger log = Logger.getLogger(PersonService.class)

    private static final int USE_RELAXED_SEARCH_LIMIT = 5

    MessageSource messageSource
    FilterService filterService

    /**
     * Helper method for addCommentByUser
     */
    def addComment(def personId, def noteText) {
        def user = UserContextHolder.contextUser
        def person = Person.get(personId);
        addCommentByUser(user, person, noteText, new Date())
    }

    /**
     * This method does neccessary steps on adding note to person
     */
    def addCommentByUser(AppUser user, Person person, String comment, Date date) {
        def note = new Note(
            date: date,
            text: comment,
            person: person,
            user: user,
        )
        note.save()
        person.refresh()
        new NoteEvent(note: note, user: user, person: person, date: date).save()
    }

    /**
     * Change stage for person with defined entities
     */
    def setStage(AppUser user, Person person, Stage newStage, String comment, Date date, boolean trackDecisions) {
        def oldStage = person.stage
        if (oldStage.id == newStage.id) {
            log.warn("The stage is repeated!")
        } else {
            person.stage = newStage
            person.save()
        }
        def event = new StageChangedEvent(user: user, person: person, date: date, from: oldStage, to: newStage, comment: comment)
        event.save()
        person.requiredDecisionsFlag = trackDecisions;
        person.save(flush: true);

        return event
    }

    /**
     * Removes binary data, marks document as removed.
     */
    def removeDocument(def person, def document) {
        if (document.removed) {
            return true
        }
        document.removed = true
        document.data = null
        new DocumentRemovedEvent(person: person, document: document).save()
    }

    /**
     * Save document and fill error's list on exceptions. Returns new event
     */
    def addDocument(def person, def documentFile, def documentType, def errors) {
        def document = new Document(
            user: UserContextHolder.contextUser,
            date: new Date(),
            type: documentType,
            name: FilenameUtils.getName(documentFile.originalFilename),
            person: person,
            data: documentFile.bytes)
        if (!document.save()) {
            log.debug("Can't save file in DB: ${document.errors}")
            document.errors.each {
                log.error "Error on document save: ${it}"
            }
            errors << message(code: 'document.save.error')
            return null
        }
        person.refresh();
        log.debug("Document is added successfully")
        return new DocumentAddedEvent(person: person, user: document.user, document: document, date: document.date).save();
    }

    /**
     * Add initialized document instance to person.
     */
    def addDocumentByUser(AppUser user, Person person, Document document, Date date) {
        if (log.isDebugEnabled()) {
            log.debug("Adding file ${document} to ${person.fullName}...")
        }
        document.person = person;
        document.save();
        person.refresh()
    }

    /**
     *   generate event about decision.
     */
    def saveDecision(AppUser user, Person person, Stage newStage,
        String comment, Date date) {

        def event = new MakeDecisionEvent(
            comment: comment,
            user: user,
            person: person,
            date: date,
            beforeDecisionStage: person.stage,
            decidedStage: newStage
        )
        event.save();

        return event
    }

    /**
     * Creates model for person list rendering. Takes a list person entities as parameter,
     * returns list of Expando objects with following attributes: id, stage, fullName, unread, docs, comments.
     */
    @Transactional(readOnly = true)
    def createListModel(def personList) {
        if (!personList) {
            return []
        }
        List result = personList.collect {person ->
            new Expando(
                id: person.id,
                stage: person.stage,
                fullName: person.fullName,
                docs: generateDocsDescription(person),
                opinions: generateOpinionsModel(person),
            )
        }
        result.unique({ a, b -> a.id <=> b.id  })
    }

    /**
     * Return all users who had been subscribed on vacancy and person updates
     */
    Map<AppUser, NotificationReason> getAllObserversFor(person) {
        Map<AppUser, NotificationReason> observers = getVacanciesObserversFor(person).inject([:]) {map, user ->
            map[user] = NotificationReason.VACANCY
            return map
        }

        def personObservers = PersonObserver.withCriteria { eq("person.id", person.id)}
        personObservers.each {personObserver ->
            if (personObserver.ignorePersonUpdates) {
                observers.remove(personObserver.user)
            } else {
                observers[personObserver.user] = NotificationReason.DIRECT
            }
        }
        observers
    }

    /**
     * Returns observers for vacancies only
     */
    def getVacanciesObserversFor(person) {
        def observers = [] as Set
        person.vacancies.findAll {it != null}.each {Vacancy vacancy ->
            observers.addAll(vacancy.users)
        }
        observers.unique()
    }

    /**
     * Returns all users who are subscribed for vacancy but unsubscribed from given user
     */
    def getVacancyUsubscribedUsersFor(Person person) {
        def vacancyObservers = getVacanciesObserversFor(person)
        def personObservers = PersonObserver.withCriteria { eq("person.id", person.id)}
        return vacancyObservers.findAll {user ->
            personObservers.find { it.user.id == user.id && it.ignorePersonUpdates }
        }
    }

    /**
     * Finds users had been subscribed on vacancies but unsubscribed for given person list
     */
    def getUnsubscribedObserversForAll(personList) {
        def observerSet = personList.inject([] as Set) {observerSet, person ->
            observerSet.addAll(getVacancyUsubscribedUsersFor(person))
            observerSet
        }
        observerSet.sort { it.login ?: it.fullName }
    }

    /**
     * Returns list of objects [ type as String[], text ]
     */
    def generateOpinionsModel(Person person) {
        List model = PersonOpinionsCache.instance.get(person.id)
        if (model == null) {
            model = [];
            if (!person.requiredDecisionsFlag) {
                person.refresh()
                def lastEvents = PersonEvent.findByPerson(person, [max: 1, sort: "date", order: "desc", cache: true])
                if (lastEvents) {
                    model << getDescriptionForEvent(lastEvents);
                }
            } else {
                Set observers = getVacanciesObserversFor(person)
                // collect observers opinions
                model = (List) observers.sort {it.login}.inject([]) {list, observer ->
                    list << getDecisionLine(observer, person)
                    return list
                }
            }
            model.each {opinion ->
                opinion.text = opinion.text.collect {"${it}&shy;"}.join();
            }

            PersonOpinionsCache.instance.put(person.id, model)
        }
        // sort and return opinions
        return model
    }

    /**
     * Returns object [ type as String[], text ] to describe status of requested decisions for given observer.
     */
    private def getDecisionLine(AppUser observer, Person person) {
        def lastStageChangedEvent = StageChangedEvent.withCriteria {
            and {
                eq("person.id", person.id)
                eq("to.id", person.stage.id)
            }
            order "date", "asc"
            cache true
        };
        if (lastStageChangedEvent) {
            lastStageChangedEvent = lastStageChangedEvent.last();
        }

        assert lastStageChangedEvent != null

        def lastDecision = MakeDecisionEvent.withCriteria {
            and {
                eq("person.id", person.id)
                eq("user.id", observer.id)
                eq("beforeDecisionStage.id", person.stage.id)
                gt("date", lastStageChangedEvent.date)
            }
            order "date", "asc"
            cache true
        };
        if (lastDecision) {
            lastDecision = lastDecision.last();
        }

        def lastComment = getLastNoteAfter(observer, person, lastStageChangedEvent.date)

        def line = null;
        if (lastDecision) {
            if (lastComment) {
                if (lastComment.date > lastDecision.date) {
                    line = getDescriptionForEvent(lastComment);
                    line.type << "decision"
                    line.type << "comment"
                }
            } else {
                line = getDescriptionForEvent(lastDecision);
                line.type << "decision"
            }
        } else {
            if (lastComment) {
                line = getDescriptionForEvent(lastComment);
                line.type << "decisionWanted"
                line.type << "comment"
            } else {
                def messageText = messageSource.getMessage("personList.decision.is.required", [observer.login] as Object[], LocaleContextHolder.locale)
                line = new Expando(type: ["decisionWanted"], text: messageText)
            }
        }
        return line
    }

    private def getLastNoteAfter(AppUser observer, Person person, Date afterDate) {
        def noteEvents = NoteEvent.withCriteria {
            and {
                eq("person.id", person.id)
                eq("user.id", observer.id)
                gt("date", afterDate)
            }
            order "date", "asc"
        }

        if (noteEvents) {
            return noteEvents.last()
        }
        return null;
    }

/**
 * Finds i18n event description ands substitutes inners with event's properties
 */
    private def getDescriptionForEvent(PersonEvent event) {
        def messageCode = "${event.class.simpleName}.shortDescription" as String
        def messageTemplate = messageSource.getMessage(messageCode, null, LocaleContextHolder.locale);
        def text = new BeanNestedPropertiesForStrSubstitutor(event).substitute(messageTemplate);
        return new Expando(type: [], text: text)
    }

    private def generateDocsDescription(Person person) {
        return person.documents.findAll({!it.removed && it.type.privileged}).sort({it.type.id})
    }

    def delete(Long personId) {
        def person = Person.get(personId)
        if (person) {
            // Delete all person events.
            // Delete using GORM because of cascade deletion handling by GORM.
            // TODO: Check dependencies and rewrite query into plain HQL.
            PersonEvent.findAllByPerson(person)*.delete()
            // Delete read flags.
            ReadFlag.executeUpdate("DELETE FROM ReadFlag WHERE person = :person", [person: person])
            // Delete person with associated documents, etc.
            person.delete();
        }
    }

    /**
     * Unsubscribes user from notifications about given person.
     * @param user User to unsubscribe.
     * @param personId Id of person.
     * @return
     */
    def unsubscribe(AppUser user, Long personId) {
        changeSubscription(personId, user, true)
    }

    def subscribe(AppUser user, Long personId) {
        changeSubscription(personId, user, false)
    }

    private PersonObserver changeSubscription(long personId, AppUser user, boolean ignoreUpdates) {
        def person = Person.get(personId);
        PersonObserver personObserver = PersonObserver.findByUserAndPerson(user, person)
        if (!personObserver) {
            personObserver = new PersonObserver(user: user, person: person);
        }
        personObserver.setIgnorePersonUpdates(ignoreUpdates)
        personObserver.save()
    }

    @Transactional(readOnly = true)
    def getPersons(Long filterId, Long vacancyId, def paginateParams) {
        def params = [:]

        String modelQuery = "select p FROM Person as p LEFT JOIN fetch p.documents LEFT JOIN fetch p.vacancies, Vacancy v WHERE 1=1 "
        String countQuery = "select count(distinct p) FROM Person as p LEFT JOIN p.vacancies, Vacancy v WHERE 1=1 "

        String query = ""

        if (vacancyId) {
            params.vacancy = Vacancy.findById(vacancyId);
            query += " AND :vacancy in elements(p.vacancies) "
        } else {
            if (SecurityTools.isProjectManager(UserContextHolder.contextUser)) {
                query += " AND v in elements(p.vacancies) and :user in elements(v.users) "
                params.user = UserContextHolder.contextUser
            }
        }
        if (filterId) {
            FilterAction filter = FilterAction.get(filterId)
            query += (" AND p." + filter.query + " ")
        }
        query += " ORDER BY p.fullName"

        paginateParams.cache = true;
        def personList = Person.executeQuery(modelQuery + query, params, paginateParams)

        def count = Person.executeQuery(countQuery + query, params)[0]
        List personModel = personList.collect {person ->
            new Expando(
                id: person.id,
                stage: person.stage,
                fullName: person.fullName,
                docs: generateDocsDescription(person),
                opinions: generateOpinionsModel(person),
            )
        }

        return [personList: personModel, totalCount: count]
    }

    Collection<domain.person.Person> findPersons(String query, Boolean forCreate) {
        Set results = new LinkedHashSet()
        results.addAll(Person.search(query).results);

        if (results.size() < USE_RELAXED_SEARCH_LIMIT) {
            def transformedQuery = query.trim() + "*"
            String[] parts = query.split("\\s")
            if (parts.size() > 1) {
                transformedQuery = parts.join('* ')
            }
            results.addAll(Person.search(transformedQuery, [max: 1000]).results);
        }

        return results
    }


}
