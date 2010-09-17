import selenium.BaseMacros
import selenium.SeleniumTestCase

class SearchTests extends SeleniumTestCase {

    void testSearch() throws Exception {
        baseMacros.login(BaseMacros.TESTHR_USERNAME, BaseMacros.TESTHR_PASSWORD)

        ["Николаев", "Сидоров"].each() {String family ->
            ["Николай", "Сидор"].each() {String name ->
                personMacros.addPerson("${family} ${name}")
            }
        }

        [
                "Ник": [
                        "Николаев Николай",
                        "Николаев Сидор",
                        "Сидоров Николай",
                ],
                "Сид": [
                        "Сидоров Сидор",
                        "Сидоров Николай",
                        "Николаев Сидор",
                ],
                "Романов Николай": [
                        "Николаев Николай",
                        "Сидоров Николай",
                ],
                "Сидоров Роман": [
                        "Сидоров Николай",
                        "Сидоров Сидор",
                ],
        ].each() {String searchText, List foundPersons ->
            baseMacros.searchPerson(searchText)
            foundPersons.each() { verifyTrue(selenium.isTextPresent(it)) }
        }
    }

}
