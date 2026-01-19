Feature: Token
    Scenario: The customer requests Token
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the customer requests "5" tokens
        Then the customer will recive a list of "5" different tokens

    Scenario: The customer does not need tokens but requests them
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the customer has "4" tokens
        When the customer requests "5" tokens
        Then the customer gets a "4" tokens that it already had

    Scenario: Token request fails for unknown customer
        When the customer requests "5" tokens using customer id "non-existent-id"
        Then the token request is not successful
        And an error message is returned saying "customer with id \"non-existent-id\" is unknown"

    Scenario: Token request fails when requesting zero tokens
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the customer requests "0" tokens
        Then the token request is not successful
        And an error message is returned saying "number of tokens must be greater than zero"

