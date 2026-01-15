Feature: Payment
    Scenario: Successful payment
        Given a customer with id "12-12"
        And a merchant with id "23-23"
        And a transaction with token "123" and amount "10" kr
        And a customer bank account with id "bank-c"
        And a merchant bank account with id "bank-m"
        When the payment is registered by the payment service
        Then the token service is asked for the customer id
        And the account service is asked for the customer bank account
        And the account service is asked for the merchant bank account
        And the reporting service receives the transaction
        And the reporting service receives a successful transaction status