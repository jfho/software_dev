Feature: Payment
    Scenario: Successful payment
        Given a customer account with first name "Alice", last name "Alicey", CPR "123456-1234", bank ID "12345678"
        And a merchant account with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321"      
        And a transaction with token "123" and amount "10" kr
        And a customer bank account with id "bank-c"
        And a merchant bank account with id "bank-m"
        When the payment is registered by the payment service
        Then the         