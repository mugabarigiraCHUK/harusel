package domain.event

import domain.Stage

/**
 * Person stage change event.
 */
class StageChangedEvent extends PersonEvent {

    static belongsTo = [
        // CR: major dkranchev 02-Mar-2010 Rename to originalStage?
        from: Stage, // original stage
        // CR: major dkranchev 02-Mar-2010 Rename to finalStage?
        to: Stage, // target stage
    ]

    /**
     * Comment on changing person's stage
     */
    String comment

    static constraints = {
        comment(nullable: true)
    }

}
