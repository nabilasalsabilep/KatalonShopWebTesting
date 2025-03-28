package cart
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

import org.openqa.selenium.Keys as Keys


class Cart {

	int counts

	@And("User open Shop menu")
	def Open_shop_menu() {

		WebUI.click(findTestObject('Object Repository/Header/ShopMenu'))

		println 'Successfully open Shop menu'
	}

	@When("User click Add to Cart {int} products")
	public void Add_to_cart_from_shop_page(int count) {

		counts = count

		// Find all product elements on the page
		List<WebElement> productElements = WebUI.findWebElements(findTestObject('Object Repository/ShopPage/Product'), 10)

		// Ensure we do not exceed available products
		int totalProducts = productElements.size()
		counts = Math.min(counts, totalProducts)

		for (int i = 0; i < counts; i++) {

			WebElement product = productElements.get(i)

			// Skip products with "Select options"
			List<WebElement> selectOptions = product.findElements(By.xpath(".//a[contains(text(),'Select options')]"))
			if (!selectOptions.isEmpty()) {
				continue
			}

			// Find "Add to cart" button
			List<WebElement> addToCartElements = product.findElements(By.xpath(".//a[contains(text(),'Add to cart')]"))
			if (addToCartElements.isEmpty()) {
				continue
			}

			// Extract product name
			WebElement productNameElement = product.findElement(By.xpath(".//h2[@class='woocommerce-loop-product__title']"))
			String productName = productNameElement.getText()
			GlobalVariable.productNames[i] = productName

			// Extract price while ignoring price before discount
			List<WebElement> priceElements = product.findElements(By.xpath(".//span[@class='price']//span[@class='woocommerce-Price-amount amount']"))
			List<WebElement> delElements = product.findElements(By.xpath(".//span[@class='price']//del"))

			if (!priceElements.isEmpty() && delElements.isEmpty()) {
				String productPrice = priceElements.get(priceElements.size() - 1).getText()
				GlobalVariable.productPrices[i] = productPrice
			} else if (!delElements.isEmpty() && priceElements.size() > 1) {
				// Get the discounted price (second price)
				String discountedPrice = priceElements.get(1).getText()
				GlobalVariable.productPrices[i] = discountedPrice
			}

			if(i < counts-1) {
				// Click "Add to cart" button
				WebElement addToCartButton = addToCartElements.get(0)

				addToCartButton.click()
			}

			if(i == counts-1) {

				// Open detail product
				productNameElement.click()

				WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/ProductDetailPage/ProductName')), GlobalVariable.productNames[counts-1], false)

				TestObject productPriceObject = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//p/span[@class='woocommerce-Price-amount amount'] | //p/ins/span[@class='woocommerce-Price-amount amount']")

				WebUI.verifyMatch(WebUI.getText(productPriceObject), GlobalVariable.productPrices[counts-1], false)

				GlobalVariable.quantities = WebUI.getAttribute(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), 'value')
			}
		}

		println "Successfully added " + counts + " products to the cart."
		println "Product Names: " + GlobalVariable.productNames
		println "Product Prices: " + GlobalVariable.productPrices

		println 'Successfully add to cart products'
	}


	@And("User open detail of the product")
	def Open_product_detail() {

		// Find all product elements on the page
		List<WebElement> productElements = WebUI.findWebElements(findTestObject('Object Repository/ShopPage/Product'), 10)

		for (int i = 0; i < productElements.size(); i++) {
			WebElement product = productElements.get(i)

			// Skip products with "Select options"
			List<WebElement> selectOptions = product.findElements(By.xpath(".//a[contains(text(),'Select options')]"))
			if (!selectOptions.isEmpty()) {
				continue
			}

			// Find "Add to cart" button
			List<WebElement> addToCartElements = product.findElements(By.xpath(".//a[contains(text(),'Add to cart')]"))
			if (addToCartElements.isEmpty()) {
				continue
			}

			// Extract product name
			WebElement productNameElement = product.findElement(By.xpath(".//h2[@class='woocommerce-loop-product__title']"))
			String productName = productNameElement.getText()
			GlobalVariable.productNames[i]= productName

			// Extract price while ignoring price before discount
			List<WebElement> priceElements = product.findElements(By.xpath(".//span[@class='price']//span[@class='woocommerce-Price-amount amount']"))
			List<WebElement> delElements = product.findElements(By.xpath(".//span[@class='price']//del"))

			if (!priceElements.isEmpty() && delElements.isEmpty()) {
				String productPrice = priceElements.get(priceElements.size() - 1).getText()
				GlobalVariable.productPrices[i] = productPrice
			} else if (!delElements.isEmpty() && priceElements.size() > 1) {
				// Get the discounted price (second price)
				String discountedPrice = priceElements.get(1).getText()
				GlobalVariable.productPrices[i] = discountedPrice
			}

			// Click product name to open product detail
			productNameElement.click()

			WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/ProductDetailPage/ProductName')), GlobalVariable.productNames[i], false)

			TestObject productPriceObject = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//p/span[@class='woocommerce-Price-amount amount'] | //p/ins/span[@class='woocommerce-Price-amount amount']")

			WebUI.verifyMatch(WebUI.getText(productPriceObject), GlobalVariable.productPrices[i], false)

			GlobalVariable.quantities = WebUI.getAttribute(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), 'value')

			break
		}

		println "Successfully added " + counts + " products to the cart."
		println "Product Names: " + GlobalVariable.productNames
		println "Product Prices: " + GlobalVariable.productPrices

		println 'Successfully add to cart products from detail page'
	}

	@When("Users input {int} quantities they want to add to the cart from the product detail page")
	def Input_number_of_products(int qtyValue) {

		WebUI.sendKeys(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), Keys.chord(Keys.DELETE))

		WebUI.sendKeys(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), qtyValue.toString())

		GlobalVariable.quantities = WebUI.getAttribute(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), 'value')

		println 'Successfully input the number of products from detail'
	}

	@And("User click button up on quantity field")
	def Add_more_quantity_with_button_up() {

		WebUI.click(findTestObject('Object Repository/ProductDetailPage/QuantityUpButton'))

		GlobalVariable.quantities = WebUI.getAttribute(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), 'value')

		println 'Quantity successfully increased'
	}

	@And("User click Add to Cart product from detail product page")
	def Add_to_cart_from_detail_page() {

		String qtydetailtext = WebUI.getAttribute(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), 'value')

		int qtydetail = Integer.parseInt(qtydetailtext)

		// Click "Add to cart" button
		WebUI.click(findTestObject('Object Repository/ProductDetailPage/AddtoCartButton'))

		// Get the added message and clean it up
		String addedToCartMessage = WebUI.getText(findTestObject('Object Repository/ProductDetailPage/AddedtoCartMessage')).replace('VIEW CART\n', '').replace('“', '"').replace('”', '"')
		String productName = WebUI.getText(findTestObject('Object Repository/ProductDetailPage/ProductName'))

		if (qtydetail == 1) {
			// For single item, no quantity prefix
			String expectedMessage = '"' + productName + '" has been added to your cart.'
			WebUI.verifyMatch(addedToCartMessage, expectedMessage, false)
		}else if(qtydetail > 1) {
			// For multiple items, include the quantity in the message
			String expectedMessage = qtydetail + ' × "' + productName + '" have been added to your cart.'
			WebUI.verifyMatch(addedToCartMessage, expectedMessage, false)
		}

		println 'Successfully click on Add to Cart button from detail page'
	}

	@And("User click View Cart from detail product page")
	def Click_View_Cart_from_detail() {
		WebUI.click(findTestObject('Object Repository/ProductDetailPage/ViewCartButton'))

		println 'Successfully open Cart page'
	}

	@Then("User verify the products inside the cart")
	def Verify_the_cart() {

		String[] name = GlobalVariable.productNames
		String[] price = GlobalVariable.productPrices


		List<WebElement> elements = WebUI.findWebElements(findTestObject('Object Repository/CartPage/TableSize'), 10)
		List<WebElement> productnamecart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductName'), 10)
		List<WebElement> productdpricecart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductPrice'), 10)
		List<WebElement> productqtycart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductQuantity'), 10)
		List<WebElement> producttotalpriceeachproduct = WebUI.findWebElements(findTestObject('Object Repository/CartPage/TotalData'), 10)

		GlobalVariable.expectedSubtotal = 0.00
		GlobalVariable.QtyCart = new String[elements.size()]
		GlobalVariable.expectedTotal = new String[elements.size()]

		// Loop through all the products in the cart
		for (int i = 0; i < elements.size(); i++) {
			String expectedName = name[i]
			String actualName = productnamecart.get(i).getText()

			// Validate product name
			WebUI.verifyMatch(actualName, expectedName, false)
			println "Row " + (i + 1) + ": Product name matches: " + actualName

			String expectedPrice = price[i]
			String actualPrice = productdpricecart.get(i).getText()

			// Validate product price
			WebUI.verifyMatch(actualPrice, expectedPrice, false)
			println "Row " + (i + 1) + ": Product price matches: " + actualPrice

			GlobalVariable.QtyCart [i] = productqtycart.get(i).getAttribute('value')

			// Get price and total for each product in the cart
			String priceText = productdpricecart.get(i).getText().replaceAll("[^0-9.]", "").trim()
			String totalText = producttotalpriceeachproduct.get(i).getText().replaceAll("[^0-9.]", "").trim()
			GlobalVariable.expectedTotal[i] = totalText

			// Parse to double for calculations
			double qtynumber = Double.parseDouble(GlobalVariable.QtyCart[i])
			double convertpricetodouble = Double.parseDouble(priceText)
			double actualTotal = Double.parseDouble(totalText)
			double expectedTotal = convertpricetodouble * qtynumber

			// Add the actual total to expectedSubtotal
			GlobalVariable.expectedSubtotal += expectedTotal

			// Validate if the expected total matches the actual total
			WebUI.verifyMatch(String.format("%.2f", actualTotal), String.format("%.2f", expectedTotal), false)
			println "Row " + (i + 1) + ": Product total matches: " + actualTotal
		}
	}

	@And("User verify the product quantity from detail page inside the cart match with the previous input")
	def Verify_quantities() {

		List<WebElement> productqtycart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductQuantity'), 10)

		WebUI.verifyMatch(productqtycart.get(counts-1).getAttribute('value'), GlobalVariable.quantities, false)

		println 'Quantity on cart page match with the previous input'
	}

	@When("User input the coupon code")
	def Input_coupon_code() {

		String couponcode = GlobalVariable.CouponData

		WebUI.sendKeys(findTestObject('Object Repository/CartPage/CouponCodeInputField'), couponcode)

		WebUI.click(findTestObject('Object Repository/CartPage/ApplyCouponButton'))

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CartPage/Alert')), 'Coupon code applied successfully.', false)

		println 'Successfully applied coupon code'
	}

	@And("User verify the Cart totals inside the cart")
	def Verify_Cart_totals() {

		// Validate if the expected subtotal matches the actual subtotal
		String actualSubtotal = WebUI.getText(findTestObject('Object Repository/CartPage/SubTotalData')).replaceAll("[^0-9.]", "").trim()
		WebUI.verifyMatch(String.format("%.2f", Double.parseDouble(actualSubtotal)), String.format("%.2f", GlobalVariable.expectedSubtotal), false)

		// Validate flat fare
		String flatrateText = WebUI.getText(findTestObject('Object Repository/CartPage/FlatRateData')).replaceAll("[^0-9.]", "").trim()
		double flatfare = Double.parseDouble(flatrateText)
		WebUI.verifyMatch(flatrateText, '20.00', false)

		// Check if coupon is applied
		double couponDiscount = 0.0
		TestObject couponObject = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//td[contains(@data-title, 'Coupon')]/span[@class='woocommerce-Price-amount amount']")

		boolean isCouponApplied = WebUI.verifyElementPresent(couponObject, 5, FailureHandling.OPTIONAL)

		if (isCouponApplied) {

			WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CartPage/CouponData')).replaceAll("(?i)Coupon: ", "").trim().toUpperCase(), GlobalVariable.CouponData, false)

			String couponText = WebUI.getText(findTestObject('Object Repository/CartPage/CouponDiscountData')).replaceAll("[^0-9.]", "").trim()

			String subtotalText = WebUI.getText(findTestObject('Object Repository/CartPage/SubTotalData')).replaceAll("[^0-9.]", "").trim()

			double subTotal = Double.parseDouble(subtotalText)

			double expectedReducedprice = subTotal * 0.5

			WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CartPage/CouponDiscountData')).replaceAll("[^0-9.]", "").trim(), String.format("%.2f", expectedReducedprice), false)

			if (!couponText.isEmpty()) {
				couponDiscount = Double.parseDouble(couponText)
			}
		}

		// Validate order total
		String actualOrderTotalText = WebUI.getText(findTestObject('Object Repository/CartPage/FinalTotal')).replaceAll("[^0-9.]", "").trim()

		double expectedOrderTotal = GlobalVariable.expectedSubtotal + flatfare - couponDiscount

		WebUI.verifyMatch(String.format("%.2f", Double.parseDouble(actualOrderTotalText)), String.format("%.2f", expectedOrderTotal), false)

		GlobalVariable.finalTotal = expectedOrderTotal

		println 'Products in the Cart match the products previously selected'
	}

	@When("User update the cart")
	def Update_cart() {

		String name = WebUI.getText(findTestObject('Object Repository/CartPage/ProductName'))

		// Delete a product from cart
		WebUI.click(findTestObject('Object Repository/CartPage/DeleteButton'))

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CartPage/Alert')).replace('“', '"').replace('”', '"'), '"' + name + '"' + ' removed. Undo?', false)


		// Update product quantities
		WebUI.sendKeys(findTestObject('Object Repository/CartPage/ProductQuantity'), Keys.chord(Keys.DELETE))

		WebUI.sendKeys(findTestObject('Object Repository/CartPage/ProductQuantity'), '3')

		WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/CartPage/ProductQuantity'), 'value'), '3', false)

		println 'Successfuly update cart'
	}

	@And("User click Update Cart button")
	def Click_update_cart() {

		WebUI.click(findTestObject('Object Repository/CartPage/UpdateCartButton'))

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CartPage/Alert')), 'Cart updated.', false)

		println 'Successfully click Update Cart'
	}

	@Then("User verify the products inside the cart after update the cart")
	def Verify_cart_after_update() {
		String[] name = GlobalVariable.productNames
		String[] price = GlobalVariable.productPrices

		List<WebElement> elements = WebUI.findWebElements(findTestObject('Object Repository/CartPage/TableSize'), 10)
		List<WebElement> productnamecart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductName'), 10)
		List<WebElement> productdpricecart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductPrice'), 10)
		List<WebElement> productqtycart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductQuantity'), 10)
		List<WebElement> producttotalpriceeachproduct = WebUI.findWebElements(findTestObject('Object Repository/CartPage/TotalData'), 10)

		GlobalVariable.expectedSubtotal = 0.00
		GlobalVariable.QtyCart = new String[elements.size()]
		GlobalVariable.expectedTotal = new String[elements.size()]

		// Loop through all the products in the cart
		for (int i = 0; i < elements.size(); i++) {
			String expectedName = name[i+1]
			String actualName = productnamecart.get(i).getText()

			// Validate product name
			WebUI.verifyMatch(actualName, expectedName, false)
			println "Row " + (i + 1) + ": Product name matches: " + actualName

			String expectedPrice = price[i+1]
			String actualPrice = productdpricecart.get(i).getText()

			// Validate product price
			WebUI.verifyMatch(actualPrice, expectedPrice, false)
			println "Row " + (i + 1) + ": Product price matches: " + actualPrice

			GlobalVariable.QtyCart [i] = productqtycart.get(i).getAttribute('value')

			// Get price and total for each product in the cart
			String priceText = productdpricecart.get(i).getText().replaceAll("[^0-9.]", "").trim()
			String totalText = producttotalpriceeachproduct.get(i).getText().replaceAll("[^0-9.]", "").trim()
			GlobalVariable.expectedTotal[i] = totalText

			// Parse to double for calculations
			double qtynumber = Double.parseDouble(GlobalVariable.QtyCart [i])
			double convertpricetodouble = Double.parseDouble(priceText)
			double actualTotal = Double.parseDouble(totalText)
			double expectedTotal = convertpricetodouble * qtynumber

			// Add the actual total to expectedSubtotal
			GlobalVariable.expectedSubtotal += expectedTotal

			// Validate if the expected total matches the actual total
			WebUI.verifyMatch(String.format("%.2f", actualTotal), String.format("%.2f", expectedTotal), false)
			println "Row " + (i + 1) + ": Product total matches: " + actualTotal
		}
	}

	@And("User click Proceed to Checkout button")
	def Proceed_to_Chekout() {
		WebUI.click(findTestObject('Object Repository/CartPage/ProceedtoCheckoutButton'))

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/CheckoutPageTitle')), 'Checkout', false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/BillingDetailsSectionTitle')), 'Billing details', false)

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CheckoutPage/YourOrderSectionTitle')), 'Your order', false)

		println 'Successfully direct to the Checkout page'
	}
}