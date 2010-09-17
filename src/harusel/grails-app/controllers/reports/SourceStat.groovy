package reports

import domain.PersonSource
import domain.Vacancy
import domain.event.StageChangedEvent

/**
 * Internal model object for source report generation:
 * Represent statistic by source:
 *  - added resume
 *  - persons assigned for negotiation
 *  - rejected persons
 */
// CR: major dkranchev 02-Mar-2010 Rename to SourceStatistics.
class SourceStat {
    PersonSource source
    // Object to collect events of exact person (to eliminate counting of the same state)
    // CR: normal dkranchev 02-Mar-2010 Rename to personEventsTracker
    Map<Long, PersonEvents> personEventsTrack = [:]

    // CR: normal dkranchev 02-Mar-2010 This property should be read only.
    // new resume
    Integer added = 0
    // CR: normal dkranchev 02-Mar-2010 This property should be read only.
    // assigned to negotiation
    Integer processing = 0
    // CR: normal dkranchev 02-Mar-2010 This property should be read only.
    // rejected persons
    Integer rejected = 0

    boolean containsPerson(Long id){
        personEventsTrack.containsKey(id)
    }

    // calculates source statistics
    void calculate(){
        added = personEventsTrack.size()
        processing = 0
        rejected = 0
        personEventsTrack.each{key, events->
            if (events.processing) processing++
            if (events.rejected) rejected++
        }
    }
}
