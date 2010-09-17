package domain.filter

import domain.filter.FilterAction

// CR: normal dkranchev 02-Mar-2010 Javadoc missed.
class FilterActionController {

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def index = { redirect(action: list, params: params) }

    def list = {
        // CR: major dkranchev 02-Mar-2010 Name the integer constants.
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        [filterActionInstanceList: FilterAction.list(params), filterActionInstanceTotal: FilterAction.count()]
    }

    def show = {
        def filterActionInstance = FilterAction.get(params.id)

        if (!filterActionInstance) {
            // CR: major dkranchev 02-Mar-2010 Not localizable.
            // CR: major dkranchev 02-Mar-2010 Not user friendly message.
            flash.message = "FilterAction not found with id ${params.id}"
            redirect(action: list)
        } else {
            [filterActionInstance: filterActionInstance]
        }
    }

    def delete = {
        def filterActionInstance = FilterAction.get(params.id)
        if (filterActionInstance) {
            try {
                filterActionInstance.delete(flush: true)
                // CR: major dkranchev 02-Mar-2010 Not localizable.
                flash.message = "FilterAction ${params.id} deleted"
                redirect(action: list)
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                // CR: major dkranchev 02-Mar-2010 Not localizable.
                flash.message = "FilterAction ${params.id} could not be deleted"
                redirect(action: show, id: params.id)
            }
        } else {
            // CR: major dkranchev 02-Mar-2010 Not localizable.
            flash.message = "FilterAction not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def filterActionInstance = FilterAction.get(params.id)

        if (!filterActionInstance) {
            // CR: major dkranchev 02-Mar-2010 Not localizable.
            flash.message = "FilterAction not found with id ${params.id}"
            redirect(action: list)
        } else {
            [filterActionInstance: filterActionInstance]
        }
    }

    def update = {
        def filterActionInstance = FilterAction.get(params.id)
        if (filterActionInstance) {
            // CR: major dkranchev 02-Mar-2010 why not using optimistic locking?
            if (params.version) {
                def version = params.version.toLong()
                if (filterActionInstance.version > version) {
                    // CR: major dkranchev 02-Mar-2010 Not localizable.
                    filterActionInstance.errors.rejectValue("version", "filterAction.optimistic.locking.failure", "Another user has updated this FilterAction while you were editing.")
                    render(view: 'edit', model: [filterActionInstance: filterActionInstance])
                    return
                }
            }
            filterActionInstance.properties = params
            if (!filterActionInstance.hasErrors() && filterActionInstance.save()) {
                // CR: major dkranchev 02-Mar-2010 Not localizable.
                flash.message = "FilterAction ${params.id} updated"
                redirect(action: show, id: filterActionInstance.id)
            } else {
                render(view: 'edit', model: [filterActionInstance: filterActionInstance])
            }
        } else {
            // CR: major dkranchev 02-Mar-2010 Not localizable.
            flash.message = "FilterAction not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def create = {
        def filterActionInstance = new FilterAction()
        filterActionInstance.properties = params
        return ['filterActionInstance': filterActionInstance]
    }

    def save = {
        def filterActionInstance = new FilterAction(params)
        if (!filterActionInstance.hasErrors() && filterActionInstance.save()) {
            // CR: major dkranchev 02-Mar-2010 Not localizable.
            flash.message = "FilterAction ${filterActionInstance.id} created"
            redirect(action: show, id: filterActionInstance.id)
        } else {
            render(view: 'create', model: [filterActionInstance: filterActionInstance])
        }
    }
}
