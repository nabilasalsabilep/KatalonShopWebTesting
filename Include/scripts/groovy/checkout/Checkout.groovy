package checkout
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

import randomdata.*
import org.openqa.selenium.Keys as Keys
import java.util.ArrayList
import java.text.SimpleDateFormat



class Checkout {

	RandomData randdata = new RandomData()

	String paymentmethodlabel, paymentmethoddata, orderstatus

	@And("User verify the Order items")
	def Verify_order_items_on_Checkout_page() {

		String[] name = GlobalVariable.productNames
		String[] QtyValue = GlobalVariable.QtyCart
		String[] totalprice = GlobalVariable.expectedTotal

		List<WebElement> elements = WebUI.findWebElements(findTestObject('Object Repository/CheckoutPage/ItemTable'), 10)
		List<WebElement> productnamecheckout = WebUI.findWebElements(findTestObject('Object Repository/CheckoutPage/ProductName'), 10)
		List<WebElement> productdpricecheckout = WebUI.findWebElements(findTestObject('Object Repository/CheckoutPage/ProductTotal'), 10)
		List<WebElement> productqtycheckout = WebUI.findWebElements(findTestObject('Object Repository/CheckoutPage/ProductQuantity'), 10)
		List<WebElement> producttotaleachproduct = WebUI.findWebElements(findTestObject('Object Repository/CheckoutPage/ProductTotal'), 10)

		double actualsubtotal = 0.00
		// Loop through all the products in the cart
		for (int i = 0; i < elements.size(); i++) {

			// Validate quantity
			String QtyValueCheckout = productqtycheckout.get(i).getText().replaceAll("[^0-9]", "").trim()

			WebUI.verifyMatch(QtyValueCheckout, QtyValue [i], false)
			println "Row " + (i + 1) + ": Product quantity matches: " + QtyValueCheckout

			// Validate product name
			String expectedName = name[i]
			String actualName = productnamecheckout.get(i).getText().replaceAll("\\s*[×x]\\s*\\d+", "").trim()

			WebUI.verifyMatch(actualName, expectedName, false)
			println "Row " + (i + 1) + ": Product name matches: " + actualName

			// Validate total price of each product
			String actualtotalprice = producttotaleachproduct.get(i).getText().replaceAll("[^0-9.]", "").trim()
			double converttotaltodouble = Double.parseDouble(actualtotalprice)

			actualsubtotal += converttotaltodouble

			WebUI.verifyMatch(String.format("%.2f", converttotaltodouble), String.format("%.2f", Double.parseDouble(totalprice[i])), false)
			println "Row " + (i + 1) + ": Product total matches: " + actualtotalprice
		}

		WebUI.verifyMatch(String.format("%.2f",actualsubtotal), String.format("%.2f", GlobalVariable.expectedSubtotal), false)

		// Validate sub total
		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/SubTotal')).replaceAll("[^0-9.]", "").trim(), String.format("%.2f", GlobalVariable.expectedSubtotal), false)

		// Validate flat fare
		String flatrateText = WebUI.getText(findTestObject('Object Repository/CheckoutPage/FlatRateData')).replaceAll("[^0-9.]", "").trim()

		double flatfare = Double.parseDouble(flatrateText)

		WebUI.verifyMatch(flatrateText, '20.00', false)

		// Check if coupon is applied
		double couponDiscount = 0.0

		boolean isCouponApplied = WebUI.verifyElementPresent(findTestObject('Object Repository/CheckoutPage/CouponDiscountData'), 5, FailureHandling.OPTIONAL)

		if (isCouponApplied) {
			WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/CouponData')).replaceAll("(?i)Coupon: ", "").trim().toUpperCase(), GlobalVariable.CouponData, false)

			String couponText = WebUI.getText(findTestObject('Object Repository/CheckoutPage/CouponDiscountData')).replaceAll("[^0-9.]", "").trim()

			String subtotalText = WebUI.getText(findTestObject('Object Repository/CheckoutPage/SubTotal')).replaceAll("[^0-9.]", "").trim()

			double subTotal = Double.parseDouble(subtotalText)

			double expectedReducedprice = subTotal * 0.5

			WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/CouponDiscountData')).replaceAll("[^0-9.]", "").trim(), String.format("%.2f", expectedReducedprice), false)

			if (!couponText.isEmpty()) {
				couponDiscount = Double.parseDouble(couponText)
			}
		}

		// Get payment method
		GlobalVariable.paymentmethod = WebUI.getText(findTestObject('Object Repository/CheckoutPage/PaymentMethodLabel'))

		// Validate total order shipping
		String actualOrderTotalText = WebUI.getText(findTestObject('Object Repository/CheckoutPage/TotalData')).replaceAll("[^0-9.]", "").trim()

		double expectedOrderTotal = GlobalVariable.expectedSubtotal + flatfare - couponDiscount

		WebUI.verifyMatch(String.format("%.2f", Double.parseDouble(actualOrderTotalText)), String.format("%.2f", expectedOrderTotal), false)

		GlobalVariable.finalTotal = expectedOrderTotal

		println 'Order items on Checkout page match with the items on the Cart page'
	}

	@And("User fill in Billing details")
	def Fill_in_Billing_details() {

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/FirstNameInputField'), randdata.generateRandomFirstName())

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/LastNameInputField'), randdata.generateRandomLastName())

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/CompanyNameInputField'), randdata.generateRandomCompanyName())

		WebUI.click(findTestObject('Object Repository/CheckoutPage/CountryDropdown'))

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/CountrySearchBox'), 'Indonesia')

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/CountrySearchBox'), Keys.chord(Keys.ENTER))

		WebUI.delay(1)

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/HouseNumberStreetNameAddressInputField'), randdata.generateRandomAddress())

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/ApartmentSuiteUnitAddressInputField'), randdata.generateRandomUnit())

		WebUI.delay(1)

		WebUI.click(findTestObject('Object Repository/CheckoutPage/ProvinceDropdown'))

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/ProvinceSearchBox'), 'JAWA TIMUR')

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/ProvinceSearchBox'), Keys.chord(Keys.ENTER))

		WebUI.delay(1)

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/PostcodeInputField'), randdata.generateRandomPostalCode())

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/CityInputField'), 'Surabaya')

		WebUI.delay(1)

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/PhoneNumberInputField'), randdata.generateRandomPhoneNumber())

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/EmailInputField'), randdata.generateRandomEmail())

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/OrderNotesInputField'), randdata.generateRandomOrderNotes())

		println 'Successfully fill in the Billing details'
	}

	@And("User click Place Order button")
	def Click_Place_Order() {

		WebUI.click(findTestObject('Object Repository/CheckoutPage/PlaceOrderButton'))

		WebUI.waitForElementPresent(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/SuccessMessage'), 30)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/SuccessMessage')), 'Thank you. Your order has been received.', false)

		println 'Successfully open Checkout page'
	}

	@And("User chooses to ship to a different address")
	def Choose_to_ship_to_a_different_address() {

		WebUI.click(findTestObject('Object Repository/CheckoutPage/CheckboxShiptoaDifferentAddress'))

		println 'Successfully choose to ship to different address'
	}

	@And("User fill in additional data that must be filled in Billing details")
	def Fill_in_additional_data_in_Billing_details() {

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/FirstNameInputFieldIfDiffAddress'), randdata.generateRandomFirstName())

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/LastNameInputFieldIfDiffAddress'), randdata.generateRandomLastName())

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/CompanyNameforDiffAddress'), randdata.generateRandomCompanyName())

		WebUI.click(findTestObject('Object Repository/CheckoutPage/CountryDropdownforDiffAddress'))

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/CountrySearchBox'), 'Indonesia')

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/CountrySearchBox'), Keys.chord(Keys.ENTER))

		WebUI.delay(1)

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/HouseNumberStreetNameInputFieldforDiffAddress'), randdata.generateRandomAddress())

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/ApartmentSuiteUnitInputFieldforDiffAddress'), randdata.generateRandomUnit())

		WebUI.delay(1)

		WebUI.click(findTestObject('Object Repository/CheckoutPage/ProvinceDropdownforDiffAddress'))

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/ProvinceSearchBox'), 'JAWA TIMUR')

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/ProvinceSearchBox'), Keys.chord(Keys.ENTER))

		WebUI.delay(1)

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/PostalCodeforDiffAddress'), randdata.generateRandomPostalCode())

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/CityInputFieldforDiffAddress'), 'Surabaya')

		WebUI.executeJavaScript("window.scrollTo(0, 0);", [])

		println 'Successfully fill in additional data in Billing details'
	}

	@And("User click enter coupon code hyperlink")
	def Click_enter_coupon_code() {

		WebUI.executeJavaScript("window.scrollTo(0, 0);", [])

		WebUI.delay(1)

		WebUI.click(findTestObject('Object Repository/CheckoutPage/EnterCouponCodeHyperlink'))

		// Verifikasi bahwa input field dan tombol apply coupon muncul
		WebUI.verifyElementPresent(findTestObject('Object Repository/CheckoutPage/CouponCodeInputField'), 10)

		WebUI.verifyElementPresent(findTestObject('Object Repository/CheckoutPage/ApplyCouponButton'), 10)

		println 'The coupon code input field successfully appears'
	}

	@And("User input the coupon code on checkout page")
	def Input_coupon_code() {

		String couponcode = GlobalVariable.CouponData

		WebUI.sendKeys(findTestObject('Object Repository/CheckoutPage/CouponCodeInputField'), couponcode)

		println 'Successfully input coupon code on checkout page'
	}

	@And("User click apply coupon button on checkout page")
	def Click_apply_coupon() {

		WebUI.click(findTestObject('Object Repository/CheckoutPage/ApplyCouponButton'))

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/Alert')), 'Coupon code applied successfully.', false)

		println 'Successfully apply the coupon code'
	}

	@Then("User verify the completed order page")
	def Verify_completed_order () {

		// Get order number on summary
		GlobalVariable.ordernumber = WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/OrderNumber'))

		// Validate current date on summary
		Date today = new Date()
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy")
		String formattedDate = sdf.format(today)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/DateOrdered')), formattedDate, false)

		GlobalVariable.orderdate = WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/DateOrdered'))

		// Validate total on summary
		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/TotalDataonSummary')).replaceAll("[^0-9.]", "").trim(), String.format("%.2f", GlobalVariable.finalTotal), false)

		// Validate payment method on summary
		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/PaymentMethodData')), GlobalVariable.paymentmethod, false)

		String[] name = GlobalVariable.productNames
		String[] QtyValue = GlobalVariable.QtyCart
		String[] totalprice = GlobalVariable.expectedTotal

		List<WebElement> elements = WebUI.findWebElements(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/ItemTableSize'), 10)
		List<WebElement> productnamecheckout = WebUI.findWebElements(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/ProductName'), 10)
		List<WebElement> productqtycheckout = WebUI.findWebElements(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/ProductQuantity'), 10)
		List<WebElement> producttotaleachproduct = WebUI.findWebElements(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/TotalPrice'), 10)

		double actualsubtotal = 0.00
		// Loop through all the products in the cart
		for (int i = 0; i < elements.size(); i++) {

			// Validate quantity
			String QtyValueCheckout = productqtycheckout.get(i).getText().replaceAll("[^0-9]", "").trim()
			GlobalVariable.totalItems += Integer.parseInt(QtyValueCheckout)

			WebUI.verifyMatch(QtyValueCheckout, QtyValue [i], false)
			println "Row " + (i + 1) + ": Product quantity matches: " + QtyValueCheckout

			// Validate product name
			String expectedName = name[i]
			String actualName = productnamecheckout.get(i).getText().replaceAll("\\s*[×x]\\s*\\d+", "").trim()

			WebUI.verifyMatch(actualName, expectedName, false)
			println "Row " + (i + 1) + ": Product name matches: " + actualName

			// Validate total price of each product
			String actualtotalprice = producttotaleachproduct.get(i).getText().replaceAll("[^0-9.]", "").trim()
			double converttotaltodouble = Double.parseDouble(actualtotalprice)

			actualsubtotal += converttotaltodouble

			WebUI.verifyMatch(String.format("%.2f", converttotaltodouble), String.format("%.2f", Double.parseDouble(totalprice[i])), false)
			println "Row " + (i + 1) + ": Product total matches: " + actualtotalprice
		}

		WebUI.verifyMatch(String.format("%.2f",actualsubtotal), String.format("%.2f", GlobalVariable.expectedSubtotal), false)

		// Validate sub total
		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/SubTotalData')).replaceAll("[^0-9.]", "").trim(), String.format("%.2f", GlobalVariable.expectedSubtotal), false)

		// Validate flat fare
		String flatrateText = WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/FlatRateData')).replaceAll("[^0-9.]", "").trim()

		double flatfare = Double.parseDouble(flatrateText)

		WebUI.verifyMatch(flatrateText, '20.00', false)

		// Check if coupon is applied
		double couponDiscount = 0.0

		boolean isCouponApplied = WebUI.verifyElementPresent(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/CouponDiscountData'), 5, FailureHandling.OPTIONAL)

		if (isCouponApplied) {

			String couponText = WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/CouponDiscountData')).replaceAll("[^0-9.]", "").trim()

			String subtotalText = WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/SubTotalData')).replaceAll("[^0-9.]", "").trim()

			double subTotal = Double.parseDouble(subtotalText)

			double expectedReducedprice = subTotal * 0.5

			WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/CouponDiscountData')).replaceAll("[^0-9.]", "").trim(), String.format("%.2f", expectedReducedprice), false)

			if (!couponText.isEmpty()) {
				couponDiscount = Double.parseDouble(couponText)
			}
		}

		// Validate payment method
		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/PaymentMethodData')), GlobalVariable.paymentmethod, false)


		// Validate total order shipping
		String actualOrderTotalText = WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/TotalFinal')).replaceAll("[^0-9.]", "").trim()

		double expectedOrderTotal = GlobalVariable.expectedSubtotal + flatfare - couponDiscount

		WebUI.verifyMatch(String.format("%.2f", Double.parseDouble(actualOrderTotalText)), String.format("%.2f", expectedOrderTotal), false)

		println 'Successfully completed the checkout process'
	}

	@And("User verify order in My Account recent orders")
	def Verify_My_account_recent_orders() {

		WebUI.click(findTestObject('Object Repository/MyAccountPage/RecentOrdersHyperlink'))

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/OrderNumber')), '#' + GlobalVariable.ordernumber, false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/OrderDate')), GlobalVariable.orderdate, false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/OrderTotalData')), GlobalVariable.expectedSubtotal, false)

		String orderText = WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/OrderTotalwithNumberofItems'))

		String itemCount = (orderText =~ /(\d+)\s*item/)[0][1]

		WebUI.verifyMatch(itemCount, GlobalVariable.totalItems.toString(), false)

		orderstatus = WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/OrderStatus'))

		println 'Order successfully appears in recent orders'
	}

	@And("User verify order detail")
	def Verify_order_detail() {

		// Validate order summary

		WebUI.click(findTestObject('Object Repository/MyAccountPage/OrdersPage/ViewHyperlink'))

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/OrderNumberonDetail')), GlobalVariable.ordernumber, false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/OrderDateonDetail')), GlobalVariable.orderdate, false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/OrderStatusonDetail')), orderstatus, false)

		// Validate order details

		String[] name = GlobalVariable.productNames
		String[] QtyValue = GlobalVariable.QtyCart
		String[] totalprice = GlobalVariable.expectedTotal

		List<WebElement> elements = WebUI.findWebElements(findTestObject('Object Repository/MyAccountPage/OrdersPage/OrderTableSize'), 10)
		List<WebElement> productnamecheckout = WebUI.findWebElements(findTestObject('Object Repository/MyAccountPage/OrdersPage/ProductName'), 10)
		List<WebElement> productqtycheckout = WebUI.findWebElements(findTestObject('Object Repository/MyAccountPage/OrdersPage/ProductQuantity'), 10)
		List<WebElement> producttotaleachproduct = WebUI.findWebElements(findTestObject('Object Repository/MyAccountPage/OrdersPage/ProductTotalPrice'), 10)

		double actualsubtotal = 0.00
		// Loop through all the products in the cart
		for (int i = 0; i < elements.size(); i++) {

			// Validate quantity
			String QtyValueCheckout = productqtycheckout.get(i).getText().replaceAll("[^0-9]", "").trim()
			GlobalVariable.totalItems += Integer.parseInt(QtyValueCheckout)

			WebUI.verifyMatch(QtyValueCheckout, QtyValue [i], false)
			println "Row " + (i + 1) + ": Product quantity matches: " + QtyValueCheckout

			// Validate product name
			String expectedName = name[i]
			String actualName = productnamecheckout.get(i).getText().replaceAll("\\s*[×x]\\s*\\d+", "").trim()

			WebUI.verifyMatch(actualName, expectedName, false)
			println "Row " + (i + 1) + ": Product name matches: " + actualName

			// Validate total price of each product
			String actualtotalprice = producttotaleachproduct.get(i).getText().replaceAll("[^0-9.]", "").trim()
			double converttotaltodouble = Double.parseDouble(actualtotalprice)

			actualsubtotal += converttotaltodouble

			WebUI.verifyMatch(String.format("%.2f", converttotaltodouble), String.format("%.2f", Double.parseDouble(totalprice[i])), false)
			println "Row " + (i + 1) + ": Product total matches: " + actualtotalprice
		}


		// Validate sub total
		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/Subtotal')), String.format("%.2f",actualsubtotal), false)

		WebUI.verifyMatch(String.format("%.2f",actualsubtotal), String.format("%.2f", GlobalVariable.expectedSubtotal), false)

		// Validate flat fare
		String flatrateText = WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/FlatRate')).replaceAll("[^0-9.]", "").trim()

		double flatfare = Double.parseDouble(flatrateText)

		WebUI.verifyMatch(flatrateText, '20.00', false)

		// Check if coupon is applied
		double couponDiscount = 0.0

		boolean isCouponApplied = WebUI.verifyElementPresent(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/CouponDiscountData'), 5, FailureHandling.OPTIONAL)

		if (isCouponApplied) {

			String couponText = WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/CouponDiscountData')).replaceAll("[^0-9.]", "").trim()

			String subtotalText = WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/SubTotalData')).replaceAll("[^0-9.]", "").trim()

			double subTotal = Double.parseDouble(subtotalText)

			double expectedReducedprice = subTotal * 0.5

			WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/OrderReceivedPage/CouponDiscountData')).replaceAll("[^0-9.]", "").trim(), String.format("%.2f", expectedReducedprice), false)

			if (!couponText.isEmpty()) {
				couponDiscount = Double.parseDouble(couponText)
			}
		}

		// Validate payment method
		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/PaymentMethod')), GlobalVariable.paymentmethod, false)


		// Validate total order shipping
		String actualOrderTotalText = WebUI.getText(findTestObject('Object Repository/MyAccountPage/OrdersPage/FinalTotal')).replaceAll("[^0-9.]", "").trim()

		double expectedOrderTotal = GlobalVariable.expectedSubtotal + flatfare - couponDiscount

		WebUI.verifyMatch(String.format("%.2f", Double.parseDouble(actualOrderTotalText)), String.format("%.2f", expectedOrderTotal), false)

		println 'The data in the order details is the same as the data input on the checkout page'
	}
}