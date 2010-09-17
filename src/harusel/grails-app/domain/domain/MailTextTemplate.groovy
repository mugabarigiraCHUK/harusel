package domain

import domain.person.Person
import org.apache.commons.lang.text.StrSubstitutor

/**
 * Email message template
 */
class MailTextTemplate {
    /**
     * The name of templates group for quick searching of templates by group on UI
     */
    // CR: minor dkranchev 02-Mar-2010 Create separate entity for groups name.
    String groupName;
    /**
     * Name of template aka short description
     */
    String name;
    /**
     * Predefined mail subject
     */
    String subject;
    /**
     * Predefined mail body
     */
    String body;

    // CR: minor dkranchev 02-Mar-2010 constraints should be above other properties in class.
    static constraints = {
        name(blank: false, nullable: false)
        groupName(blank: false, nullable: false)
        body(blank: false, nullabele: false)
        subject(blank: true, nullable: true)
    }

    def makeFor(Person person) {
        MailTextTemplate template = new MailTextTemplate();
        StrSubstitutor substitutor = new StrSubstitutor(person.properties);
        template.subject = substitutor.replace(subject);
        template.body = substitutor.replace(body);
        return template;
    }

    def MailTextTemplate() {
    }
}
