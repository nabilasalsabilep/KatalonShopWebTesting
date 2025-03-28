@Login
Feature: Login feature

  @SuccessfullyLogin
  Scenario: User wants to successfully login with valid credential
    Given User open the website
   	And User open Login page
   	And User input valid email
   	And User input valid password
   	When User click Login button
   	Then User successfully logged into the website