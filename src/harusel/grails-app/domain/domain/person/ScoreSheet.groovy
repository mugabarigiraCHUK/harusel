package domain.person

import domain.user.AppUser

/**
 * Person score sheet from particular user on some qualifucation
 */
class ScoreSheet implements Serializable {

    static belongsTo = [
        user: AppUser, // Reporter
        person: Person, // Scored person
    ]

    static mapping = {
        cache true
    };

    String name
    Date date
    /**
     * Overall score comment
     */
    String comment

    static transients = ['nameList'];

    static constraints = {
        name(blank: false)
        date(nullable: false)
        comment(nullable: true, blank: true)
    }

    static List<String> getNameList() {
        ScoreSheet.withCriteria {
            projections {
                distinct('name');
            }
        }
    }
}
