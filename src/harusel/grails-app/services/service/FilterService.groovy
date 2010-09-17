package service

import domain.filter.FilterAction
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.transaction.annotation.Transactional
import personFilters.FilterConstructor
import security.UserContextHolder

public class FilterService {

    private static Map<String, Closure> filtersCache = Collections.synchronizedMap(new HashMap<String, Closure>())

    AppUserService appUserService

    Closure getCriteria(long filterId) {
        log.debug("Requesting criteria for filter ${filterId}")
        def filter = FilterAction.get(filterId)
        assert filter != null: "Cannot find filter for filterId = ${filterId}"
        return parseFilterCriteria(filter)
    }

    private Closure parseFilterCriteria(filter) {
        assert filter: "Filter is null"
        final Closure closureFromCache = filtersCache[filter.query]
        if (closureFromCache) {
            return closureFromCache
        }
        String configDefinition = "filter = { ${filter.query} }"
        def config = new ConfigSlurper().parse(configDefinition);
        filtersCache[filter.query] = config.filter
        return config.filter
    }


    @Transactional(readOnly = true)
    def getFiltersList() {

        def filterConstructors = ConfigurationHolder.config.personFilters.collect { new FilterConstructor(it) }

        def vacancyList = appUserService.getAppUserVacancies(UserContextHolder.contextUser).sort({it.name})

        List result = filterConstructors.collect {filterConstructor ->
            new Expando(
                id: filterConstructor.filterId,
                name: filterConstructor.filterName,
                unreadPersonCount: filterConstructor.unreadPersonCount,
                vacancyList: vacancyList
            )
        }

        return result
    }


}
