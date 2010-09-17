import org.codehaus.groovy.grails.test.DefaultGrailsTestHelper
import org.codehaus.groovy.grails.test.DefaultGrailsTestRunner

includeTargets << grailsScript("_GrailsClean")
includeTargets << grailsScript("_GrailsRun")

// The four test phases that we can run.
unitTests = ['unit']
seleniumTests = ['selenium']

// The phases that we will run on this execution. Override this in your
// own scripts to control the phases and their order.
phasesToRun = [/*'unit', */ 'selenium']

offline = false

// A list of test names. These can be of any of this forms:
//
//   org.example.*
//   org.example.**.*
//   MyService
//   MyService.testSomeMethod
//   org.example.other.MyService.testSomeMethod
//
// The default pattern runs all tests.
testNames = buildConfig.grails.testing.patterns ?: ['**.*']

// Controls which result formats are generated. By default both XML
// and plain text files are created. You can override this in your
// own scripts.
reportFormats = ["xml", "plain"]

// If true, only run the tests that failed before.
reRunTests = false

// Where the report files are created.
testReportsDir = grailsSettings.testReportsDir

// Set up an Ant path for the tests.
ant.path(id: "grails.test.classpath", testClasspath)

createTestReports = true
compilationFailures = []

testHelper = null
testsFailed = false

target('default': "Run a Grails applications unit tests") {
    depends(checkVersion, configureProxy, parseArguments, cleanTestReports)

    if (argsMap["params"])
        testNames = argsMap["params"]
    if (argsMap["xml"]) {
        reportFormats = ["xml"]
        createTestReports = false
    }
    else {
        createTestReports = !argsMap["no-reports"]
    }

    offline = argsMap["offline"]
    println "offline=$offline"

    reRunTests = argsMap["rerun"]

    allTests()
}

target(allTests: "Runs the project's tests.") {
    if (!offline) {
        depends(compile)
    }

    ant.mkdir(dir: testReportsDir)
    ant.mkdir(dir: "${testReportsDir}/html")
    ant.mkdir(dir: "${testReportsDir}/plain")

    // If we are to run the tests that failed, replace the list of
    // test names with the failed ones.
    if (reRunTests)
        testNames = getFailedTests()

    // If no phases are explicitly configured, run them all.
    if (!phasesToRun)
        phasesToRun = ["unit", "selenium"]

    event("TestPhasesStart", [phasesToRun])

    // This runs the tests and generates the formatted result files.
    testRunner = new DefaultGrailsTestRunner(testReportsDir, reportFormats)

    // Process the tests in each phase that is configured to run.
    phasesToRun.each {String phase ->
        // Skip this phase if there are no test types registered for it.
        def testTypes = this."${phase}Tests"
        if (!testTypes)
            return

        // Add a blank line before the start of this phase so that it
        // is easier to distinguish
        println()

        event("StatusUpdate", ["Starting $phase tests"])
        event("TestPhaseStart", [phase])

        // Do whatever preparation is needed to run the tests in this
        // phase. The method/closure should return a test helper.
        testHelper = this."${phase}TestsPreparation"()

        // Now run all the tests registered for this phase.
        testTypes.each(processTests)

        // Perform any clean up required.
        this."${phase}TestsCleanUp"()

        event("TestPhaseEnd", [phase])
    }

    String msg = testsFailed ? "\nTests FAILED" : "\nTests PASSED"
    if (createTestReports) {
        produceReports()
        msg += " - view reports in ${testReportsDir}."
    }

    event("StatusFinal", [msg])

    event("TestPhasesEnd", [])

    return testsFailed ? 1 : 0
}

/**
 * Compiles and runs all the tests of the given type and then generates
 * the reports for them.
 * @param type The type of the tests to compile (not the test phase!)
 * For example, "unit", "jsunit", "webtest", etc.
 */
processTests = {String type ->
    println "Running tests of type '$type'"

    // First compile the test classes.
    compileTests(type)

    // Run them.
    runTests(type)

    // Process the results.
    createReports(type)
}

/**
 * Compiles all the test classes for a particular type of test, for
 * example "unit" or "webtest". Assumes that the source files are in
 * the "test/$type" directory. It also compiles the files to distinct
 * directories for each test type: "$testClassesDir/$type".
 * @param type The type of the tests to compile (not the test phase!)
 * For example, "unit", "jsunit", "webtest", etc.
 */
compileTests = {String type ->
    event("TestCompileStart", [type])

    def destDir = new File(grailsSettings.testClassesDir, type)
    ant.mkdir(dir: destDir.path)
    try {
        def classpathId = "grails.test.classpath"
        ant.groovyc(destdir: destDir,
            projectName: grailsAppName,
            encoding: "UTF-8",
            classpathref: classpathId) {
            javac(classpathref: classpathId, debug: "yes", encoding: 'UTF-8')
            src(path: "${basedir}/test/${type}")
        }
    }
    catch (Exception e) {
        event("StatusFinal", ["Compilation Error: ${e.message}"])
        return 1
    }

    event("TestCompileEnd", [type])
}

runTests = {String type ->
    def prevContextClassLoader = Thread.currentThread().contextClassLoader
    try {
        // Get all the test files to run for this test type.
        def testSuite = testHelper.createTests(testNames, type)
        if (testSuite.testCount() == 0) {
            event("StatusUpdate", ["No tests found in test/$type to execute"])
            return
        }

        // Set the context class loader to the one used to load the tests.
        Thread.currentThread().contextClassLoader = testHelper.currentClassLoader

        event("TestSuiteStart", [type])
        int testCases = testSuite.countTestCases()
        println "-------------------------------------------------------"
        println "Running ${testCases} $type test${testCases > 1 ? 's' : ''}..."

        def start = new Date()
        def result = testRunner.runTests(testSuite)
        def end = new Date()

        event("TestSuiteEnd", [type, testSuite])
        event("StatusUpdate", ["Tests Completed in ${end.time - start.time}ms"])

        def failedTestCount = result.errorCount() + result.failureCount()
        println "-------------------------------------------------------"
        println "Tests passed: ${result.runCount() - failedTestCount}"
        println "Tests failed: ${failedTestCount}"
        println "-------------------------------------------------------"

        // If any of the tests fail, we register the whole test run as
        // a failure.
        if (failedTestCount > 0)
            testsFailed = true

        return result
    }
    catch (Exception e) {
        event("StatusFinal", ["Error running $type tests: ${e.toString()}"])
        e.printStackTrace()
        testsFailed = true
        return null
    }
    finally {
        Thread.currentThread().contextClassLoader = prevContextClassLoader
    }
}

createReports = {String type ->
    // Reports are not currently done on a per-type basis.
}

/**
 * Prepare for the unit tests. Simply sets up the default test helper.
 */
unitTestsPreparation = {
    return new DefaultGrailsTestHelper(grailsSettings, classLoader, resolveResources)
}

/**
 * Prepare for the selenium tests. Starts up the test server.
 */
seleniumTestsPreparation = {
    if (!offline) {
        packageApp()
        runApp()
    }
    return new DefaultGrailsTestHelper(grailsSettings, classLoader, resolveResources)
}

/**
 * Clean up after the unit tests. Nothing to do.
 */
unitTestsCleanUp = {
}

/**
 * Clean up after the selenium tests. Shuts down the test server.
 */
seleniumTestsCleanUp = {
    stopServer()
}

resolveTestFiles = {Closure filter ->
    def testFiles = resolveTestResources {"file:${basedir}/test/unit/${it}.groovy"}
    testFiles.addAll(resolveTestResources {"file:${basedir}/test/unit/${it}.java"})
}

target(packageTests: "Puts some useful things on the classpath for integration tests.") {
    ant.copy(todir: new File(grailsSettings.testClassesDir, "selenium").path) {
        fileset(dir: "${basedir}", includes: metadataFile.name)
    }
    ant.copy(todir: grailsSettings.testClassesDir.path, failonerror: false) {
        fileset(dir: "${basedir}/grails-app/conf", includes: "**", excludes: "*.groovy, log4j*, hibernate, spring")
        fileset(dir: "${basedir}/grails-app/conf/hibernate", includes: "**/**")
        fileset(dir: "${basedir}/src/java") {
            include(name: "**/**")
            exclude(name: "**/*.java")
        }
        fileset(dir: "${basedir}/test/unit") {
            include(name: "**/**")
            exclude(name: "**/*.java")
            exclude(name: "**/*.groovy")
        }
        fileset(dir: "${basedir}/test/selenium") {
            include(name: "**/**")
            exclude(name: "**/*.java")
            exclude(name: "**/*.groovy")
        }
    }
}

target(produceReports: "Outputs aggregated xml and html reports") {
    ant.junitreport(todir: "${testReportsDir}") {
        fileset(dir: testReportsDir) {
            include(name: "TEST-*.xml")
        }
        report(format: "frames", todir: "${testReportsDir}/html")
    }
}

def getFailedTests() {
    File file = new File("${testReportsDir}/TESTS-TestSuites.xml")
    if (file.exists()) {
        def xmlParser = new XmlParser().parse(file)
        def failedTests = xmlParser.testsuite.findAll { it.'@failures' =~ /.*[1-9].*/ || it.'@errors' =~ /.*[1-9].*/}

        return failedTests.collect {
            String testName = it.'@name'
            testName = testName.replace('Tests', '')
            def pkg = it.'@package'
            if (pkg) {
                testName = pkg + '.' + testName
            }
            return testName
        }
    } else {
        return []
    }
}
