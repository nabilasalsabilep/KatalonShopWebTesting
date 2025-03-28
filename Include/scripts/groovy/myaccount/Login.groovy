package myaccount
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When



class Login {

	@Given("User open the website")
	def Open_the_website() {

		WebUI.openBrowser('')

		WebUI.navigateToUrl('https://cms.demo.katalon.com/')

		WebUI.maximizeWindow()

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Header/ShopName')), 'Katalon Shop', false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Header/ShopShortDesc')), 'Katalon Ecommerce', false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Header/CartMenu')), 'CART', false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Header/CheckoutMenu')), 'CHECKOUT', false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Header/MyAccountMenu')), 'MY ACCOUNT', false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Header/SamplePageMenu')), 'SAMPLE PAGE', false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Header/ShopMenu')), 'SHOP', false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/ShopPage/ShopPageTitle')), 'Shop', false)

		println 'Successfully open the website'
	}

	@And("User open Login page")
	def Open_login_page() {

		WebUI.click(findTestObject('Object Repository/Header/MyAccountMenu'))

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/LoginSectionTitle')), 'Login', false)

		println 'Successfully open login page'
	}

	@And("User input valid email")
	def Input_valid_username() {

		WebUI.sendKeys(findTestObject('Object Repository/MyAccountPage/InputUsername'), GlobalVariable.username)

		println 'Successfully input valid email'
	}

	@And("User input valid password")
	def Input_valid_password() {

		WebUI.sendKeys(findTestObject('Object Repository/MyAccountPage/InputPassword'), GlobalVariable.password)

		println 'Successfully input valid password'
	}

	@When("User click Login button")
	def Click_login_button() {

		WebUI.click(findTestObject('Object Repository/MyAccountPage/LogInButton'))

		println 'Successfully click login button'
	}
	
	@Then("User successfully logged into the website")
	def Successfully_logged_into_website() {
		
		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/WelcomeMessage')), 'Hello KatalonLover (not KatalonLover? Log out)', false)
		
		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/MyAccountDesc')), 'From your account dashboard you can view your recent orders, manage your shipping and billing addresses, and edit your password and account details.', false)
		
		println 'Successfully logged into the website'
	}

	@And("User click My Account menu")
	def Click_my_account_menu() {

		WebUI.click(findTestObject('Object Repository/Header/MyAccountMenu'))

		println 'Successfully click My Account menu'
	}
}