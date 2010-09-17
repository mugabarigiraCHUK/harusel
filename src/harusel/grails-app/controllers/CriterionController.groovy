import domain.Criterion
import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.springsecurity.Secured

@Secured(['ROLE_HR_ALLOWED'])
// CR: normal dkranchev 02-Mar-2010 Javadoc missed.
class CriterionController {

    def criterionService

    def cancel = {
        render "<div class='notify'>" + message(code: 'criterion.canceled') + "</div>"
    }

    def save = {
        def newCriteriaTree = []
        def query = JSON.parse(params.criterias)
        query.eachWithIndex {criteria, index ->
            def criteriaInstance = getCriteriaForObject(criteria, index, null)
            newCriteriaTree << criteriaInstance
        }
        def removedCriterionList = criterionService.getRemovedCriterions(newCriteriaTree)
        removedCriterionList.each { it.delete() }
        newCriteriaTree.each {it.save()}
        // CR: major dkranchev 02-Mar-2010 View messed with controller.
        render "<div class='notify'>" + message(code: 'criterion.updated') + "</div>"
    }

    private def getCriteriaForObject(def criteriaObject, def index, def parent) {
        def criteria
        if (criteriaObject.id == 'new') {
            criteria = new Criterion()
        } else {
            criteria = Criterion.get(criteriaObject.id.toLong())
        }
        criteria.name = criteriaObject.name
        criteria.parent = parent
        criteria.childIndex = index
        criteria.children = new TreeSet()
        criteriaObject.children.eachWithIndex {child, subIndex ->
            criteria.children.add(getCriteriaForObject(child, subIndex, criteria))
        }
        return criteria
    }
}
