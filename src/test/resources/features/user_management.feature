Feature: User Management API

  Scenario: Verify that the user can fetch all users
    Given I have the base API URL
    When I send a GET request with token
    Then the response status code should be 200

  Scenario: Create a new user successfully
    Given I have the base API URL
    When I have create a new user with following details
    | first_name | kamu |
    | last_name  | Manickam |
    | email     | kamu@gmial.com |
    | age       | 29             |
    Then the response status code should be 201

  Scenario: Delete the user successfully
    Given I have the base API URL
    When I delete the user with id.
    Then the response status code should be 204