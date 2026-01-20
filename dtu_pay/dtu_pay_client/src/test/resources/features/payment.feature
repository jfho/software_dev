@payment
Feature: Payment
    Scenario: Successful payment
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        And the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And the merchant has a token from the customer
        When the merchant initiates a transaction for "10" kr
        Then the payment is successful
        And the balance of the customer at the bank is "990" kr
        And the balance of the merchant at the bank is "1010" kr

    Scenario: Merchant is not known
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the merchant has a token from the customer
        When a payment is initiated for "10" kr using merchant id "non-existent-id"
        Then the payment is not successful
        And an error message is returned saying "Transaction failed"

    Scenario: Payment fails due to insufficient funds
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "5"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        And the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And the customer requests "5" tokens
        When the merchant initiates a transaction for "10" kr
        Then the payment is not successful
        And an error message is returned saying "Transaction failed"

    Scenario: Payment fails when token has already been used
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        And the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And the customer requests "1" tokens
        When the merchant initiates a transaction for "10" kr
        And the merchant initiates a transaction for "10" kr
        Then the payment is not successful
        And an error message is returned saying "Transaction failed"

    Scenario: Payment fails when token is unknown
        Given a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        And the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        When the merchant initiates a transaction for "10" kr using token id "non-existent-token"
        Then the payment is not successful
        And an error message is returned saying "Transaction failed"
