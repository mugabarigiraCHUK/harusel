package domain

import domain.user.AppUser
import org.codehaus.groovy.grails.plugins.springsecurity.Secured

// CR: major dkranchev 02-Mar-2010 Javadoc missed
@Secured(['ROLE_HR_ALLOWED'])
class VacancyController {
    // CR: major dkranchev 02-Mar-2010 Sun code conventions violation.
    def criterionService

    def index = { redirect(action: list, params: params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def select = {
        final def vacancyList = Vacancy.findAllByActive(true, [cache: true]);
        render(template: "selectDialog", model: [vacancyList: vacancyList])
    }

    def list = {
        render template: 'list', model: [vacancyList: Vacancy.list()]
    }

    def cancel = {
        flash.message = message(code: "vacancy.canceled")
        redirect(action: 'edit', params: params)
    }

    def edit = {
        def vacancyInstance = Vacancy.get(params.id)

        if (!vacancyInstance) {
            flash.message = message(code: "vacancy.notFound", args: [params.id])
            redirect(action: list)
        }
        else {
            return [selectedCriteriaIdList: getSelectedCriterionIdList(vacancyInstance),
                vacancy: vacancyInstance,
                criteriaList: criterionService.roots]
        }
        return
    }

    private def getSelectedCriterionIdList(vacancyInstance) {
        def selectedCriteriaIdList = []
        vacancyInstance.criteria?.each {criteria ->
            selectedCriteriaIdList << criteria.criterion.id;
            criteria.criterion.children?.each {child ->
                selectedCriteriaIdList << child.id
            }
        }
        return selectedCriteriaIdList
    }

    // CR: major dkranchev 02-Mar-2010 same as in SOurceController.
    def update = {
        def onSuccess = 'edit'
        if (params.backToList?.toBoolean()) {
            onSuccess = 'list'
        }
        def vacancy = Vacancy.get(params.id)
        if (vacancy) {
            if (params.version) {
                def version = params.version.toLong()
                if (vacancy.version > version) {
                    vacancy.errors.rejectValue("version", "vacancy.optimistic.locking.failure", "Another user has updated this Vacancy while you were editing.")
                    render(view: 'edit', model: [vacancy: vacancy,
                        selectedCriteriaIdList: getSelectedCriterionIdList(vacancy),
                        criteriaList: criterionService.roots])
                    return
                }
            }
            vacancy.properties = params
            updateVacancyUsers(vacancy, params.usersIdList);
            updateVacancyCriterias(vacancy, params.criteriasIdList);
            if (!vacancy.hasErrors() && vacancy.save()) {
                flash.message = message(code: "vacancy.updated", args: [vacancy.name])
                redirect(action: onSuccess, id: vacancy.id)
            }
            else {
                render(view: 'edit', model: [vacancy: vacancy,
                    selectedCriteriaIdList: getSelectedCriterionIdList(vacancy),
                    criteriaList: criterionService.roots])
            }
        }
        else {
            flash.message = message(code: "vacancy.notFound", args: [params.id])
            redirect(action: list)
        }
    }

    private def paramsAsList(obj) {
        if (!obj) {
            return []
        }
        if (obj instanceof String) {
            return [obj]
        }
        // CR: normal dkranchev 02-Mar-2010 If obj is not a list then method is buggy.
        return obj
    }


    private def updateVacancyUsers(def vacancy, def usersIdList) {
        // CR: major dkranchev 02-Mar-2010 Business logic should be in service.
        def usersId = paramsAsList(usersIdList)*.toLong();
        usersId.each {userId ->
            def user = AppUser.get(userId)
            vacancy.addToUsers(user)
        }
    }

    private def updateVacancyCriterias(def vacancy, def criteriasIdList) {
        // CR: major dkranchev 02-Mar-2010 Business logic should be in service.
        def criteriasId = paramsAsList(criteriasIdList)*.toLong()
        vacancy.criteria*.delete()

        def criterias = criteriasId.collect {criteriaId ->
            def criterion = Criterion.get(criteriaId)
            new VacancyCriterion(vacancy: vacancy, criterion: criterion)
        }
        vacancy.criteria = criterias
    }

    def create = {
        def vacancyInstance = new Vacancy()
        vacancyInstance.properties = params
        render view: "edit", model: [selectedCriteriaIdList: [],
            vacancy: vacancyInstance,
            criteriaList: criterionService.roots]
    }

    def save = {
        def onSuccess = 'edit'
        if (params.backToList?.toBoolean()) {
            onSuccess = 'list'
        }
        def vacancy = new Vacancy(params)
        updateVacancyUsers(vacancy, params.usersIdList);
        updateVacancyCriterias(vacancy, params.criteriasIdList);
        if (!vacancy.hasErrors() && vacancy.save()) {
            flash.message = message(code: "vacancy.created", args: [vacancy.name])
            redirect(action: onSuccess, id: vacancy.id)
        }
        else {
            render(view: 'edit', model: [vacancy: vacancy,
                selectedCriteriaIdList: getSelectedCriterionIdList(vacancy),
                criteriaList: criterionService.roots])
        }
    }

    private def getIdList() {
        def idList = params.id
        if (idList instanceof String) {
            idList = [idList]
        }
        return idList
    }

    def deactivate = {
        idList.each {id ->
            def vacancy = Vacancy.get(id.toLong())
            vacancy.active = false
            vacancy.save()
        };
        flash.message = message(code: "vacancy.deactivated")
        redirect(action: 'list');
    }
}
