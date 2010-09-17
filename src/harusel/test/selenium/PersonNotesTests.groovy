import com.thoughtworks.selenium.GroovySeleneseTestCase

class PersonNotesTests extends GroovySeleneseTestCase {

    @Override
    void setUp() throws Exception {
        super.setUp('http://localhost:8080/', '*chrome')
        setDefaultTimeout(30000)
        setCaptureScreenshotOnFailure(false)
    }

    void testPersonNotesTests() throws Exception {
        selenium.openAndWait('/login/signout')
        waitFor {
            selenium.isElementPresent('j_username')
        }
        selenium.type('j_username', 'testhr1')
        selenium.type('j_password', 'testhr1')
        selenium.clickAndWait('//input[@value=\'Login\']')
        selenium.runScript('$.ajaxSetup({async: false})')
        selenium.runScript('$.fx.off=true')
        waitFor {
            selenium.isElementPresent('id=1')
        }
        assertTrue(selenium.isElementPresent('id=1'))
        selenium.click('id=1')
        selenium.click('addComment')
        waitFor {
            selenium.isElementPresent('noteText')
        }
        selenium.type('noteText', 'Added from list')
        selenium.click('//button[2]')
        waitFor {
            selenium.isElementPresent('1')
        }
        selenium.click('1')
        selenium.click('addComment')
        waitFor {
            selenium.isElementPresent('noteText')
        }
        selenium.type('noteText', 'Cancelled')
        selenium.click('//button[1]')
        waitFor {
            selenium.isElementPresent('2')
        }
        selenium.click('2')
        selenium.click('addComment')
        waitFor {
            selenium.isElementPresent('noteText')
        }
        selenium.type('noteText', 'Multiple')
        selenium.click('//button[2]')
        selenium.click('link=Сычев Руслан')
        waitFor {
            selenium.isElementPresent('fullName')
        }
        selenium.click('addComment')
        waitFor {
            selenium.isElementPresent('noteText')
        }
        selenium.type('noteText', 'Added from card')
        selenium.click('//div[11]/button[2]')
        selenium.click('addComment')
        waitFor {
            selenium.isElementPresent('noteText')
        }
        selenium.type('noteText', 'Cancelled')
        selenium.click('//div[11]/button[1]')
        selenium.click('//div[@id=\'tabPane\']/ul/span[1]/li/a/span')
        selenium.click('addComment')
        waitFor {
            selenium.isElementPresent('noteText')
        }
        selenium.type('noteText', 'Added from events')
        selenium.click('//div[11]/button[2]')
        waitFor {
            selenium.isTextPresent('Added from events')
        }
        verifyTrue(selenium.isTextPresent('Added from card'))
        verifyTrue(selenium.isTextPresent('Multiple'))
        verifyTrue(selenium.isTextPresent('Added from list'))
        verifyFalse(selenium.isTextPresent('Cancelled'))
        selenium.click('//div[@id=\'tabPane\']/ul/span[5]/li/a/span')
        selenium.click('addComment')
        waitFor {
            selenium.isElementPresent('noteText')
        }
        selenium.type('noteText', 'Added from notes')
        selenium.click('//div[11]/button[2]')
        verifyTrue(selenium.isTextPresent('Added from notes'))
        verifyTrue(selenium.isTextPresent('Added from events'))
        verifyTrue(selenium.isTextPresent('Added from card'))
        verifyTrue(selenium.isTextPresent('Multiple'))
        verifyTrue(selenium.isTextPresent('Added from list'))
        verifyFalse(selenium.isTextPresent('Cancelled'))
    }
}
