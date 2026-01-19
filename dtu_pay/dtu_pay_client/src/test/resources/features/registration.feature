@registration
Feature: Registration
    Scenario: Register DTUPay customer account
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        When the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        Then the customer registration is successful

    Scenario: Register DTUPay merchant account
        Given a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        When the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        Then the merchant registration is successful

    Scenario: Unregister DTUPay customer account
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        When the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the customer unregisters for DTUPay
        Then the customer unregistration is successful

    Scenario: Unregister DTUPay merchant account
        Given a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        When the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And the merchant unregisters for DTUPay
        Then the merchant unregistration is successful

    Scenario: Customer registration fails if CPR is already registered
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        When the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        Then the customer registration is not successful
        And an error message is returned saying "customer already exists"

    Scenario: Merchant registration fails if CPR is already registered
        Given a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        When the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        Then the merchant registration is not successful
        And an error message is returned saying "merchant already exists"
