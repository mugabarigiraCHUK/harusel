package domain.event

import domain.person.ScoreSheet

/**
 * Describes score list addition
 */
class ScoreSheetEvent extends PersonEvent {

    static belongsTo = [score: ScoreSheet] // Added score
}
