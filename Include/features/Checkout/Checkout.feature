@Checkout
Feature: Checkout feature

  @CheckoutwithUserAddress
  Scenario: User wants to successfully checkout products with one address
    Given User open the website
    And User open detail of the product
    And Users input 2 quantities they want to add to the cart from the product detail page
    And User click Add to Cart product from detail product page	
    And User click View Cart from detail product page
    And User verify the products inside the cart
    And User verify the Cart totals inside the cart
    When User click Proceed to Checkout button
    And User verify the Order items
    And User fill in Billing details
    And User click Place Order button
    Then User verify the completed order page
    
  @CheckoutwithaDifferentShippingAddress
  Scenario: User wants to successfully checkout products with different shipping address
    Given User open the website
    And User open detail of the product
    And User click Add to Cart product from detail product page
    And User click View Cart from detail product page
    And User verify the products inside the cart
    And User verify the Cart totals inside the cart
    When User click Proceed to Checkout button
    And User verify the Order items
    And User fill in Billing details
    And User chooses to ship to a different address
    And User fill in additional data that must be filled in Billing details
    And User click Place Order button
    Then User verify the completed order page
    
  @CheckoutwithCoupon
  Scenario: User wants to successfully checkout products with coupon code
    Given User open the website
    And User click Add to Cart 2 products
    And Users input 2 quantities they want to add to the cart from the product detail page
    And User click Add to Cart product from detail product page
    And User click View Cart from detail product page
    And User verify the product quantity from detail page inside the cart match with the previous input
    And User verify the products inside the cart
    And User verify the Cart totals inside the cart
    When User click Proceed to Checkout button
    And User verify the Order items
    And User fill in Billing details
    And User click enter coupon code hyperlink
    And User input the coupon code on checkout page
    And User click apply coupon button on checkout page
    And User verify the Order items
    And User click Place Order button
    Then User verify the completed order page