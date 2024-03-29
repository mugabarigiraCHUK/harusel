package domain.event

// CR: normal dkranchev 02-Mar-2010 Missed javadoc.
class MailEvent extends PersonEvent {

    String subject;
    String text;

    static constraints = {
        /** Max mail size.   */
        text(maxSize: 65000)
    }

    static mapping = {
        columns {
            // TODO: Test if this works on MSSQL.
            text type: 'text'
        }
    }

}
