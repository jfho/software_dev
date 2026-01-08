Feature: Payment
    Scenario: Successful Payment
        Given a customer with name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And a merchant with name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        When the merchant initiates a payment for "10" kr by the customer
        Then the payment is successful
        And the balance of the customer at the bank is "990" kr
        And the balance of the merchant at the bank is "1010" kr