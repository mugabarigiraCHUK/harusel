package domain

import domain.VacancyCriterion
import domain.person.ScorePoint

/**
 * Score criterion. Criteria has hierarchical structure.
 */
class Criterion implements Comparable, Serializable {

    static transients = ['readOnly']

    static hasMany = [
        children: Criterion,
        vacancies: VacancyCriterion,
        scorePoints: ScorePoint,
    ]

    static belongsTo = [parent: Criterion]

    static mappedBy = [
        children: 'parent'
    ]

    
    SortedSet children
    int childIndex
    String name
    String description

    static constraints = {
        description(blank: true, nullable: true)
    }

    String toString() {
        "Criterion[id=$id, i=$childIndex, '$name', $children, ${readOnly ? 'readOnly' : ''}]"
    }

    public int compareTo(def o) {
        childIndex.compareTo(o.childIndex)
    }

    boolean isReadOnly() {
        children != null ? !children.isEmpty() : false
    }

    // CR: normal dkranchev 02-Mar-2010 id also should be used in hashCode.
    public boolean equals(def obj) {
        if (this == obj) {
            return true
        }
        if (!(obj instanceof Criterion)) {
            return false;
        }
        if (id != null && obj.id != null) {
            id == obj.id
        }
        name == obj.name
    }

    public int hashCode() {
        name ? name.hashCode() : super.hashCode()
    }
}
