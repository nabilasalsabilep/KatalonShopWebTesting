@Checkout
Feature: Checkout feature

  @SuccessfullyCheckout
  Scenario: User wants to successfully checkout products
    Given User open the website
    When User click Add to Cart 2 products from Shop page
    And User click Add to Cart products from detail product page
    And User click View Cart from detail product page
    Then User verify the products inside the cart
    And User verify the Cart totals inside the cart