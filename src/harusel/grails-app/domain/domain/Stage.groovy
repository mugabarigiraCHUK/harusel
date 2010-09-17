package domain

/**
 * Candidate state
 */
class Stage implements Serializable {
    /**
     * Stage unique code name
     */
    String codeName

    String toString() {
        "id=$id : nameCode=$codeName"
    }

    static searchable = {
        codeName index: 'no'
    }
}
