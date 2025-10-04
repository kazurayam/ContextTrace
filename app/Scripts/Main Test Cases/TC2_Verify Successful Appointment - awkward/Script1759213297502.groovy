import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

WebUI.comment('Story: Book an appointment')

WebUI.comment('Given that the user has logged into their account')

WebUI.openBrowser(GlobalVariable.G_SiteURL)

WebUI.takeScreenshot('1-Main_Test_Cases-TC_Verify_Successful_Appointment_-_awkward.png')

WebUI.callTestCase(findTestCase('Common Test Cases/Login - awkward'), [('Username') : 'John Doe', ('Password') : 'ThisIsNotAPassword'],
	FailureHandling.STOP_ON_FAILURE)

WebUI.comment('And Appointment page is displayed')

if (true) {
	WebUI.selectOptionByLabel(findTestObject('Page_CuraAppointment/lst_Facility'), 'Hongkong CURA Healthcare Center', false)

	WebUI.check(findTestObject('Page_CuraAppointment/chk_Medicaid'))

	WebUI.check(findTestObject('Page_CuraAppointment/chk_Readmission'))

	WebUI.setText(findTestObject('Page_CuraAppointment/txt_VisitDate'), '27/12/2016')

	WebUI.setText(findTestObject('Page_CuraAppointment/txt_Comment'), 'Please make appointment as soon as possible.')
}

WebUI.comment('When he fills in valid information in Appointment page')

WebUI.takeScreenshot('3-Main_Test_Cases-TC_Verify_Successful_Appointment_-_awkward.png')

WebUI.click(findTestObject('Page_CuraAppointment/btn_BookAppointment'))

WebUI.verifyElementPresent(findTestObject('Page_AppointmentConfirmation/lbl_Facility'), 30)

WebUI.verifyTextPresent('Appointment Confirmation', false)

WebUI.comment('Then he should be able to book a new appointment')

if (true) {
	WebUI.verifyMatch('Hongkong CURA Healthcare Center', WebUI.getText(findTestObject('Page_AppointmentConfirmation/lbl_Facility')),
		false)

	WebUI.verifyMatch('Yes', WebUI.getText(findTestObject('Page_AppointmentConfirmation/lbl_HospitalReadmission')), false)

	WebUI.verifyMatch('Medicaid', WebUI.getText(findTestObject('Page_AppointmentConfirmation/lbl_Program')), false)

	WebUI.verifyMatch('27/12/2016', WebUI.getText(findTestObject('Page_AppointmentConfirmation/lbl_VisitDate')), false)

	WebUI.verifyMatch('Please make appointment as soon as possible.', WebUI.getText(findTestObject('Page_AppointmentConfirmation/lbl_Comment')),
		false)
}

WebUI.takeScreenshot('4-Main_Test_Cases-TC_Verify_Successful_Appointment_-_awkward.png')

WebUI.closeBrowser()