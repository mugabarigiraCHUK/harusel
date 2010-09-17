package service

import domain.event.PersonEvent
import domain.user.AppUser
import grails.util.Environment
import java.util.Map.Entry
import javax.mail.MessagingException
import org.apache.tools.mail.MailMessage
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.context.MessageSource
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import security.UserContextHolder

/**
 * Service is Used for sending notifications about new events to reviewers
 */
class NotificationService {

    def mailSender
    // blueprint message (configured in resources.groovy)
    def mailMessage

    MessageSource messageSource

    PersonService personService

    NotificationBuilder notificationBuilder

    /**
     * Sends notifications for given Event. The template file for message is looked for by event class name
     * with suffix '.txt' added. These files should be placed where this Event class was loaded.
     */
    def sendNotification(PersonEvent event, String serverURL) {
        log.debug "send notifications about changes in person '${event.person.fullName}'"

        def messages = createMessages(event, serverURL)

        // Send them all together
        try {
            mailSender.send(messages as SimpleMailMessage[])
        }
        catch (MailException e) {
            log.error "Failed to send emails: $e.message", e
        }
        catch (MessagingException e) {
            log.error "Failed to send emails: $e.message", e
        }
    }

    /**
     * Create list of SimpleMailMessage objects
     */
    private List<MailMessage> createMessages(PersonEvent event, String serverURL) {

        Map<AppUser, NotificationReason> recepients = personService.getAllObserversFor(event.person);

        // todo: to delete
        if (Environment.current != Environment.PRODUCTION) {
            // testhr1 hasn't email - throws java.lang.NullPointerException
            UserContextHolder.contextUser.email = ConfigurationHolder.config.notification.testEmail
            recepients = [:]
            recepients[UserContextHolder.contextUser] = NotificationReason.VACANCY;
        }

        def messages = []

        for (Entry<AppUser, NotificationReason> recepient in recepients.entrySet()) {
            log.debug "create mail to '${recepient.key}'"
            // create a copy of the default mail
            def mail = new SimpleMailMessage(mailMessage as SimpleMailMessage)
            // should be wrapped with list because of String is like
            // List and setTo method has 2 version for String and List objects.
            mail.to = [recepient.key.email]

            def message = notificationBuilder.buildMessage(event, recepient.key, recepient.value, serverURL);

            mail.from = message.from
            mail.text = message.body
            mail.subject = message.subject
            messages << mail
        }
        return messages
    }

}
