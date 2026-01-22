# Author: Jane Doe
Feature: Account service
    Scenario: Register DTUPay customer account
        When a customer registers for DTUPay with first name "Alice", last name "Alicey", CPR "123456-1234", bank ID "12345678"
        Then the customer registration is successful

    Scenario: Register DTUPay merchant account
        When a merchant registers for DTUPay with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321"
        Then the merchant registration is successful

    Scenario: Retrieve DTUPay customer account information
        Given a customer account with first name "Alice", last name "Alicey", CPR "123456-1234", bank ID "12345678"
        Then the customer account information can be retrieved

    Scenario: Retrieve DTUPay merchant account information
        Given a merchant account with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321"
        Then the merchant account information can be retrieved

    Scenario: Retrieve non-existent DTUPay customer account information
        When the customer with id "non-existent" is retrieved
        Then the customer is null

    Scenario: Retrieve non-existent DTUPay merchant account information
        When the merchant with id "non-existent" is retrieved
        Then the merchant is null

    Scenario: Unregister DTUPay customer account
        Given a customer account with first name "Alice", last name "Alicey", CPR "123456-1234", bank ID "12345678"
        When the customer unregisters for DTUPay
        Then the customer unregistration is successful

    Scenario: Unregister DTUPay merchant account
        Given a merchant account with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321"
        When the merchant unregisters for DTUPay
        Then the merchant unregistration is successful
    
    Scenario: Retrieve bank ID associated with customer ID
        Given a customer account with first name "Alice", last name "Alicey", CPR "123456-1234", bank ID "12345678"
        And a subscriber for the customer bank account response event
        When a bank account request for the customer with correlation ID "42424242" is emitted
        Then a bank account response for the customer with bank ID "12345678" and correlation ID "42424242" is emitted

    Scenario: Retrieve bank ID associated with merchant ID
        Given a merchant account with first name "Alice", last name "Alicey", CPR "123456-1234", bank ID "12345678"
        And a subscriber for the merchant bank account response event
        When a bank account request for the merchant with correlation ID "42424242" is emitted
        Then a bank account response for the merchant with bank ID "12345678" and correlation ID "42424242" is emitted
