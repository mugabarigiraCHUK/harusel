package domain.filter

// CR: normal dkranchev 02-Mar-2010 Javadoc missed.
class FilterAction implements Serializable {

    static constraints = {
        description(blank: false)
        query(blank: false)
        name(blank: false)
    }

    static mapping = {
        columns {
            query type: "text"
        }
    }
    /**
     * Name of filter
     */
    String name

    /**
     * If true this filter should be displayed on home page
     */
    // CR: major dkranchev 02-Mar-2010 rename to visible?
    boolean active;

    /**
     * Filter criteria definition
     */
    String query

    /**
     * Human friendly description
     */
    // CR: normal dkranchev 02-Mar-2010 Should be refactored to support multilanguage.
    String description
}
