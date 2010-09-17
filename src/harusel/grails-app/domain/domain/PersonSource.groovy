package domain

/**
 * Where candidate come from.
 */
// CR: normal dkranchev 02-Mar-2010 rename to CandidateSource?
class PersonSource implements Serializable {

    static constraints = {
        agreement(nullable: true)
        name(blank: false)
    }

    /**
     * Source name, e.g. NGS, Sukhorukov, etc.
     */
    String name

    /**
     * Terms of agreement
     */
    String agreement

    /**
     * Is this source active
     */
    boolean active

    /**
     * Returns source name. Used at least in the timeline.
     */
    String toString() {
        name
    }
}
