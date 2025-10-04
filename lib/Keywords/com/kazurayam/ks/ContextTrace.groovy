package com.kazurayam.ks

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.keyword.BuiltinKeywords
import com.kms.katalon.core.keyword.internal.KeywordExecutor
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.util.KeywordUtil

public class ContextTrace {

	protected static String CURRENT_TESTSUITE_ID = null
	protected static String ROOT_TESTCASE_ID = null
	protected static Stack<String> TESTCASE_CALL_STACK = null

	/**
	 * Constructor does nothing.
	 * It is declared public because Katalon Studio requires a Keyword classes to have a public constructor.
	 */
	ContextTrace() {}

	/**
	 * record the ID of latest Test Suite
	 * this method should be called by a TestListener
	 * @param tsContext
	 */
	static final void setTestSuiteContext(TestSuiteContext tsContext) {
		CURRENT_TESTSUITE_ID = tsContext.getTestSuiteId()
	}

	/**
	 * @return if the caller Test Case is running inside a scope of a Test Suite, then return true.
	 * Otherwise, return false; this means, you ran the Test Case directly without wrapping Test Suite.
	 */
	static final boolean insideTestSuiteScope() {
		return (CURRENT_TESTSUITE_ID != null)
	}

	/**
	 * record the ID of latest Test Case
	 * this method should be called by a TestListener
	 * @param tcContext
	 */
	static final void setTestCaseContext(TestCaseContext tcContext) {
		ROOT_TESTCASE_ID = tcContext.getTestCaseId()
		TESTCASE_CALL_STACK = new Stack<String>()
		TESTCASE_CALL_STACK.push(tcContext.getTestCaseId())
		modifyCallTestCaseKeyword(TESTCASE_CALL_STACK)
	}

	/**
	 * Modify the implementation of `callTestCase(TestCase,...)` keyword.
	 * Once modified, a call to `callTestCase()` will record the id of called Test Case,
	 * which is accessible for the called Test Case itself.
	 *
	 * @param callStack
	 */
	private static final void modifyCallTestCaseKeyword(Stack<String> callStack) {
		BuiltinKeywords.metaClass.'static'.callTestCase = { TestCase calledTestCase, Map binding, FailureHandling flowControl ->
			callStack.push(calledTestCase.getTestCaseId())
			Object result = (Object)KeywordExecutor.executeKeywordForPlatform(
					KeywordExecutor.PLATFORM_BUILT_IN, "callTestCase", calledTestCase, binding, flowControl)
			callStack.pop()
			return result
		}
		BuiltinKeywords.metaClass.'static'.callTestCase = { TestCase calledTestCase, Map binding ->
			callStack.push(calledTestCase.getTestCaseId())
			Object result = (Object)KeywordExecutor.executeKeywordForPlatform(
					KeywordExecutor.PLATFORM_BUILT_IN, "callTestCase", calledTestCase, binding)
			callStack.pop()
			return result
		}
	}

	/**
	 * E.g, "Test Suites/improved"
	 *
	 * This method may return null when the Test Case was executed out of a Test Suite scope.
	 */
	@Keyword
	static final String getTestSuiteId() {
		if (CURRENT_TESTSUITE_ID == null) {
			KeywordUtil.markWarning("getTestSuiteID() was called while CURRENT_TESTSUITE_ID is null")
		}
		return CURRENT_TESTSUITE_ID
	}

	/**
	 * When getTestSuiteId() returns "Test Sites/A/B/C", then getEscapedTestSuiteId() returns "A-B-C"
	 *
	 * If getTestSuiteId() returns null, then getEscapedTestSuiteId() returns an empty string ""
	 */
	@Keyword
	static String getEscapedTestSuiteId() {
		if (getTestSuiteId() != null) {
			return escape(getTestSuiteId())
		} else {
			return ""
		}
	}

	/**
	 * alias to getEscapedTestSuiteId()
	 */
	@Keyword
	static String escapedTestSuiteId() {
		return getEscapedTestSuiteId()
	}

	/**
	 * returns the ID of the Test Case which was immediately invoked by Katalon Studio.
	 * If "Test Cases/main/demo" calls "Test Cases/main/demo_sub", and the "demo_sub" called
	 * this method, then this method returns "Test Cases/main/demo", not "demo_sub".
	 */
	@Keyword
	static final String getRootTestCaseId() {
		if (ROOT_TESTCASE_ID != null) {
			return ROOT_TESTCASE_ID
		} else {
			throw new IllegalStateException("setTestCaseContext(TestCaseContext) required")
		}
	}

	/**
	 * returns the value returned by escape(getRootTestCaseId())
	 */
	@Keyword
	static String getEscapedRootTestCaseId() {
		return escape(getRootTestCaseId())
	}

	/**
	 * returns the ID of the Test Case script which immediately calls this method.
	 * If "Test Cases/main/demo" calls "Test Cases/main/demo_sub", and the "demo_sub" called
	 * this method, then this method returns "Test Cases/main/demo_sub", not "demo"
	 */
	@Keyword
	static final String getCurrentTestCaseId() {
		if (ROOT_TESTCASE_ID != null) {
			return TESTCASE_CALL_STACK.get(TESTCASE_CALL_STACK.size() - 1)
		} else {
			throw new IllegalStateException("setTestCaseContext(TestCaseContext) required")
		}
	}

	/**
	 * returns escape(getCurrentTestCaseId())
	 */
	@Keyword
	static String getEscapedCurrentTestCaseId() {
		return escape(getCurrentTestCaseId())
	}

	/**
	 * Make a clone of java.util.Stack<String> instance which contains the TestCaseId.
	 * The returned Stack represents the chain of caller-callee by `WebUI.callTestCase(TestCase)` keyword.
	 */
	@Keyword
	static final Stack<String> getTestCaseIdStack() {
		Stack stack = new Stack<String>()
		for (String entry in TESTCASE_CALL_STACK) {
			stack.push(entry)
		}
		return stack
	}

	/**
	 * If "Test Cases/main/demo" calls "Test Cases/main/demo_sub" and the "demo_sub" called this method,
	 * then a string 
	 * 
	 * <code>main-demo,main-demo_sub</code>
	 * 
	 * is returned.
	 * The series of escaped TestCaseId are concatenated with a delimiter character.
	 * The delimiter parameter is optional, will default to a comma (,)
	 */
	@Keyword
	static String getEscapedTestCaseIdChain(String delimiter = ',') {
		List<String> list = new ArrayList<>()
		for (String entry in TESTCASE_CALL_STACK) {
			list.add(escape(entry))
		}
		return list.join(delimiter)
	}

	/**
	 * alias to getEscapedTestCaseIdChain()
	 */
	@Keyword
	static String escapedTestCaseIdChain(String delimiter = ',') {
		return getEscapedTestCaseIdChain(delimiter)
	}

	/**
	 * prints a message string into STDOUT.
	 * the message shows the values returned by
	 * <ol>
	 * <li>ContextTrace.getTestSuiteId()</li>
	 * <li>ContextTrace.escapedTestSuiteId()</li>
	 * <li>ContextTrace.getCurrentTestCaseId()</li>
	 * <li>ContextTrace.getEscapedCurrentTestCaseId</li>
	 * <li>ContextTrace.getTestCaseIdStack().toString()</li>
	 * <li>ContextTrace.escapedTestCaseIdChain()</li>
	 * </ol>
	 *
	 * The output could be as follows, for example:
	 * <code>
	 * ContextTrace.getTestSuiteId():                Test Suites/runDemo
	 * ContextTrace.escapedTestSuiteId():            runDemo
	 * ContextTrace.getCurrentTestCaseId():          Test Cases/main/demo_sub
	 * ContextTrace.getEscapedCurrentTestCaseId():   main-demo_sub
	 * ContextTrace.getTestCaseIdStack():            [Test Cases/main/demo, Test Cases/main/demo_sub]
	 * ContextTrace.escapedTestCaseIdChain():        main-demo,main-demo_sub
	 * </code>
	 */
	@Keyword
	static void status() {
		StringBuilder sb = new StringBuilder()
		sb.append("ContextTrace.getTestSuiteId():                ${ContextTrace.getTestSuiteId()}\n")
		sb.append("ContextTrace.escapedTestSuiteId():            ${ContextTrace.escapedTestSuiteId()}\n")
		sb.append("ContextTrace.getCurrentTestCaseId():          ${ContextTrace.getCurrentTestCaseId()}\n")
		sb.append("ContextTrace.getEscapedCurrentTestCaseId():   ${ContextTrace.getEscapedCurrentTestCaseId()}\n")
		sb.append("ContextTrace.getTestCaseIdStack():            ${ContextTrace.getTestCaseIdStack().toString()}\n")
		sb.append("ContextTrace.escapedTestCaseIdChain():        ${ContextTrace.escapedTestCaseIdChain()}")
		println "${sb.toString()}"
	}


	/**
	 * <p>Convert the given string as "id" parameter into a new string which is suitable as a part of file path.</p>
	 *
	 * <p>This method is declared "non final" intentionally. You can override this "escape" method.</p>
	 *
	 * <ol>
	 * <li>If the id starts with "Test Case/", then the part will be trimmed off.
	 *    E.g, "Test Cases/TC1" -> "TC1"</li>
	 *
	 * <li>If the id starts with "Test Suites/", then the part will be trimmed off.
	 *    E.g, "Test Suites/TS1" -> "TS1"</li>
	 *
	 * <li>A whitespace character is translated into a underbar '_' character.
	 *    E.g, "Test Cases/main part/TC1" -> "main_part-TC1"</li>
	 *
	 * <li>Two or more consecutive whitespace characters are translated into a single underbar '_' character.
	 *    E.g, "Multiple    whitespaces    here" -> "Multiple_whitespaces_here"</li>
	 *
	 * <li>Several characters, which are NOT allowed in the file path on Windows,
	 *     are translated into a hyphen '-' character
	 *    <ul>
	 *    <li>&lt; smaller than</li>
	 *    <li>&gt; larger than</li>
	 *    <li>: colon</li>
	 *    <li>" double quote</li>
	 *    <li>/ forward slash</li>
	 *    <li>\ back slash</li>
	 *    <li>| vertical bar</li>
	 *    <li>? question</li>
	 *    <li>* asterisk</li>
	 *    </ul>
	 *    </li>
	 *
	 * <li>Finally, any consecutive hyphen characters are translated into a single hyphen character</li>
	 *
	 * </ol>
	 *
	 * <p>Please note that a forward slash / and a back slash \ will be replaced with a hyphen.
	 *    <ul>
	 *    <li>E.g, "Test Cases/main/shared/Level 1" -> "main-shared-Level_1"</li>
	 *    </ul>
	 *    Therefore the string value returned will look a flat name, not a tree-like structure anymore. </p>
	 *
	 *
	 * <p>If the given id is null, then the pack(id) will silently return null.</p>
	 *
	 *
	 * @param id
	 * @return a string converted from the given id
	 */
	@Keyword
	static String escape(String id) {
		if (id != null) {
			return id.replaceAll('^Test Cases/', '')
					.replaceAll('^Test Suites/', '')
					.replaceAll('[\\s]+', '_')
					.replaceAll('[<>:\"/\\\\|?*]', '-')
					.replaceAll('[\\-]+', '-')
		} else {
			return null
		}
	}
}
