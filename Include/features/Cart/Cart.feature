@Cart
Feature: Cart feature

  @AddtoCart
  Scenario: User wants to successfully add multiple products to cart
    Given User open the website
    When User click Add to Cart 2 products
    And Users input 3 quantities they want to add to the cart from the product detail page
    And User click button up on quantity field
    And User click Add to Cart product from detail product page
    And User click View Cart from detail product page
    And User verify the product quantity from detail page inside the cart match with the previous input
    Then User verify the products inside the cart
    And User verify the Cart totals inside the cart
   	
   @AddtoCartwithCoupon   
   Scenario: User wants to add to cart with coupon codes 
   	Given User open the website
   	And User open detail of the product
   	And User click Add to Cart product from detail product page
   	And User click View Cart from detail product page
   	When User input the coupon code
   	Then User verify the products inside the cart
   	And User verify the Cart totals inside the cart
   	
  @UpdateCart	
  Scenario: User wants to update cart
    Given User open the website
    When User click Add to Cart 4 products
    And Users input 2 quantities they want to add to the cart from the product detail page
    And User click Add to Cart product from detail product page
    And User click View Cart from detail product page
    And User verify the product quantity from detail page inside the cart match with the previous input
    And User verify the products inside the cart
    And User update the cart
    And User click Update Cart button
    Then User verify the products inside the cart after update the cart
    And User verify the Cart totals inside the cart