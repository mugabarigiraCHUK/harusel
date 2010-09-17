import selenium.BaseMacros
import selenium.SeleniumTestCase

class NotificationTests extends SeleniumTestCase {

    void testNotificationTests() throws Exception {
        baseMacros.login(BaseMacros.TESTHR_USERNAME, BaseMacros.TESTHR_PASSWORD)
        personMacros.addPerson("Пользователь для NotificationTests")
        personMacros.setStage("Ожидание резюме", "Тест NotificationTests установил статус пользователя в Ожидание резюме")
        personMacros.save()
        baseMacros.gotoHome()
        assertEquals(1, greenMail.getReceivedMessages().size())
    }
}
