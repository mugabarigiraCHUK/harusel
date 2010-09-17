import domain.Criterion
import domain.PersonSource
import domain.Vacancy
import org.codehaus.groovy.grails.plugins.springsecurity.Secured

/**
 * Preferance controller
 */
@Secured(['ROLE_HR_ALLOWED'])
class PreferencesController {
    def criterionService
    def appUserService

    /**
     * Renders main page with tabs
     */
    def index = {
        def selectedTab = params.tab ?: 0;
        [tab: selectedTab];
    }

    /**
     * Tab vacancy
     */
    def vacancy = {
        [vacancyList: Vacancy.list()]
    }

    /**
     * Assigning manager on vacancy dialog
     */
    def reviewersList = {
        [reviewersList: appUserService.getProjectManagers()]
    }

    /**
     * Tab vacancy criterion
     */
    def criteria = {
        final def criteriaList = Criterion.withCriteria {
            isNull('parent')
            order('childIndex', 'asc')
        }
        [criteriaList: criteriaList]
    }

    /**
     * Tab source
     */
    def source = {
        [sourceList: PersonSource.list()]
    }

    @Deprecated
    def menu = { }

}
