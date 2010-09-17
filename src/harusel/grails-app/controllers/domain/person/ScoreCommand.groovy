package domain.person

import domain.Criterion
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.MapUtils
import org.apache.commons.lang.StringUtils
import security.UserContextHolder

class ScoreCommand {

    def criterionService

    static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    // CR: major dkranchev 02-Mar-2010 Bad name.
    Map val = MapUtils.lazyMap([:], FactoryUtils.constantFactory(''));
    Map comment = MapUtils.lazyMap([:], FactoryUtils.constantFactory(''));

    Long personId
    Long id
    String name
    String date
    String sheetComment

    // CR: major dkranchev 02-Mar-2010 Extract closures in methods for better code readability.
    static constraints = {
        val(validator: {val, obj, errors ->
            def notBlankValueCounter = 0
            val.each {criterionId, value ->
                if (StringUtils.isBlank(value)) {
                    return
                }
                notBlankValueCounter++
                if (!value.isInteger()) {
                    errors.rejectValue("val[$criterionId]", 'typeMismatch.java.lang.Integer')
                    return
                }
                value = value.toInteger()
                if (!(value in (0..2))) {
                    errors.rejectValue("val[$criterionId]", 'person.score.point.value.invalid')
                }
            }
            if (!notBlankValueCounter) {
                errors.rejectValue('val', 'person.score.empty.sheet')
            }
        })
        date(blank: false, nullable: false, validator: {val, obj, errors ->
            try {
                dateFormat.parse(val);
            } catch (ParseException e) {
                errors.rejectValue('date', 'person.score.sheet.date.invalid')
            }
        })
        name(blank: false, nullable: false, validator: {val, obj, errors ->
            def sameSheets = ScoreSheet.withCriteria {
                eq('name', name)
                try {
                    eq('date', dateFormat.parse(obj.date))
                } catch (ParseException e) {
                    // CR: major dkranchev 02-Mar-2010 Ignored exception.
                }
                if (obj.id) {
                    not {
                        eq('id', obj.id)
                    }
                }
                person {
                    eq('id', obj.personId)
                }
                user {
                    eq('id', UserContextHolder.contextUser.id)
                }
            }
            if (sameSheets) {
                errors.rejectValue('name', 'person.score.sheet.allready.exist');
                return
            }
            return
        })
        sheetComment(blank: true, nullable: true)
    }

    // TODO: Move to ScoreController.

    ScoreSheet createAndSaveScoreSheet() {
        def scoreSheet
        if (id != null) {
            scoreSheet = ScoreSheet.get(id)
        } else {
            scoreSheet = new ScoreSheet()
        }
        scoreSheet.name = name
        // CR: normal dkranchev 02-Mar-2010 Exception is not catched.
        scoreSheet.date = dateFormat.parse(date)
        scoreSheet.comment = sheetComment
        scoreSheet.user = UserContextHolder.contextUser
        scoreSheet.person = Person.get(personId)

        scoreSheet.save();
        // delete old scorepoints.
        if (scoreSheet.id) {
            ScorePoint.executeUpdate("delete ScorePoint s where s.score = :score", [score: scoreSheet]);
        }

        val.each {criterionId, value ->
            if (value == null || value == '') {
                return
            }
            value = value.toInteger();
            new ScorePoint(
                criterion: Criterion.get(criterionId.toLong()),
                value: value,
                comment: comment[criterionId]?.toString(),
                score: scoreSheet,
            ).save()
        }
        scoreSheet
    }

    static ScoreCommand getInstance(ScoreSheet sheet) {
        def command = new ScoreCommand(
            name: sheet.name,
            date: dateFormat.format(sheet.date),
            sheetComment: sheet.comment,
            id: sheet.id,
            personId: sheet.person.id
        )
        def points = ScorePoint.findAllByScore(sheet, [cache:true])

        points.each {ScorePoint point ->
            command.val[point.criterion.id.toString()] = point.value
            command.comment[point.criterion.id.toString()] = point.comment
        }
        command
    }

}
