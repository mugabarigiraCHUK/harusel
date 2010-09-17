package domain.person

import domain.user.AppUser
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Person's document descriptor. Person document may be CV, poll answers, etc.
 * When document gets removed by user, entity becomes marked appropriately and the data is deleted.
 */
class Document implements Serializable {
    static belongsTo = [
        person: Person, // document owner person
        user: AppUser // User who's added document
    ]

    static constraints = {
        data nullable: true
        user nullable: true
    }

    static mapping = {
        columns {
            data sqlType: ConfigurationHolder.config.datatypes.longvarbinary, lazy: true;
        }

    }

    static searchable = {
        except = ['data']
        name boost: 2.0
        date index: 'no'
        removed index: 'no'
        type component: true
    }

    /**
     * Document type: cv, questionnaire, etc
     */
    DocumentType type

    /**
     * Original document file name
     */
    String name

    /**
     * Creation date
     */
    Date date

    /**
     * true if document has been removed
     */
    boolean removed

    /**
     * Actual document data.
     */
    byte[] data

    /**
     * Returns document file name.
     */
    String toString() {
        name
    }
}
