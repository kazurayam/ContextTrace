import com.kazurayam.ks.ContextTrace
import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext

class SetupContextTrace {
	@BeforeTestSuite
	def beforeTestSuite(TestSuiteContext context) {
		ContextTrace.setTestSuiteContext(context)
	}

	@BeforeTestCase
	def beforeTestCase(TestCaseContext context) {
		ContextTrace.setTestCaseContext(context)
	}
}
