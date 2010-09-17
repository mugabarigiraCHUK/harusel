package selenium

import com.thoughtworks.selenium.GroovySelenium
import selenium.SeleniumTestCase

public class PersonMacros {

    SeleniumTestCase testCase

    GroovySelenium selenium

    void addPerson(String fullName) {
        selenium.click("id=btnAddNew")
        testCase.waitFor {
            selenium.isElementPresent("id=addPersonNameField")
        }
        selenium.type("id=addPersonNameField", fullName)
        testCase.waitFor {
            selenium.isElementPresent("id=addPersonPopupSubmit")
        }
        selenium.click("id=addPersonPopupSubmit")
        testCase.baseMacros.waitBlockUI()
        selenium.click("id=add")
        testCase.baseMacros.waitBlockUI()
    }

    /**
     * WARNING:
     * Use it if you know that person you are searching is exists,
     * Searching for add person is used at this method
     */
    void strongSearch(String fullName) {
        selenium.click("id=btnAddNew")
        testCase.waitFor {
            selenium.isElementPresent("id=addPersonNameField")
        }
        selenium.type("id=addPersonNameField", fullName)
        testCase.waitFor {
            selenium.isElementPresent("id=addPersonPopupSubmit")
        }
        selenium.click("id=addPersonPopupSubmit")
        testCase.baseMacros.waitBlockUI()
    }

    void setFullName(String fullName) {
        selenium.type("id=fullName", fullName)
    }

    void setBirthYear(Integer year) {
        selenium.select("id=birthDate", "label=${year}")
    }

    void setEmails(String emails) {
        selenium.type("id=emails", emails)
    }

    void setPhones(String phones) {
        selenium.type("id=phones", phones)
    }

    void setVacancies(List<String> vacancies) {
        selenium.click("id=editVacancies")
        testCase.baseMacros.waitBlockUI()
        selenium.runScript("\$('.ui-dialog:visible table tr input[type=checkbox][checked]').click()")
        vacancies.each() {String vacancy ->
            selenium.runScript("\$('.ui-dialog:visible table tr:contains(${vacancy}) input[type=checkbox]').click()")
        }
        selenium.runScript("\$('.ui-dialog:visible button:last').click()")
    }

    void setSource(String source) {
        selenium.click("id=editSource")
        testCase.baseMacros.waitBlockUI()
        selenium.runScript("\$('.ui-dialog:visible table tr:contains(${source}) input[type=radio]').click()")
        selenium.runScript("\$('.ui-dialog:visible button:last').click()")
    }

    void save() {
        selenium.click("id=save")
        testCase.baseMacros.waitBlockUI()
    }

    static final String TIMELINE_TAB = "События"
    static final String INFO_TAB = "Общая информация"
    static final String DOCUMENTS_TAB = "Документы"
    static final String SCORES_TAB = "Оценки"
    static final String NOTES_TAB = "Заметки"

    void gotoTab(String tabName) {
        selenium.runScript("\$('.tabTitle:contains(${tabName}) a').click()")
        testCase.baseMacros.waitBlockUI()
    }

    /**
     * Sets person stage from person page
     * @param newStage name of new stage
     * @param comment comment
     */
    void setStage(String newStage, String comment) {
        selenium.click("id=setStageButton")
        selenium.runScript("\$('.ui-dialog:visible select option[selected]').removeAttr('selected')")
        selenium.runScript("\$('.ui-dialog:visible select option:contains(${newStage})').attr('selected', true)")
        selenium.runScript("\$('.ui-dialog:visible textarea').attr('value', '${comment}')")
        selenium.runScript("\$('.ui-dialog:visible button:last').click()")
        testCase.baseMacros.waitBlockUI()
    }

    /**
     * Mark person from person list
     * @param fullName full name of the person
     */
    void markPerson(String fullName) {
        testCase.baseMacros.searchPerson(fullName)
        selenium.runScript("\$('#mainPane table tr:contains(${fullName}) input[type=checkbox]').click()")
        selenium.runScript("\$('#mainPane table tr:contains(${fullName}) input[type=checkbox]').change()")
    }

    /**
     * Goto to the page of specified person.
     * @param fullName full name of the person
     */
    void gotoPersonPage(String fullName) {
        selenium.runScript("\$('#mainPane table tr a:contains(${fullName})').click()")
        testCase.baseMacros.waitBlockUI()
    }

}
