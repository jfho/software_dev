Feature: Token
    Scenario: The customer requests Token
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the customer requests "5" tokens
        Then the customer will recive a list of "5" different tokens

    Scenario: The customer does not need tokens but requests them
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the customer has "4" tokens
        When the customer requests "5" tokens
        Then the customer gets a message to "customer already has tokens" 

    Scenario: Token request fails when requesting zero tokens
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the customer requests "0" tokens
        Then the token request is not successful
        And an error message is returned saying "number of tokens must be greater than zero"

    Scenario: The customer requests more than 6 Tokens
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the customer requests "10" tokens
        And an error message is returned saying "The number of requeted tokens are invalid"

