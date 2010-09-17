package selenium

import com.thoughtworks.selenium.GroovySelenium
import selenium.SeleniumTestCase

public class BaseMacros {

    SeleniumTestCase testCase

    GroovySelenium selenium

    void logout() {
        selenium.openAndWait("/login/signout")
    }

    void waitBlockUI() {
        testCase.waitFor {
            !selenium.isElementPresent("//div[@class='blockUI blockMsg blockPage']")
        }
    }

    void gotoHome() {
        selenium.openAndWait("home")
        testCase.baseMacros.waitBlockUI()
    }

    static final String TESTHR_USERNAME = "testhr1"

    static final String TESTHR_PASSWORD = "testhr1"

    void login(String username, String password) {
        testCase.baseMacros.logout()
        selenium.type("j_username", "testhr1")
        selenium.type("j_password", "testhr1")
        selenium.clickAndWait("id=loginButton")
        selenium.runScript("\$.fx.off=true")
        selenium.runScript("\$.ajaxSetup({async: false})")
        testCase.baseMacros.waitBlockUI()
    }

    void searchPerson(String searchText) {
        selenium.type('id=searchTextField', searchText)
        selenium.click('id=searchButton')
        testCase.baseMacros.waitBlockUI()
    }

    void assertJS(String condition) {
        String setTrueScript = "\$('<div>').attr('id', 'selenium-boolean-value').appendTo(\$('html'))"
        String setFalseScript = "\$('#selenium-boolean-value').remove()"
        selenium.runScript(setFalseScript)
        selenium.runScript("if (${condition}) {${setTrueScript}} else {${setFalseScript}}")
        testCase.assertTrue(selenium.isElementPresent("id=selenium-boolean-value"))
    }

    void resetSession() {
        selenium.runScript('document.cookie="JSESSIONID=; espires=-1; path=/"')
    }

}