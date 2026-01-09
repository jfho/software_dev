Feature: Payment
    Scenario: Successful Payment
        Given a customer with name "Susan"
        Given the customer is registered with Simple DTU Pay
        Given a merchant with name "Daniel"
        Given the merchant is registered with Simple DTU Pay
        When the merchant initiates a payment for 10 kr by the customer
        Then the payment is successful

    Scenario: List of payments
        Given a customer with name "Susan", who is registered with Simple DTU Pay
        And a merchant with name "Daniel", who is registered with Simple DTU Pay
        Given a successful payment of "10" kr from the customer to the merchant
        When the manager asks for a list of payments
        Then the list contains a payments where customer "Susan" paid "10" kr to merchant "Daniel"

    Scenario: Customer is not known
        Given a merchant with name "Daniel", who is registered with Simple DTU Pay
        When the merchant initiates a payment for "10" kr using customer id "non-existent-id"
        Then the payment is not successful
        And an error message is returned saying "customer with id \"non-existent-id\" is unknown"
    
    Scenario: Successful Payment
        Given a customer with name "Susan", last name "Baldwin", and CPR "030154-4421"
        And the customer "4421" is registered with the bank with an initial balance of 1000 kr
        And the customer "4421" is registered with Simple DTU Pay using their bank account
        And a merchant with name "Daniel", last name "Oliver", and CPR "131161-3045"
        And the merchant "3045" is registered with the bank with an initial balance of 1000 kr
        And the merchant "3045" is registered with Simple DTU Pay using their bank account
        When the merchant initiates a payment for 10 kr by the customer
        Then the payment is successful
        And the balance of the customer at the bank is 990 kr
        And the balance of the merchant at the bank is 1010 kr