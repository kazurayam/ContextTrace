package com.kazurayam.ks

import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNull

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

@RunWith(JUnit4.class)
public class ContextTraceTest {

	@Test
	void test_getRootTestCaseId() {
		String rootTcId = ContextTrace.getRootTestCaseId()
		assertEquals("Test Cases/test/com.kazurayam.ks/ContextTraceTestRunner", rootTcId)
	}

	@Test
	void test_getEscapedRootTestCaseId() {
		String escaped = ContextTrace.getEscapedRootTestCaseId()
		assertEquals("test-com.kazurayam.ks-ContextTraceTestRunner", escaped)
	}

	@Test
	void test_getTestCaseCallList() {
		Stack<String> stack = WebUI.callTestCase(findTestCase("Test Cases/main/TC1"), [:])
		//println tcIdList
		assertEquals(5, stack.size())
		assertEquals(stack.get(0), 'Test Cases/test/com.kazurayam.ks/ContextTraceTestRunner')
		assertEquals(stack.get(1), 'Test Cases/main/TC1')
		assertEquals(stack.get(2), 'Test Cases/main/shared/Level 1')
		assertEquals(stack.get(3), 'Test Cases/main/shared/Level 2')
		assertEquals(stack.get(4), 'Test Cases/main/shared/Level 3')
	}

	@Test
	void test_getCurrentTestCaseId() {
		assertEquals("Test Cases/test/com.kazurayam.ks/ContextTraceTestRunner", ContextTrace.getCurrentTestCaseId())
	}

	//-------------------------------------------------------------------------

	@Test
	void test_trimTestCases() {
		assertEquals('foo', ContextTrace.escape('Test Cases/foo'))
	}

	@Test
	void test_trimTestSuites() {
		assertEquals('bar', ContextTrace.escape('Test Suites/bar'))
	}

	@Test
	void test_whitespace() {
		assertEquals('a_b', ContextTrace.escape('a b'))
	}

	@Test
	void test_whitespaces() {
		assertEquals('a_b', ContextTrace.escape('a   b'))
	}

	@Test
	void test_greaterThan() {
		assertEquals('a-b', ContextTrace.escape('a<b'))
	}

	@Test
	void test_lessThan() {
		assertEquals('a-b', ContextTrace.escape('a>b'))
	}

	@Test
	void test_colon() {
		assertEquals('a-b', ContextTrace.escape('a:b'))
	}

	@Test
	void test_doubleQuote() {
		assertEquals('a-b', ContextTrace.escape('a\"b'))
	}

	@Test
	void test_forwardSlash() {
		assertEquals('a-b-c', ContextTrace.escape('a/b/c'))
	}

	@Test
	void test_backSlash() {
		assertEquals('a-b-c', ContextTrace.escape('a\\b\\c'))
	}

	@Test
	void test_verticalBar() {
		assertEquals('a-b', ContextTrace.escape('a|b'))
	}

	@Test
	void test_question() {
		assertEquals('a-b', ContextTrace.escape('a?b'))
	}

	@Test
	void test_asterisk() {
		assertEquals('a-b', ContextTrace.escape('a*b'))
	}

	@Test
	void test_hyphens() {
		assertEquals('a-b', ContextTrace.escape('a---b'))
	}

	@Test
	void test_realistic() {
		assertEquals('Main_Test_Cases-TC2_Verify_Successful_Appointment',
				ContextTrace.escape('Test Cases/Main Test Cases/TC2_Verify Successful Appointment'))
	}

	@Test
	void test_non_latin_characters() {
		assertEquals('標準問題-テスト_その１',
				ContextTrace.escape('Test Cases/標準問題/テスト その１'))
	}

	@Test
	void test_null() {
		assertNull(ContextTrace.escape(null))
	}
}
