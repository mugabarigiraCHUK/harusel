package performanceReview

/**
 * Question in Performance Review template
 */
// CR: major dkranchev 02-Mar-2010 Javadoc missed.
// CR: major dkranchev 02-Mar-2010 Enable second level cache for this entity.
class Question implements Comparable<Question>, Serializable {

    static hasMany = [
        children: Question,
    ]

    /**
     * For questions inside group parent it is reference on group, for group it is null
     */
    static belongsTo = [parent: Question];

    static mappedBy = [
        children: 'parent'
    ]

    static mapping = {
		children batchSize:25
	}

    /**
     * Text of question
     */
    String text;
    /**
     * Index in question's group
     */
    int childIndex;
    /**
     * Questions set for given group
     */
    SortedSet children;
    /**
     * Marker for deleted question (set true if can't delete because of existing answers)
     */
    boolean isDeleted = false;

    int compareTo(Question o) {
        return o.childIndex == childIndex ? 0 : o.childIndex > childIndex ? -1 : 1;
    }

    @Override
    boolean equals(final o) {
        if (!o || getClass() != o.class) {
            return false;
        }
        Question question = (Question) o;

        if (childIndex != question.childIndex)
            return false;
        if (!text.equals(question.text))
            return false;

        return true;
    }

    @Override
    int hashCode() {
        int result;

        result = text.hashCode();
        result = 31 * result + childIndex;
        return result;
    }

    /**
     * Returns text of question
     */
    @Override
    def String toString() {
        return text;
    }


}
