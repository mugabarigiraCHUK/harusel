package domain.person

import org.codehaus.groovy.grails.plugins.springsecurity.Secured

@Secured(['ROLE_HR_ALLOWED', 'ROLE_PM_ALLOWED'])
class DocumentController {
    private static final String CHARSET_FOR_FILENAMES = "UTF-8"
    private static final String SPACE_CODE = "%20"

    def personService

    def download = {
        Document document = Document.get(params.id);
        if (document && !document.removed) {
            // see RFC2231
            String encodedFilename = URLEncoder.encode(document.name, CHARSET_FOR_FILENAMES).replace("+", SPACE_CODE)
            response.setHeader 'Content-disposition', "attachment; filename*=${CHARSET_FOR_FILENAMES}''${encodedFilename}"
            response.contentLength = document.data.size()
            response.outputStream << document.data;
        } else {
            response.sendError 404
        }
    }

    def list = {
        def person = Person.get(params.id as Long)
        if (!person) {
            log.error("Can't find person ${params.id}");
            throw new IllegalArgumentException("No person with id=${params.id}")
        }
        [documents: person.documents.findAll {!it.removed}]
    }

    def uploadForm = {
        def privileged = [], other = []
        DocumentType.getAll().each {
            (it.privileged ? privileged : other) << it
        }
        return [
            fileCount: getFileCount(),
            privilegedTypes: privileged,
            otherTypes: other,
        ]
    }

    def create = {DocumentUploadCommand command ->
        log.debug("Document is uploaded...")
        def errors = []
        def person = Person.get(params.id)
        if (person) {
            command.documents?.findAll {it}.each {upload ->
                if (upload.file.empty) {
                    log.debug("File is empty...")
                    errors << message(code: 'document.empty', args: [upload.file.originalFilename])
                    return null
                }
                def documentType = upload.type
                if (!documentType) {
                    def typeName = upload.typeName?.trim()
                    if (!typeName) {
                        log.debug("Type of documentFile is unspecified...")
                        errors << message(code: 'document.documentTypeDescription.empty')
                        return null
                    }
                    documentType = DocumentType.findByName(typeName) ?: new DocumentType(name: typeName).save()
                }
                def event = personService.addDocument(person, upload.file, documentType, errors)
                if (!errors) {
                    flash.message = message(code: "document.added")
                }
            }
        } else {
            errors << "Person with id ${params.id} not found"
            log.error("Can't find person with id ${params.id}")
        }
        [errors: errors]
    }

    def remove = {
        request.getParameterValues('documentId').each { // params.documentId may produce both String and List
            def document = Document.get(it)
            if (document) {
                def person = Person.createCriteria().get { documents { eq("id", document.id) } }
                personService.removeDocument(person, document)
                flash.message = message(code: "document.removed", args: [document.name]);
            }
        }
        redirect(action: 'list', id: params.id)
    }

    def getFileCount = {
        grailsApplication.config.person.documents.to.upload
    }
}