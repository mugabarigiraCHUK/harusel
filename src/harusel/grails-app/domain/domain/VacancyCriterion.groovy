package domain

/**
 * Link between vacancy and criterion.
 */
// CR: major dkranchev 02-Mar-2010 Clarify class responsibility. 
class VacancyCriterion implements Serializable {

    static belongsTo = [
            vacancy: Vacancy,
            criterion: Criterion,
    ]

    boolean required
}
