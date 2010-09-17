package domain.person

/**
 * Document type, like CV in russian, CV in english, test, survey, etc.
 */
class DocumentType implements Serializable {

    static constraints = {
        typeCode(nullable: true)
    }

    static searchable = {
        name boost: 2.0
    }

    /**
     * Key.
     * For types hardcode convenience.
     * See docTypes in @{link Config.groovy} file
     * Nullable.
     */
    String typeCode

    /**
     * Localized name.
     */
    String name

    /**
     * Tells if documents of this type should be easily accessible from the UI.
     * Privileged because it is main type and has localization by typeCode property value
     */
    boolean privileged

    /**
     * Two document types are equals if they share same name.
     */
    boolean equals(def that) {
        return that instanceof DocumentType && name == that?.name
    }

    /**
     * Returns localized document type name.
     */
    String toString() {
        name
    }
}
