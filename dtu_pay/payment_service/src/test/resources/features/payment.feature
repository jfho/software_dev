Feature: Payment
    Scenario: Successful payment    
        Given a transaction with token "fb96b25f-f4f4-4664-a9a2-c4be60fea2a6", amount "10" kr and merchant id "d98d6ffe-6033-4d18-9297-0021e95fe6d3"
        When the payment is received by the payment service
        And a customer bank account with id "019bdbc8-9904-784a-8bfa-3f450ebfe2d6" is received
        And a merchant bank account with id "9c30e743-a22f-4406-b1a0-52360f0e5e2a" is received
        Then the payment is successful