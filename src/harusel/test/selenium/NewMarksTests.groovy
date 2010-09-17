import java.text.DateFormat
import java.text.SimpleDateFormat
import org.apache.log4j.Logger
import selenium.PersonMacros
import selenium.SeleniumTestCase

/**
 * Validates addition marks to person action
 */
public class NewMarksTests extends SeleniumTestCase {
    static Logger log = Logger.getLogger(NewMarksTests.class)

    static DateFormat dateFormatForScoreTitle = new SimpleDateFormat('MMM dd, yyyy');
    static DateFormat dateFormatForDateInputString = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * Tests new mark without mistakes (mark ~ [0..2])
     */
    public void testAddRightMark() {
        openMarkList("User1 for mark addition tests")

        def stageTitle = "New mark title"
        def date = new Date()
        def mark = "1"
        def comment = "new mark for 1"

        selenium.type("id=name", stageTitle)
        selenium.type("id=date", dateFormatForDateInputString.format(date))
        selenium.type("css=input.valueInput", mark)
        selenium.type("id=comment[1]", comment)

        selenium.click("css=div.ui-dialog button:nth-child(2)")
        baseMacros.waitBlockUI()
        assertTrue(!selenium.isElementPresent("id=date"))

        personMacros.gotoTab(PersonMacros.SCORES_TAB)

        def sheetName = "$stageTitle ${dateFormatForScoreTitle.format(date)}"

        assertTrue(selenium.isTextPresent(sheetName))
        assertTrue(selenium.isTextPresent(comment))
        baseMacros.assertJS('$.trim($("td.markCell").text())=="1"')
        baseMacros.assertJS('$.trim($("td.commentCell").text())=="' + comment + '"')

        personMacros.gotoTab(PersonMacros.TIMELINE_TAB)
        assertTrue(selenium.isTextPresent(stageTitle))
        assertEquals(1, greenMail.getReceivedMessages().size())

    }

    /**
     * Tests new mark with mistakes (mark is not in list [0..2])
     */
    public void testAddBadMark() {
        openMarkList("User2 for mark addition tests")
        def date = new Date()
        def stageTitleValue = "New mark title"
        def comment = "new mark for 1"
        def formatedDate = dateFormatForDateInputString.format(date)

        [
            [
                stageTitle: "",
                date: "",
                comment: "",
                mark: "",
                errorsNum: 3
            ],
            [
                stageTitle: stageTitleValue,
                date: "",
                comment: "",
                mark: "-1",
                errorsNum: 2
            ],
            [
                stageTitle: stageTitleValue,
                date: formatedDate,
                comment: "",
                mark: "-1",
                errorsNum: 1
            ],
            [
                stageTitle: stageTitleValue,
                date: formatedDate,
                comment: comment,
                mark: "3",
                errorsNum: 1
            ],
        ].each {
            if (it.stageTitle) {
                selenium.type("id=name", it.stageTitle)
            }
            if (it.date) {
                selenium.type("id=date", it.date)
            }
            if (it.mark) {
                selenium.type("id=val[1]", it.mark)
            }
            if (it.comment) {
                selenium.type("id=comment[1]", it.comment)
            }
            selenium.click("css=div.ui-dialog button:nth-child(2)")
            baseMacros.waitBlockUI()

            baseMacros.assertJS('$("div.errors").length==' + it.errorsNum)
        }
    }

    private def openMarkList(String personName) {
        baseMacros.login("testhr1", "testhr1")
        // this user already exists
        personMacros.strongSearch(personName)

        waitFor {
            selenium.isElementPresent('css=a.linkToPersonInfo')
        }
        selenium.click('css=a.linkToPersonInfo')
        baseMacros.waitBlockUI()

        selenium.click('id=addScore')
        baseMacros.waitBlockUI()
    }

}