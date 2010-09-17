package domain.person

import domain.Criterion

/**
 * Single (commented) score of some person's quality.
 */
class ScorePoint implements Serializable {

    static belongsTo = [
        score: ScoreSheet, // Owning score sheet
        // TODO: Check : cascade delete ScorePoints on deletion Criterion?
        criterion: Criterion, // What has been scored
    ]

    static constraints = {
        comment(blank: true)
        value(nullable: false, range: 0..2)
    }

    /**
     * Score [0..2]
     */
    int value

    /**
     *  comment to point
     */
    String comment

    /**
     * Returns score value as string.
     */
    String toString() {
        value as String
    }
}
