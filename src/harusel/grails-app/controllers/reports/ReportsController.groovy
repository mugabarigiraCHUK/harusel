package reports

import common.DatePeriodCommand
import domain.Vacancy
import domain.event.CreateEvent
import domain.event.PersonEvent
import domain.event.StageChangedEvent
import domain.filter.FilterAction
import domain.person.Person
import org.apache.commons.lang.time.DateFormatUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.Secured
import service.FilterService

@Secured(['ROLE_HR_ALLOWED', 'ROLE_PM_ALLOWED'])
// CR: major dkranchev 02-Mar-2010 Javadoc missed.
class ReportsController {
    public static final String PERSON_FILTER_IN_PROGRESS_CODE = 'inProgress'

    FilterService filterService

    def index = {}

    /**
     * Loads vacancy statistics tab content
     */
    def vacancyStatistics = {
        [dateFromInit: new Date(System.currentTimeMillis() - (30 * 24 * 60 * 60l * 1000l))]
    }

    /**
     * Generates xml report for open vacancies: active candidates statistic
     */
    def exportVacanciesReport = {
        List<Vacancy> vacanciesList = Vacancy.findAllByActive(true, [sort: "name", cache: true])

        Map<Long, Integer> counters = new LinkedHashMap<Long, Integer>()
        Map<Long, Vacancy> vacanciesIndex = new HashMap<Long, Vacancy>()
        vacanciesList.each {
            counters[it.id] = 0
            vacanciesIndex[it.id] = it
        }

        // CR: major dkranchev 02-Mar-2010 Introduce some code.
        def inProgressFilterName = ConfigurationHolder.config.personFilters.find { it.code == PERSON_FILTER_IN_PROGRESS_CODE}?.name
        if (!inProgressFilterName) {
            log.error("Can't find person filter with code $PERSON_FILTER_IN_PROGRESS_CODE")
            throw new IllegalStateException("The in progress filter is not configured.")
        }
        FilterAction action = FilterAction.findByName(inProgressFilterName, [cache: true])
        assert action, 'Can not find query for all active candidates'

        List<Person> personList = Person.withCriteria {and filterService.getCriteria(action.id)}

        personList.each {Person person ->
            person.vacancies.each {Vacancy vacancy ->
                if (vacancy.active) {
                    counters[vacancy.id]++
                }
            }
        }

        render(contentType: "application/binary", encoding: "UTF-8") {
            vacanciesReport {
                counters.each {key, value ->
                    vacancy(
                        name: vacanciesIndex[key].name,
                        candidates: value,
                    )
                }
            }
        }
    }

    /**
     * Generates xml report for sources
     * Note: Takes into account only persons created during specified period.
     */
    // CR: major dkranchev 02-Mar-2010 Business logic should be in service.
    def exportSourcesReport = {DatePeriodCommand command ->
        List<PersonEvent> eventsList = PersonEvent.findAllByDateBetween(command.dateFrom, command.dateTo, [sort: "date", cache: true])
        List<SourceStat> statList = new LinkedList()
        Map<Long, SourceStat> statIndex = new HashMap<Long, SourceStat>()

        // CR: normal dkranchev 02-Mar-2010 Refactor statistics calculation
        eventsList.each {PersonEvent evt ->
            // resume creation
            if (evt.person.source && evt instanceof CreateEvent) {
                SourceStat stat = statIndex[evt.person.source.id]
                if (!stat) {
                    stat = new SourceStat(source: evt.person.source)
                    statList.add(stat)
                    statIndex[stat.source.id] = stat
                }
                // storing person id's to filter person evets which were not created during specified period
                stat.personEventsTrack[evt.person.id] = new PersonEvents()
            }

            if (evt.person.source && evt instanceof StageChangedEvent) {
                SourceStat stat = statIndex[evt.person.source.id]
                // only persons which were created during specified period
                if (stat && stat.containsPerson(evt.person.id)) {
                    // CR: major dkranchev 02-Mar-2010 Comparison should be done on some code.
                    // CR: normal dkranchev 02-Mar-2010 Non-localizable .
                    if ((evt as StageChangedEvent).to.codeName == 'interview') {
                        stat.personEventsTrack[evt.person.id].processing = true
                    }
                    // CR: major dkranchev 02-Mar-2010 Comparison should be done on some code.
                    // CR: normal dkranchev 02-Mar-2010 Non-localizable .
                    if ((evt as StageChangedEvent).to.codeName == 'rejected') {
                        stat.personEventsTrack[evt.person.id].rejected = true
                    }
                }
            }
        }

        statList.sort {a, b -> a.source.name.compareToIgnoreCase(b.source.name)}
        statList.each {it.calculate()}
        render(contentType: "application/binary", encoding: "UTF-8") {
            sourcesReport(
                from: DateFormatUtils.format(command.dateFrom, "dd.MM.yyyy"),
                to: DateFormatUtils.format(command.dateTo, "dd.MM.yyyy")
            ) {
                statList.each {
                    source(
                        name: it.source.name,
                        added: it.added,
                        processing: it.processing,
                        rejected: it.rejected
                    )
                }
            }
        }
    }
}
