package domain

import org.apache.commons.lang.StringUtils
import org.apache.commons.validator.EmailValidator

class SendMailCommand {
    static final String EMAILS_SEPARATOR = ";"

    long personId;
    String to;
    String subject;
    String text;

    static constraints = {
        // CR: normal dkranchev 02-Mar-2010 Extract validator.
        to(nullable: false, blank: false, validator: {val, obj ->
            def emails = obj.getEmails();
            def validator = new EmailValidator();
            def isValid = emails.find { !validator.isValid(it) } == null;
            return isValid ?: "default.invalid.email.message";
        })

        subject(nullable: false, blank: false)
        text(nullable: false, blank: false)

    };

    // CR: major dkranchev 02-Mar-2010 Inconsistent UI. In candidate UI user can enter ',' as separator. 
    def getEmails() {
        return to.split(EMAILS_SEPARATOR).collect { StringUtils.trim(it) };
    }
}
