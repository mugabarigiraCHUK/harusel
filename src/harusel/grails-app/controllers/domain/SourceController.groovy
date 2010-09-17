// CR: major dkranchev 02-Mar-2010 Refactor packages content. 
package domain

import org.codehaus.groovy.grails.plugins.springsecurity.Secured

// CR: major dkranchev 02-Mar-2010 Javadoc missed.
@Secured(['ROLE_HR_ALLOWED'])
class SourceController {
    def index = {
        [sourceList: PersonSource.list()]
    }

    def select = {
        final def sourceList = PersonSource.withCriteria { eq('active', true) };
        render(template: "selectDialog", model: [sourceList: sourceList])
    }

    def list = {
        render template: 'list', model: [sourceList: PersonSource.list()]
    }

    def cancel = {
        flash.message = message(code: "source.canceled")
        redirect(action: edit, params: params)
    }

    def edit = {
        def source = PersonSource.get(params.id)

        if (!source) {
            flash.message = message(code: "source.notFound", args: [params.id])
            redirect(action: list)
            return
        }
        return [source: source]
    }

    def create = {
        def source = new PersonSource()
        source.properties = params
        render view: "edit", model: ['source': source]
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
            def source = PersonSource.get(id.toLong())
            source.active = false
            source.save()
        };
        flash.message = message(code: "source.deactivated")
        redirect(action: 'list');
    }

    // CR: normal dkranchev 02-Mar-2010 methods in controllers are not transactional, update is not reliable.
    def update = {
        def backToList = params.close?.toBoolean();
        def source = PersonSource.get(params.id)
        if (source) {
            if (params.version) {
                def version = params.version.toLong()
                if (source.version > version) {
                    // CR: major dkranchev 02-Mar-2010 Non localizable message.
                    source.errors.rejectValue("version", "source.optimistic.locking.failure", "Another user has updated this Vacancy while you were editing.")
                    render(view: 'edit', model: [source: source])
                    return
                }
            }
            source.properties = params
            if (!source.hasErrors() && source.save()) {
                flash.message = message(code: "source.updated", args: [source.name])
                if (backToList) {
                    redirect(action: 'list')
                } else {
                    redirect(action: 'edit', id: source.id)
                }
            }
            else {
                render(view: 'edit', model: [source: source])
            }
        }
        else {
            flash.message = "Vacancy not found with id ${params.id}"
            redirect(action: list)
        }
    }


    def save = {
        def backToList = params.close?.toBoolean()
        def source = new PersonSource(params)
        if (!source.hasErrors() && source.save()) {
            flash.message = message(code: "source.created", args: [source.name])
            if (backToList) {
                redirect(action: 'list')
            } else {
                redirect(action: 'edit', id: source.id)
            }
        }
        else {
            render(view: 'edit', model: [source: source])
        }
    }

}
