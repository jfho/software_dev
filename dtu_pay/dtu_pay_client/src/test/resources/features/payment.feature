Feature: Payment
    Scenario: Successful payment
        Given a customer bank account with name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And a merchant bank account with name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        When the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the merchant registers for DTUPay with username "cstrauss"
        When the customer performs a payment for "10" kr to the merchant
        Then the balance of the customer at the bank is "990" kr
        And the balance of the merchant at the bank is "1010" kr
    
    Scenario: List of payments
        Given a customer bank account with name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And a merchant bank account with name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        When the customer registers for DTUPay with username "jweikop"
        And the merchant registers for DTUPay with username "cstrauss"
        And the customer performs a payment for "10" kr to the merchant
        And the manager asks for a list of payments
        Then the list contains payments where customer "jweikop" paid "10" kr to merchant "cstrauss"
