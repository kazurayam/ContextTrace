import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import com.kazurayam.ks.ContextTrace
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/**
 * main/demo
 */

println "ContextTrace.getTestSuiteId(): " + ContextTrace.getTestSuiteId()
println "ContextTrace.escapedTestSuiteId(): " + ContextTrace.escapedTestSuiteId()
println "ContextTrace.getCurrentTestCaseId(): " + ContextTrace.getCurrentTestCaseId()
println "ContextTrace.getEscapedCurrentTestCaseId(): " + ContextTrace.getEscapedCurrentTestCaseId()
println "ContextTrace.getTestCaseIdStack(): " + ContextTrace.getTestCaseIdStack()
println "ContextTrace.escapedTestCaseIdChain(): " + ContextTrace.escapedTestCaseIdChain()

WebUI.callTestCase(findTestCase("main/demo_sub"), [:])
