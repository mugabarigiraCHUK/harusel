package service

import common.BeanNestedPropertiesForStrSubstitutor
import common.HRToolConfig
import domain.event.PersonEvent
import domain.user.AppUser
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Builder of notifications to be sent by e-mail.
 * @since 23-Jun-2010 11:15:13
 * @version 1.3
 */
class NotificationBuilder {

    MessageSource messageSource
    AppUserService appUserService

    /**
     * Builds message for the event.
     * @param event Event which will be notified.
     *
     @return Message.
     */
    def buildMessage(PersonEvent event, AppUser recepient, NotificationReason reason, String serverURL) {
        return [
            body: getMailBody(event, recepient, reason, serverURL),
            subject: getMailSubject(event),
            from: getMailSender(event, recepient, reason)
        ]
    }

    private String getMailSender(PersonEvent event, AppUser recepieint, NotificationReason reason) {
            event.user.email ?: HRToolConfig.config.email.from
    }

    /**
     * Loads message template for given Event instance
     */
    private String getMailBody(PersonEvent event, AppUser recepient, NotificationReason reason, String serverURL) {
        def salutation = getResourceString(event, "NotificationTemplate.common.salutation", [recepient.getFullName()] as Object[])
        def body = getResourceString(event, getBodyKey(event))
        def details = getResourceString(event, "NotificationTemplate.common.seeDetailsLink", buildDetailsLink(event, serverURL))
        def whyReceive = ""
        switch (reason) {
            case NotificationReason.VACANCY:
                List recepientVacancies = appUserService.getAppUserVacancies(recepient)
                def resultVacancies = recepientVacancies.intersect(event.person.vacancies)
                if (resultVacancies) {
                    whyReceive = getResourceString(event, "NotificationTemplate.common.whyReceived.vacancy", [resultVacancies])
                }
                break
            case NotificationReason.DIRECT:
                whyReceive = getResourceString(event, "NotificationTemplate.common.whyReceived.direct", [event.person.fullName])
                break
            default:
                log.error("Unsupported reason passed: " + reason)
        }

        def unsubscribe = getResourceString(event, "NotificationTemplate.common.unsubscribe", buildUnsubscribeLink(event, serverURL));
        def footer = getResourceString(event, "NotificationTemplate.common.regards");

        String newLine = "\r\n\r\n"
        return salutation + newLine + body + newLine + details + newLine + whyReceive + newLine + unsubscribe + newLine + footer
    }

    private String buildUnsubscribeLink(PersonEvent personEvent, String serverURL) {
        return serverURL + "/person/unsubscribe/" + personEvent.person.id
    }

    private String buildDetailsLink(PersonEvent personEvent, String serverURL) {
        return serverURL + "/person/view/" + personEvent.person.id
    }


    private String getBodyKey(PersonEvent event) {
        return "NotificationTemplate.${event.class.simpleName}.body"
    }

    private String getResourceString(PersonEvent event, String resourceCode, Object[] params = null) {
        def template = messageSource.getMessage(resourceCode, params, LocaleContextHolder.locale);
        def result = new BeanNestedPropertiesForStrSubstitutor(event).substitute(template);
        if (!result) {
            log.error("Cannot build message '${resourceCode}' for event: '${event}'")
        }
        return result
    }

    /**
     * Finds message resource for key <event's simple class name>.notification.subject
     */
    private String getMailSubject(PersonEvent event) {
        return getResourceString(
            event,
            "NotificationTemplate.${event.class.simpleName}.subject".toString(),
            [event.person.fullName] as Object[]
        );
    }
}
