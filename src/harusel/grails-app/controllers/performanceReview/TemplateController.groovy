package performanceReview

import service.TemplateService

/**
 * Controller to perform CRUD operations on Template.
 */
class TemplateController {

    TemplateService templateService;

    // CR: normal dkranchev 02-Mar-2010 It is better to use defaultAction property.
    def index = { redirect(action: list, params: params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    // CR: major dkranchev 02-Mar-2010 Create named constants for integers.
    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        [templateInstanceList: Template.list(params), templateInstanceTotal: Template.count()]
    }

    def show = {
        def templateInstance = Template.get(params.id)

        if (!templateInstance) {
            // CR: major dkranchev 02-Mar-2010 Non localizable.
            flash.message = "Template not found with id ${params.id}"
            redirect(action: list)
        }
        else { return [templateInstance: templateInstance] }
    }

    /**
     * Deletes template by id.
     */
    def delete = {
        Long templateId = params.id
        if (templateService.delete(templateId)) {
            // CR: major dkranchev 02-Mar-2010 Non localizable.
            flash.message = "Template ${params.id} deleted"
            redirect(action: list)
        }
        else {
            // CR: major dkranchev 02-Mar-2010 Non localizable.
            flash.message = "Template ${params.id} could not be deleted"
            redirect(action: show, id: params.id)
        }
    }

    def edit = {
        def templateInstance = Template.get(params.id)

        if (!templateInstance) {
            // CR: major dkranchev 02-Mar-2010 Non localizable.
            flash.message = "Template not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [templateInstance: templateInstance]
        }
    }

    // CR: major dkranchev 02-Mar-2010 Same as in ScoreController
    def update = {
        def templateInstance = Template.get(params.id)
        if (templateInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (templateInstance.version > version) {

                    templateInstance.errors.rejectValue("version", "template.optimistic.locking.failure", "Another user has updated this Template while you were editing.")
                    render(view: 'edit', model: [templateInstance: templateInstance])
                    return
                }
            }
            templateInstance.properties = params
            if (!templateInstance.hasErrors() && templateInstance.save()) {
                flash.message = "Template ${params.id} updated"
                redirect(action: show, id: templateInstance.id)
            }
            else {
                render(view: 'edit', model: [templateInstance: templateInstance])
            }
        }
        else {
            // CR: major dkranchev 02-Mar-2010 Non localizable.
            flash.message = "Template not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def create = {
        def templateInstance = new Template()
        templateInstance.properties = params
        return ['templateInstance': templateInstance]
    }

    def save = {
        def templateInstance = new Template(params)
        if (!templateInstance.hasErrors() && templateInstance.save()) {
            // CR: major dkranchev 02-Mar-2010 Non localizable.
            flash.message = "Template ${templateInstance.id} created"
            redirect(action: show, id: templateInstance.id)
        }
        else {
            render(view: 'create', model: [templateInstance: templateInstance])
        }
    }
}
