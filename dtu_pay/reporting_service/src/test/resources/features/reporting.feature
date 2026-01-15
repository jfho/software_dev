Feature: Reporting service
    Scenario: Manager requests list of payments
        Given a customer account with first name "Alice", last name "Alicey", CPR "123456-1234", bank ID "12345678", DTUPay ID "111"
        And a merchant account with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321", DTUPay ID "222"
        When a payment event with amount "10" is received from ID "111" to "222"
        And a payment event with amount "20" is received from ID "111" to "222"
        And a payment event with amount "30" is received from ID "111" to "222"
        When the manager requests a list of payments
        Then the list contains 3 payments with amounts "10", "20", "30"

    Scenario: Customer requests list of payments
        Given a customer account with first name "Alice", last name "Alicey", CPR "123456-1234", bank ID "12345678", DTUPay ID "111"
        And another customer account with first name "Charlie", last name "Charlieton", CPR "222222-2222", bank ID "22222222", DTUPay ID "222"
        And a merchant account with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321", DTUPay ID "333"
        And another merchant account with first name "Daniel", last name "Daniels", CPR "4444-4444", bank ID "44444444", DTUPay ID "444"
        When a payment event with amount "10" is received from ID "111" to "333"
        And a payment event with amount "20" is received from ID "111" to "444"
        And a payment event with amount "30" is received from ID "222" to "444"
        And a payment event with amount "30" is received from ID "111" to "333"
        And a payment event with amount "20" is received from ID "222" to "333"
        When the customer requests a list of payments
        Then the list contains 3 payments with amounts "10", "20", "30"

    Scenario: Merchant requests list of payments
        Given a customer account with first name "Alice", last name "Alicey", CPR "123456-1234", bank ID "12345678", DTUPay ID "111"
        And another customer account with first name "Charlie", last name "Charlieton", CPR "222222-2222", bank ID "22222222", DTUPay ID "222"
        And a merchant account with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321", DTUPay ID "333"
        And another merchant account with first name "Daniel", last name "Daniels", CPR "4444-4444", bank ID "44444444", DTUPay ID "444"
        When a payment event with amount "10" is received from ID "111" to "333"
        And a payment event with amount "20" is received from ID "111" to "444"
        And a payment event with amount "30" is received from ID "222" to "444"
        And a payment event with amount "30" is received from ID "111" to "333"
        And a payment event with amount "20" is received from ID "222" to "333"
        When the merchant requests a list of payments
        Then the list contains 3 payments with amounts "10", "30", "20"


