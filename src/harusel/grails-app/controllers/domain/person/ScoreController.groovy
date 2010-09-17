package domain.person

import domain.event.ScoreSheetEvent
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.springsecurity.Secured

// CR: normal dkranchev 02-Mar-2010 Javadoc missed.
@Secured(['ROLE_HR_ALLOWED', 'ROLE_PM_ALLOWED'])
class ScoreController {
    public static Logger log = Logger.getLogger(ScoreCommand.class)

    def index = { redirect(action: create, params: params) }
    def criterionService
    def notificationService

    def listAlone = {
        Person person = Person.get(params.id)
        render(view: "listAlone", model: getScoreListModel(person))
    }

    def list = {
        Person person = Person.get(params.id)
        render(view: "list", model: getScoreListModel(person))
    }

    def create = {
        Person person = Person.get(params.id)
        if (!person) {
            log.error("Can't find person with id='${params.id}'")
            // CR: major dkranchev 02-Mar-2010 How this error is rendered in UI?
            throw new IllegalArgumentException("illegal person id ${params.id}")
        }
        render(view: "edit", model: [command: new ScoreCommand(personId: person.id),
                criterions: criterionService.roots,
                visibleCriterions: criterionService.getCriterionIdListForPerson(person)]
        );
    }

    def edit = {
        Person person = Person.get(params.personId)
        if (!person) {
            log.error("Can't find person with id='${params.id}'")
            // CR: major dkranchev 02-Mar-2010 How this error is rendered in UI?
            throw new IllegalArgumentException("illegal person id ${params.id}")
        }
        def sheet = ScoreSheet.get(params.id)
        if (!sheet) {
            log.error("Can't find sheet with id='${params.id}'")
            // CR: major dkranchev 02-Mar-2010 How this error is rendered in UI?
            throw new IllegalArgumentException("illegal person id ${params.id}")
        }
        render(view: "edit", model: [command: ScoreCommand.getInstance(sheet),
                criterions: criterionService.roots,
                visibleCriterions: criterionService.getCriterionIdListForPerson(person)]
        );
    }

    def save = {ScoreCommand cmd ->
        if (!cmd.hasErrors()) {
            def score = cmd.createAndSaveScoreSheet()
            def event = new ScoreSheetEvent(
                    score: score,
                    date: new Date(),
                    user: score.user,
                    person: score.person
            )
            // CR: major dkranchev 02-Mar-2010 If event cannot be saved then notification should not be sent.
            // CR: major dkranchev 02-Mar-2010 This method should be transactional.
            event.save()
            notificationService.sendNotification(event)
            render('<div class="notify">' + message(code: 'score.added') + '</div>')
        } else {
            render(view: "edit", model: [command: cmd,
                    criterions: criterionService.roots,
                    visibleCriterions: criterionService.getCriterionIdListForPerson(Person.get(cmd.personId))]
            );
        }
    }

    def getSheetNames = {
        render view: 'sheetNames', model: [sheetNames: ScoreSheet.getNameList()]
    }

    private def getScoreListModel(Person person) {
        [
                person: person,
                criterions: criterionService.roots,
                sheets: getScoreSheetsForPerson(person),
                visibleCriterions: criterionService.getVisibleCriterionIdFor(person),
                sheetDateAvailableToEdit: new Date() - grailsApplication.config.score.available.editable.days
        ]
    }

    private def getScoreSheetsForPerson(person) {
        def scoreSheetMapByName = [:]
        def scoreSheetsMap = criterionService.getScoreSheetsForPerson(person)
        scoreSheetsMap.each {name, scoreSheets ->
            def scoreSheetsWithPoints = scoreSheets.collect { scoreSheet ->
                new Expando(
                            sheet: scoreSheet,
                            points: collectScorePointsFromSheet(scoreSheet)
                    )
            }
            scoreSheetMapByName.put(name, scoreSheetsWithPoints)
        }
        return scoreSheetMapByName
    }

    private def collectScorePointsFromSheet(ScoreSheet scoreSheet) {
        return ScorePoint.findAllByScore(scoreSheet, [cache:true])
    }
}
