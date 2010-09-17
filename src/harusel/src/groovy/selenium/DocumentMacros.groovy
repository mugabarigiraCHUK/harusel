package selenium

import com.thoughtworks.selenium.GroovySelenium
import selenium.SeleniumTestCase

public class DocumentMacros {

    SeleniumTestCase testCase

    GroovySelenium selenium

    static final String QUESTIONNAIRE_TYPE = "Анкета"
    static final String SUMMARY_RU_TYPE = "Резюме RU"
    static final String SUMMARY_EN_TYPE = "Резюме EN"
    static final String OTHER_TYPE = "другое"

    void uploadDocument(String localPath, String type, String typeName) {
        selenium.click("id=addDoc")
        testCase.baseMacros.waitBlockUI()
        selenium.runScript("\$('.ui-dialog:visible table tr:contains(${type}) input[type=radio]').click()")
        if (OTHER_TYPE == type) {
            selenium.runScript("\$('.ui-dialog:visible table tr:contains(${OTHER_TYPE}) input[type=text]').val('${typeName}')")
        }
        selenium.type("documents[0].file", localPath)
        selenium.runScript("\$('.ui-dialog:visible button:contains(Загрузить)').click()")
        testCase.baseMacros.waitBlockUI()
    }

    void removeDocuments(List<String> names) {
        // mark documents to remove
        names.each() {String name ->
            selenium.runScript("\$('#tabPane form:visible tr:contains(${name}) input[type=checkbox]').click()")
        }
        // click on the button
        selenium.click('id=removeDocuments')
        testCase.baseMacros.waitBlockUI()
    }

}
