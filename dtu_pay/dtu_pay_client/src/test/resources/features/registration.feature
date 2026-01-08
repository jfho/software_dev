Feature: Registration
    Scenario: Register DTUPay customer account
        Given a customer bank account with name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        When the customer registers for DTUPay with username "jweikop"
        Then the customer registration is successful

    Scenario: Register DTUPay merchant account
        Given a merchant bank account with name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        When the merchant registers for DTUPay with username "cstrauss"
        Then the merchant registration is successful

    Scenario: Unregister DTUPay customer account
        Given a customer bank account with name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        When the customer registers for DTUPay with username "jweikop"
        And the customer unregisters for DTUPay
        Then the customer unregistration is successful

    Scenario: Unregister DTUPay merchant account
        Given a merchant bank account with name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        When the merchant registers for DTUPay with username "cstrauss"
        And the merchant unregisters for DTUPay
        Then the merchant unregistration is successful