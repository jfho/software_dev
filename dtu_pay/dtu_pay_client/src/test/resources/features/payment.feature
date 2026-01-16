Feature: Payment
    Scenario: Successful payment
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        And the merchant has a token from the customer
        When the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        When the customer performs a payment for "10" kr to the merchant
        Then the balance of the customer at the bank is "990" kr
        And the balance of the merchant at the bank is "1010" kr
    
    Scenario: List of payments
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        When the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And the customer performs a payment for "10" kr to the merchant
        And the manager asks for a list of payments
        Then the list contains payments where the customer paid "10" kr to the merchant

    Scenario: Customer is not known
        Given a merchant bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And the merchant registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the merchant initiates a payment for "10" kr using customer id "non-existent-id"
        Then the payment is not successful
        And an error message is returned saying "customer with id \"non-existent-id\" is unknown"
    
    Scenario: Merchant is not known
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the customer initiates a payment for "10" kr using merchant id "non-existent-id"
        Then the payment is not successful
        And an error message is returned saying "merchant with id \"non-existent-id\" is unknown"
