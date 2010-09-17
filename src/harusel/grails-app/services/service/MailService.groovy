package service

import domain.SendMailCommand
import domain.event.MailEvent
import domain.person.Person
import security.UserContextHolder

class MailService {

    boolean transactional = true

    boolean createEvent(SendMailCommand mailCommand) {
        Person person = Person.get(mailCommand.personId);
        def user = UserContextHolder.contextUser;
        MailEvent event = new MailEvent(date: new Date(), person: person, user: user,
            subject: mailCommand.subject,
            text: mailCommand.text);
        if(event.save()){
            return true;
        }
        else{
            log.info ("Can't save MailEvent: " + event.errors.allErrors);
            return false;
        }
    }
}
