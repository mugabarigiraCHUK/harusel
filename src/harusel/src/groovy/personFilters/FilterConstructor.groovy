package personFilters

import domain.person.Person
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * This class group persons by their vacancy with filtering by their stage
 */
class FilterConstructor {
    private static final Log log = LogFactory.getLog(FilterConstructor.class)
    /**
     * Filter action id related with given constructor
     */
    private def filterId

    /**
     * Filter Name
     */
    private def filterName

    /**
     * filter person's stages to include in filter
     */
    private def includedStageIdList
    /**
     * Set of vacancies of persons which are satisfy filter
     */
    private Set vacancies = [] as Set
    /**
     * How much unread persons was included by filter
     */
    private long unreadPersonCount

    public FilterConstructor(def filterDefinition) {
        this.filterId = filterDefinition.id
        this.filterName = filterDefinition.name
        this.includedStageIdList = filterDefinition.includedStageIdList
    }

    /**
     * Go person through filter and return true if it was included
     */
    public boolean process(Person person, boolean isUnread) {
        log.debug("Processing person with stage = ${person.stage.id}")
        if (includedStageIdList.contains(person.stage.id)) {
            vacancies.addAll(person.vacancies)
            if (isUnread) {
                log.debug("Incrementing unread person count")
                unreadPersonCount++
            }
            log.debug("\t\tUnread person count is ${unreadPersonCount}")
            return true
        }
        log.debug("\t\tUnread person count is ${unreadPersonCount}")
        return false
    }

    /**
     * How much unreaded persons was included by filter
     */
    public long getUnreadPersonCount() {
        return unreadPersonCount
    }

    /**
     * Set of vacancies of persons which are satisfy filter
     */
    public Set getVacancies() {
        return vacancies
    }

    /**
     * Filter Action id related with given constructor
     */
    public def getFilterId() {
        return filterId
    }

    public def getFilterName() {
        return filterName
    }
}
