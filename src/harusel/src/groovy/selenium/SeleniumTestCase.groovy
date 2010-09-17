package selenium

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.thoughtworks.selenium.GroovySeleneseTestCase
import selenium.BaseMacros
import selenium.DocumentMacros
import selenium.PersonMacros

public class SeleniumTestCase extends GroovySeleneseTestCase {
    // to avoid permission's issue on Linux use PORT great then 1024
    int MAIL_PORT = 55555

    BaseMacros baseMacros
    PersonMacros personMacros
    DocumentMacros documentMacros
    GreenMail greenMail

    @Override
    void setUp() {
        super.setUp('http://localhost:8080/', '*firefox')
        setDefaultTimeout(30000)
        setCaptureScreenshotOnFailure(true)
        greenMail = new GreenMail(new ServerSetup(MAIL_PORT, "127.0.0.1", "smtp"))
        greenMail.start()
        initMacros()
    }

    @Override
    void tearDown() {
        super.tearDown()
        greenMail.stop()
    }

    void initMacros() {
        baseMacros = new BaseMacros(testCase: this, selenium: selenium)
        personMacros = new PersonMacros(testCase: this, selenium: selenium)
        documentMacros = new DocumentMacros(testCase: this, selenium: selenium)
    }

}
