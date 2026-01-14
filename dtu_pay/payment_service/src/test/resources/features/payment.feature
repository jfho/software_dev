Feature: Payment
    Scenario: Successful payment
        Given a transaction with token "123", merchant id "123-456" and amount "10" kr
        And the customer has id "456-789"
        And customer bank account with id "12-12"
        And merchant bank account with id "34-34"
        When we register the transaction
        Then the "payments.customerid.request" queue has 1 element with string "123"
        And the "payments.customerbankaccount.request" queue has 1 element with string "456-789"
        And the "payments.merchantbankaccount.request" queue has 1 element with string "123-456"
        And the "payments.transaction.report" queue has 1 element with string "456-789,123-456,10"
        And the "payments.transaction.status" queue has 1 element with string "Transaction successful"
