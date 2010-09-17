package domain.person

/**
 * Command with data for change state method.
 */
class ChangeStageCommand {
    String dialogViewName
    String comment
    boolean trivialChanges
    boolean composeEmail
    boolean unsubscribe
    boolean subscribeAll
    boolean trackDecisions
    String actionName
    List userIdListToSubscribe
    List personIdList
    Long stageId

    static constraints = {
        stageId(nullable: true)
        comment(blank: false)
        actionName(blank: true)
        userIdListToSubscribe(nullable: true)
    }
}
