import selenium.SeleniumTestCase

/**
 * This test suite tries interact with application after session expires
 *
 */
public class SessionExpirationTests extends SeleniumTestCase {


    /**
     * Tests that after session expiring all pages requests are redirected to login page
     */
    public void testGetAnotherPage() {
        baseMacros.login("testhr1", "testhr1")

        baseMacros.resetSession()

        selenium.open("/")

        verifyTrue(selenium.isElementPresent("id=j_username"))

    }

    /**
     * Tests that after session expiring ajax requests are redirected to login page
     */
    public void testGetAjaxData() {
        baseMacros.login("testhr1", "testhr1")
        waitFor {
            selenium.isElementPresent('css=a.linkToPersonInfo')
        }
        baseMacros.resetSession()
        // try get person general info
        selenium.click('css=a.linkToPersonInfo')
        baseMacros.waitBlockUI()

        verifyTrue(selenium.isElementPresent("id=j_username"))
    }

    /**
     * Tests that after session expiring file uploading is redirected to login page
     * WARNING: To permit file uploading set mozilla prefs 'signed.applets.codebase_principal_support' to TRUE
     *      TIP: enter in addres promt: about:config. 
     */
    public void testFileUpload() {
        baseMacros.login("testhr1", "testhr1")
        waitFor {
            selenium.isElementPresent('css=a.linkToPersonInfo')
        }
        selenium.isElementPresent('css=a.linkToPersonInfo')
        selenium.click('css=a.linkToPersonInfo')
        baseMacros.waitBlockUI()

        selenium.click('id=addDoc')
        baseMacros.waitBlockUI()

        baseMacros.resetSession()

        File file = File.createTempFile("tempFileToUploadTest", ".tmp");
        file << "temp file!"

        selenium.type('documents[0].file', file.path)
        selenium.type('documents[0].typeName', 'test uploading')
        selenium.click('css=div.ui-dialog button:nth-child(2)')
        baseMacros.waitBlockUI()

        file.delete();

        verifyTrue(selenium.isElementPresent("id=j_username"))

    }
}