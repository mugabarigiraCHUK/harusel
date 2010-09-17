package domain.event

import domain.Stage

// CR: minor dkranchev 02-Mar-2010 Missed javadoc
class MakeDecisionEvent extends PersonEvent {

    String comment

    static belongsTo = [
        beforeDecisionStage: Stage,
        decidedStage: Stage
    ]

    static constraints = {
        comment(blank: false)
    }

}
