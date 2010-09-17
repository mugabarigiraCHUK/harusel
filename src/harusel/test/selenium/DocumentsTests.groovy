import selenium.BaseMacros
import selenium.DocumentMacros
import selenium.PersonMacros
import selenium.SeleniumTestCase

public class DocumentsTests extends SeleniumTestCase {
    String FILE_CONTENT = "test-data"

    List<File> tempFiles

    @Override
    void setUp() {
        super.setUp()

        tempFiles = (0..5).collect() {Integer index ->
            File tmpFile = File.createTempFile("documents-tests-с русскими буквами", ".tmp")
            tmpFile << FILE_CONTENT
            return tmpFile
        }
    }

    @Override
    void tearDown() {
        super.tearDown()

        tempFiles.each() {File tmpFile -> tmpFile.delete()}
    }

    void testFastDocumentLinks() {
        baseMacros.login(BaseMacros.TESTHR_USERNAME, BaseMacros.TESTHR_PASSWORD)
        def testPersonName = "Пользователь для DocumentsTests.testFastDocumentLinks"
        personMacros.addPerson(testPersonName)
        personMacros.strongSearch(testPersonName)
        [
            [iconIndex: 0, documentType: DocumentMacros.QUESTIONNAIRE_TYPE],
            [iconIndex: 1, documentType: DocumentMacros.SUMMARY_RU_TYPE],
            [iconIndex: 2, documentType: DocumentMacros.SUMMARY_EN_TYPE],
        ].each {documentDescription ->
            baseMacros.assertJS("\$(\$('#personList img').get(${documentDescription.iconIndex})).parent('a').size()==0")
            personMacros.markPerson(testPersonName)
            documentMacros.uploadDocument(tempFiles[0].path, documentDescription.documentType, "")
            baseMacros.assertJS("\$(\$('#personList img').get(${documentDescription.iconIndex})).parent('a').size()>0")
        }

    }

    void testDocument() throws Exception {
        baseMacros.login(BaseMacros.TESTHR_USERNAME, BaseMacros.TESTHR_PASSWORD)
        personMacros.addPerson("Пользователь для DocumentsTests.testDocument")

        documentMacros.uploadDocument(tempFiles[0].path, DocumentMacros.OTHER_TYPE, "добавленный из списка")
        [
            [
                tabName: PersonMacros.TIMELINE_TAB,
                localPath: tempFiles[1].path,
                type: DocumentMacros.QUESTIONNAIRE_TYPE, typeName: "",
            ],
            [
                tabName: PersonMacros.INFO_TAB,
                localPath: tempFiles[2].path,
                type: DocumentMacros.SUMMARY_RU_TYPE, typeName: "",
            ],
            [
                tabName: PersonMacros.DOCUMENTS_TAB,
                localPath: tempFiles[3].path,
                type: DocumentMacros.SUMMARY_EN_TYPE, typeName: "",
            ],
            [
                tabName: PersonMacros.SCORES_TAB,
                localPath: tempFiles[4].path,
                type: DocumentMacros.OTHER_TYPE, typeName: "некоторый другой тип документов",
            ],
            [
                tabName: PersonMacros.NOTES_TAB,
                localPath: tempFiles[5].path,
                type: DocumentMacros.OTHER_TYPE, typeName: "совсем другой тип документов",
            ],
        ].each() {
            personMacros.gotoTab(it.tabName)
            documentMacros.uploadDocument(it.localPath, it.type, it.typeName)
        }

        personMacros.gotoTab(PersonMacros.DOCUMENTS_TAB)
        [
            "добавленный из списка",
            DocumentMacros.QUESTIONNAIRE_TYPE,
            DocumentMacros.SUMMARY_RU_TYPE,
            DocumentMacros.SUMMARY_EN_TYPE,
            "некоторый другой тип документов",
            "совсем другой тип документов"
        ].each() {baseMacros.assertJS("\$('#tabPane table:visible:contains(${it})').length > 0")}

        // remove documents
        documentMacros.removeDocuments((0..3).collect() {tempFiles[it].name})

        [
            "добавленный из списка",
            DocumentMacros.QUESTIONNAIRE_TYPE,
            DocumentMacros.SUMMARY_RU_TYPE,
            DocumentMacros.SUMMARY_EN_TYPE,
        ].each() {baseMacros.assertJS("\$('#tabPane table:visible:contains(${it})').length <= 0")}
        [
            "некоторый другой тип документов",
            "совсем другой тип документов"
        ].each() {baseMacros.assertJS("\$('#tabPane table:visible:contains(${it})').length > 0")}
    }

}