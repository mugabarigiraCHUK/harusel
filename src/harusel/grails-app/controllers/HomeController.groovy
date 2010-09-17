import domain.person.Person
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.Secured
import org.springframework.context.i18n.LocaleContextHolder
import security.UserContextHolder

@Secured(['ROLE_HR_ALLOWED', 'ROLE_PM_ALLOWED'])
// CR: major dkranchev 02-Mar-2010 Javadoc missed.
class HomeController {
    def filterService
    def personService
    def messageSource

    def index = {
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

        def model = [filters: filterDetails, candidateOperations: candidateOperations,
            toolbarButtons: ConfigurationHolder.config.toolbarButtons
        ]

        return model
    }

    def addComment = {
        if (!StringUtils.isBlank(params.note)) {
            def personList = (params.personIdList instanceof String) ? [params.personIdList] : params.personIdList
            personList.each {personId ->
                personService.addComment(personId, params.note)
            }
        }
        // CR: major dkranchev 02-Mar-2010 Do not mess view with controller.
        render('<div class="notify">' + message(code: 'comment.added') + '</div>')
    }

    def subscribe = {
        List personsIdList = getPersonIdList(params)
        personsIdList.each {id ->
            personService.subscribe(UserContextHolder.contextUser, id.toLong())
        }
        render "ok"
    }

    private List getPersonIdList(Map params) {
        def personsIdList = params.id;
        if (params.id instanceof String) {
            personsIdList = [params.id];
        }
        return personsIdList
    }

    def remove = {
        List personsIdList = getPersonIdList(params)
        personsIdList.each {id ->
            personService.delete(id.toLong())
        }

        render "ok"
    }

    def person = {
        redirect(controller: "person", action: "generic", params: params)
    }

    def getFilters = {
        def user = UserContextHolder.getContextUser();
        user.refresh()
        // TODO: filters should be sorted by some property (to avoid mixing on filters reloading)
        def filters = filterService.getFiltersList();
        render(template: "/partial/filters", model: [filters: filters])
    }

    def search = {
        params.query = StringUtils.trimToEmpty(params.query)
        if (StringUtils.isEmpty(params.query)) {
            if (!params.create) {
                redirect controller: "person", action: "filter"
            } else {
                redirect(controller: "person", action: "add", params: [fullName: params.query])
            }
        } else {

            Set<Person> personList = personService.findPersons(params.query, params.create)

            if (!personList?.size() && params.create) {
                redirect(controller: "person", action: "add", params: [fullName: params.query])
            } else {
                request.query = params.query
                render(view: "/person/list", model: [personList: personService.createListModel(personList), query: params.query])
            }
        }
    }
}
