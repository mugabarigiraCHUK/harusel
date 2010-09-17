import selenium.BaseMacros
import selenium.PersonMacros
import selenium.SeleniumTestCase

public class ChangePersonTests extends SeleniumTestCase {

    void testChangePerson() throws Exception {
        baseMacros.login(BaseMacros.TESTHR_USERNAME, BaseMacros.TESTHR_PASSWORD)

        personMacros.addPerson("Пользователь для ChangePersonTests")

        personMacros.setFullName("Кармен Электра")
        personMacros.setBirthYear(1972)
        personMacros.setEmails("carmen@gmail.com; electra@gmail.com, carmen.electra@gmail.com electro-carmen@gmail.com")
        personMacros.setPhones("+ 7 913 762 6727; +7 383 332 8909")
        personMacros.setVacancies(["Manager", "Web designer", "Tester"])
        personMacros.setSource("Мой круг")
        personMacros.save()

        personMacros.gotoTab(PersonMacros.TIMELINE_TAB)

        [
                "изменил данные; новые значения:",
                "Фамилия имя отчество:",
                "Кармен Электра",
                "Год рождения:",
                "1972",
                "E-mails:",
                "carmen@gmail.com; electra@gmail.com; carmen.electra@gmail.com; electro-carmen@gmail.com ",
                "Телефоны:",
                "+ 7 913 762 6727; +7 383 332 8909",
                "Вакансии:",
                "Manager, Web designer, Tester",
                "Источник:",
                "Мой круг",
        ].each() { verifyTrue(selenium.isTextPresent(it)) }
    }

}