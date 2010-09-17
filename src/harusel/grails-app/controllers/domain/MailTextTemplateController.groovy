package domain

import common.HRToolConfig
import domain.person.Person
import grails.converters.JSON
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import service.MailService

// CR: normal dkranchev 02-Mar-2010 Javadoc missed.
class MailTextTemplateController {

    // CR: major dkranchev 02-Mar-2010 Violation of Sun code conventions
    JavaMailSender mailSender
    def messageSource
    MailService mailService

    public static final String RESPONSE_ERROR_KEY = "error"
    public static final String RESPONSE_MESSAGE_KEY = "message"

    def index = { redirect(action: list, params: params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def selectTemplateDialog = {
        def templates = MailTextTemplate.list().groupBy {it.groupName};
        render templates as JSON;
    }

    def mailComposer = {
        def template = null;
        def person = Person.get(params.personId?.toLong())
        if (params.id) {
            template = MailTextTemplate.get(params.id).makeFor(person);
        } else {
            template = new MailTextTemplate();
        }
        [template: template, person: person]
    }

    def sendMail = {SendMailCommand command ->
        def responseObj = [:];
        if (command.hasErrors()) {
            def errors = command.errors.allErrors;
            def messages = [];
            errors.each {error ->
                messages << message(error: error);
            }
            responseObj.put(RESPONSE_ERROR_KEY, messages)
        } else {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            command.getEmails().each { message.addTo(it) };
            message.from = new InternetAddress(HRToolConfig.config.email.from);
            message.subject = command.subject;
            message.setText(createHtmlMessage(command), true);
            try {
                mailSender.send(mimeMessage);
                createEvent(command);
                responseObj.put(RESPONSE_MESSAGE_KEY, messageSource.getMessage("message.sendMail.ok", null,
                    LocaleContextHolder.locale))
            } catch (MailSendException e) {
                log.error "Failed to send emails: $e.message", e
                responseObj.put(RESPONSE_ERROR_KEY, [messageSource.getMessage("message.sendMail.error", null,
                    LocaleContextHolder.locale)])
            }
        }

        render responseObj as JSON;
    }

    // CR: normal dkranchev 02-Mar-2010 Extract template.

    private String createHtmlMessage(SendMailCommand command) {
        StringBuilder sb = new StringBuilder();
        sb.append("<HTML>\n");
        sb.append("<HEAD>\n");
        sb.append("<TITLE>\n");
        sb.append(command.subject + "\n");
        sb.append("</TITLE>\n");
        sb.append("</HEAD>\n");
        sb.append("<BODY>\n");
        sb.append(command.text);
        sb.append("\n");
        sb.append("</BODY>\n");
        sb.append("</HTML>\n");

        return sb.toString();
    }

    def createEvent(SendMailCommand mailCommand) {
        // TODO: Notifiy user about error.
        mailService.createEvent(mailCommand)
    }


    def list = {
        // CR: major dkranchev 02-Mar-2010 Create named constants for integers.
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        [mailTextTemplateInstanceList: MailTextTemplate.list(params), mailTextTemplateInstanceTotal: MailTextTemplate.count()]
    }

    def show = {
        def mailTextTemplateInstance = MailTextTemplate.get(params.id)

        if (!mailTextTemplateInstance) {
            // CR: major dkranchev 02-Mar-2010 Non localizable message.
            flash.message = "MailTextTemplate not found with id ${params.id}"
            redirect(action: list)
        }
        else { return [mailTextTemplateInstance: mailTextTemplateInstance] }
    }

    def delete = {
        def mailTextTemplateInstance = MailTextTemplate.get(params.id)
        if (mailTextTemplateInstance) {
            try {
                mailTextTemplateInstance.delete(flush: true)
                // CR: major dkranchev 02-Mar-2010 Non localizable message.
                flash.message = "MailTextTemplate ${params.id} deleted"
                redirect(action: list)
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                // CR: major dkranchev 02-Mar-2010 Non localizable message.
                flash.message = "MailTextTemplate ${params.id} could not be deleted"
                redirect(action: show, id: params.id)
            }
        }
        else {
            // CR: major dkranchev 02-Mar-2010 Non localizable message.
            flash.message = "MailTextTemplate not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def mailTextTemplateInstance = MailTextTemplate.get(params.id)

        if (!mailTextTemplateInstance) {
            // CR: major dkranchev 02-Mar-2010 Non localizable message.
            flash.message = "MailTextTemplate not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [mailTextTemplateInstance: mailTextTemplateInstance]
        }
    }

    def update = {
        def mailTextTemplateInstance = MailTextTemplate.get(params.id)
        if (mailTextTemplateInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (mailTextTemplateInstance.version > version) {
                    // CR: major dkranchev 02-Mar-2010 Non localizable message.
                    mailTextTemplateInstance.errors.rejectValue("version", "mailTextTemplate.optimistic.locking.failure", "Another user has updated this MailTextTemplate while you were editing.")
                    render(view: 'edit', model: [mailTextTemplateInstance: mailTextTemplateInstance])
                    return
                }
            }
            mailTextTemplateInstance.properties = params
            if (!mailTextTemplateInstance.hasErrors() && mailTextTemplateInstance.save()) {
                flash.message = "MailTextTemplate ${params.id} updated"
                redirect(action: show, id: mailTextTemplateInstance.id)
            }
            else {
                render(view: 'edit', model: [mailTextTemplateInstance: mailTextTemplateInstance])
            }
        }
        else {
            // CR: major dkranchev 02-Mar-2010 Non localizable message.
            flash.message = "MailTextTemplate not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def create = {
        def mailTextTemplateInstance = new MailTextTemplate()
        mailTextTemplateInstance.properties = params
        return ['mailTextTemplateInstance': mailTextTemplateInstance]
    }

    def save = {
        def mailTextTemplateInstance = new MailTextTemplate(params)
        if (!mailTextTemplateInstance.hasErrors() && mailTextTemplateInstance.save()) {
            // CR: major dkranchev 02-Mar-2010 Non localizable message.
            flash.message = "MailTextTemplate ${mailTextTemplateInstance.id} created"
            redirect(action: show, id: mailTextTemplateInstance.id)
        }
        else {
            render(view: 'create', model: [mailTextTemplateInstance: mailTextTemplateInstance])
        }
    }
}
