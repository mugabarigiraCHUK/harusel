package service

import domain.Criterion
import domain.person.Person
import domain.person.ScoreSheet
import domain.person.ScorePoint
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CriterionService {

    def getRemovedCriterions(def criterionTreeAfterUpdate) {
        def listBeforeUpdate = Criterion.list()
        def flatList = []
        criterionTreeAfterUpdate.each {
            flatList << it
            flatList.addAll(it.children)
        }
        def removed = listBeforeUpdate.findAll {src ->
            flatList.find({ it.id == src.id }) == null
        }
        return removed
    }

    def getRoots() {
        Criterion.withCriteria {
            isNull('parent')
            order('childIndex', 'asc')
        }
    }

    def getCriterionIdListForPerson(Person person) {
        def ids = new HashSet()
        person.vacancies?.each {vacancy ->
            vacancy.criteria.each {criteria ->
                def criterion = criteria.criterion
                while (criterion) {
                    ids << criterion.id
                    criterion = criterion.parent
                }
            }
        }

        return ids
    }

    def getScoreSheetsForPerson(Person person) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat((String) ConfigurationHolder.config.person.scores.date.format);
        def commitedSheets = ScoreSheet.withCriteria {
            eq('person', person)
            order('date', 'desc')
        }
        def sheetsByName = [:]
        commitedSheets.each {sheet ->
            def sheetName = "${sheet.name} ${dateFormatter.format(sheet.date)}"
            if (!sheetsByName[sheetName]) {
                sheetsByName[sheetName] = []
            }
            sheetsByName[sheetName] << sheet
        }
        sheetsByName.each {name, sheets -> sheets.sort {it.user.fullName} }
        return sheetsByName
    }

    HashSet getVisibleCriterionIdFor(Person person) {
        def visibleCriterions = new HashSet()
        def sheetMapByName = getScoreSheetsForPerson(person)
        sheetMapByName.each {name, sheetList ->
            sheetList.each {scoreSheet ->
                ScorePoint.findAllByScore(scoreSheet, [cache:true]).each {scorePoint ->
                    def criterion = scorePoint.criterion
                    while (criterion) {
                        visibleCriterions << criterion.id
                        criterion = criterion.parent
                    }
                }
            }
        }
        visibleCriterions
    }
}
