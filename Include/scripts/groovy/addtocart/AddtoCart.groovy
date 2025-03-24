package addtocart
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


class AddtoCart {

	List<String> productNames = new ArrayList<>()
	List<String> productPrices = new ArrayList<>()
	List<String> QtyCart = new ArrayList<>()
	int counts
	double expectedSubtotal
	String quantities
	TestData couponData = findTestData("CouponCode")

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


	@When("User input the number of products they wants to add to the cart")
	def Input_number_of_products() {

		WebUI.sendKeys(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), Keys.chord(Keys.CONTROL, 'a'))

		WebUI.sendKeys(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), Keys.chord(Keys.DELETE))

		WebUI.sendKeys(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), '3')

		WebUI.click(findTestObject('Object Repository/ProductDetailPage/QuantityUpButton'))

		quantities = WebUI.getAttribute(findTestObject('Object Repository/ProductDetailPage/QuantityInputField'), 'value')

		println 'Successfully input the number of products from detail'
	}

	@When("User click Add to Cart from detail product page")
	def Add_to_Cart_one_type_product_from_detail() {

		WebUI.click(findTestObject('Object Repository/ProductDetailPage/AddtoCartButton'))

		println 'Successfully added items on cart'
	}

	@When("User click Add to Cart {int} products from Shop page")
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
			productNames.add(productName)

			// Extract price while ignoring price before discount
			List<WebElement> priceElements = product.findElements(By.xpath(".//span[@class='price']//span[@class='woocommerce-Price-amount amount']"))
			List<WebElement> delElements = product.findElements(By.xpath(".//span[@class='price']//del"))

			if (!priceElements.isEmpty() && delElements.isEmpty()) {
				String productPrice = priceElements.get(priceElements.size() - 1).getText()
				productPrices.add(productPrice)
			} else if (!delElements.isEmpty() && priceElements.size() > 1) {
				// Get the discounted price (second price)
				String discountedPrice = priceElements.get(1).getText()
				productPrices.add(discountedPrice)
			}

			// Click "Add to cart" button
			WebElement addToCartButton = addToCartElements.get(0)
			addToCartButton.click()
		}

		println "Successfully added " + counts + " products to the cart."
		println "Product Names: " + productNames
		println "Product Prices: " + productPrices

		println 'Successfully add to cart products from Shop page'
	}


	@And("User open detail of the product")
	def Open_product_detail() {

		// Find all product elements on the page
		List<WebElement> productElements = WebUI.findWebElements(findTestObject('Object Repository/ShopPage/Product'), 10)

		// Ensure we do not exceed available products
		int totalProducts = productElements.size()
		counts = Math.min(counts, totalProducts)

		for (int i = counts; i < totalProducts; i++) {
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
			productNames.add(productName)

			// Extract price while ignoring price before discount
			List<WebElement> priceElements = product.findElements(By.xpath(".//span[@class='price']//span[@class='woocommerce-Price-amount amount']"))
			List<WebElement> delElements = product.findElements(By.xpath(".//span[@class='price']//del"))

			if (!priceElements.isEmpty() && delElements.isEmpty()) {
				String productPrice = priceElements.get(priceElements.size() - 1).getText()
				productPrices.add(productPrice)
			} else if (!delElements.isEmpty() && priceElements.size() > 1) {
				// Get the discounted price (second price)
				String discountedPrice = priceElements.get(1).getText()
				productPrices.add(discountedPrice)
			}

			// Click product name to open product detail
			productNameElement.click()

			WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/ProductDetailPage/ProductName')), productNames[counts], false)

			TestObject productPriceObject = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//p/span[@class='woocommerce-Price-amount amount'] | //p/ins/span[@class='woocommerce-Price-amount amount']")

			WebUI.verifyMatch(WebUI.getText(productPriceObject), productPrices[counts], false)

			break
		}

		println "Successfully added " + counts + " products to the cart."
		println "Product Names: " + productNames
		println "Product Prices: " + productPrices

		println 'Successfully add to cart products from detail page'
	}

	@And("User click Add to Cart products from detail product page")
	def Add_to_cart_from_detail_page() {

		// Click "Add to cart" button
		WebUI.click(findTestObject('Object Repository/ProductDetailPage/AddtoCartButton'))

		// Verify Added to Cart message
		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/ProductDetailPage/AddedtoCartMessage')).replace('VIEW CART\n', '').replace('“', '"').replace('”', '"'), quantities + ' × ' +'"'+ WebUI.getText(findTestObject('Object Repository/ProductDetailPage/ProductName')) + '"' + ' have been added to your cart.', false)

		println 'Successfully click on Add to Cart button from detail page'
	}

	@And("User click View Cart from detail product page")
	def Click_View_Cart_from_detail() {
		WebUI.click(findTestObject('Object Repository/ProductDetailPage/ViewCartButton'))

		println 'Successfully open Cart page'
	}

	@Then("User verify the products inside the cart")
	def Verify_the_cart() {

		String[] name = productNames
		String[] price = productPrices
		String[] qty = QtyCart

		List<WebElement> elements = WebUI.findWebElements(findTestObject('Object Repository/CartPage/TableSize'), 10)
		List<WebElement> productnamecart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductName'), 10)
		List<WebElement> productdpricecart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductPrice'), 10)
		List<WebElement> productqtycart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductQuantity'), 10)
		List<WebElement> producttotalpriceeachproduct = WebUI.findWebElements(findTestObject('Object Repository/CartPage/TotalData'), 10)

		expectedSubtotal = 0.00
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
			
			String QtyValue = productqtycart.get(i).getAttribute('value')

			// Get price and total for each product in the cart
			String priceText = productdpricecart.get(i).getText().replaceAll("[^0-9.]", "").trim()
			String totalText = producttotalpriceeachproduct.get(i).getText().replaceAll("[^0-9.]", "").trim()

			// Parse to double for calculations
			double qtynumber = Double.parseDouble(QtyValue)
			double convertpricetodouble = Double.parseDouble(priceText)
			double actualTotal = Double.parseDouble(totalText)
			double expectedTotal = convertpricetodouble * qtynumber

			// Add the actual total to expectedSubtotal
			expectedSubtotal += expectedTotal

			// Validate if the expected total matches the actual total
			WebUI.verifyMatch(String.format("%.2f", actualTotal), String.format("%.2f", expectedTotal), false)
			println "Row " + (i + 1) + ": Product total matches: " + actualTotal
		}
	}

	@And("User verify the product quantity from detail page inside the cart match with the previous input")
	def Verify_quantities() {

		List<WebElement> productqtycart = WebUI.findWebElements(findTestObject('Object Repository/CartPage/ProductQuantity'), 10)

		WebUI.verifyMatch(productqtycart.get(counts).getAttribute('value'), quantities, false)

		println 'Quantity on cart page match with the previous input'
	}

	@When("User input the coupon code")
	def Input_coupon_code() {

		String couponcode = couponData.getValue(1, 1)

		WebUI.sendKeys(findTestObject('Object Repository/CartPage/CouponCodeInputField'), couponcode)

		WebUI.click(findTestObject('Object Repository/CartPage/ApplyCouponButton'))

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CartPage/Alert')), 'Coupon code applied successfully.', false)

		println 'Successfully applied coupon code'
	}

	@And("User verifies discount from coupon")
	def Verify_reduced_price() {

		String subtotalText = WebUI.getText(findTestObject('Object Repository/CartPage/SubTotalData')).replaceAll("[^0-9.]", "").trim()

		double subTotal = Double.parseDouble(subtotalText)

		double expectedReducedprice = subTotal * 0.5

		WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/CartPage/CouponData')).replaceAll("[^0-9.]", "").trim(), String.format("%.2f", expectedReducedprice), false)
	}

	@And("User verify the Cart totals inside the cart")
	def Verify_Cart_totals() {

		// Validate if the expected subtotal matches the actual subtotal
		String actualSubtotal = WebUI.getText(findTestObject('Object Repository/CartPage/SubTotalData')).replaceAll("[^0-9.]", "").trim()
		WebUI.verifyMatch(String.format("%.2f", Double.parseDouble(actualSubtotal)), String.format("%.2f", expectedSubtotal), false)

		// Validate flat fare
		String flatrateText = WebUI.getText(findTestObject('Object Repository/CartPage/FlatRateData')).replaceAll("[^0-9.]", "").trim()
		double flatfare = Double.parseDouble(flatrateText)
		WebUI.verifyMatch(flatrateText, '20.00', false)

		// Check if coupon is applied
		double couponDiscount = 0.0
		TestObject couponObject = new TestObject().addProperty("xpath", ConditionType.EQUALS, "//td[contains(@data-title, 'Coupon')]/span[@class='woocommerce-Price-amount amount']")

		boolean isCouponApplied = WebUI.verifyElementPresent(couponObject, 5, FailureHandling.OPTIONAL)

		if (isCouponApplied) {
			String couponText = WebUI.getText(couponObject).replaceAll("[^0-9.]", "").trim()
			if (!couponText.isEmpty()) {
				couponDiscount = Double.parseDouble(couponText)
			}
		}

		// Validate order total
		String actualOrderTotalText = WebUI.getText(findTestObject('Object Repository/CartPage/FinalTotal')).replaceAll("[^0-9.]", "").trim()

		double expectedOrderTotal = expectedSubtotal + flatfare - couponDiscount

		WebUI.verifyMatch(String.format("%.2f", Double.parseDouble(actualOrderTotalText)), String.format("%.2f", expectedOrderTotal), false)

		println 'Products in the Cart match the products previously selected'
	}

	@When("User update the cart")
	def Update_cart() {
						
		WebUI.sendKeys(findTestObject('Object Repository/CartPage/ProductQuantity'), Keys.chord(Keys.CONTROL, 'a'))
		
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
}