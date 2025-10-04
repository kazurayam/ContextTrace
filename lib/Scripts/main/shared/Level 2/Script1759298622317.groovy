import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import com.kazurayam.ks.ContextTrace
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

ContextTrace.status()

return WebUI.callTestCase(findTestCase('main/shared/Level 3'), [:], FailureHandling.OPTIONAL)
