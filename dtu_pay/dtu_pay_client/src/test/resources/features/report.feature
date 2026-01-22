# Author: s215698

@reports
Feature: Report
    Scenario: Report DTUPay customer account
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "1000"
        And another customer bank account with first name "Alice", last name "Smith", CPR "424242-4242", and balance "2000"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        And the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And another customer registers for DTUPay with first name "Alice", last name "Smith", CPR "424242-4242"
        And the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        When the merchant has a token from the customer
        And the merchant initiates a transaction for "10" kr
        And the merchant has a token from the other customer
        And the merchant initiates a transaction for "15" kr
        And the customer requests the report 
        Then the customer gets the report of the "1" payments
        And there is a payment from the customer to the merchant with amount "10"

    Scenario: Report DTUPay merchant account
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "100"
        And another customer bank account with first name "Alice", last name "Smith", CPR "424242-4242", and balance "100"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        And another merchant bank account with first name "Bob", last name "Jones", CPR "555555-5555", and balance "1000"
        And the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And another merchant registers for DTUPay with first name "Bob", last name "Jones", CPR "555555-5555"
        And the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And another customer registers for DTUPay with first name "Alice", last name "Smith", CPR "424242-4242"
        When the merchant has a token from the customer
        And the merchant initiates a transaction for "10" kr
        And the merchant has a token from the other customer
        And the merchant initiates a transaction for "15" kr
        And the other merchant has a token from the customer
        And the other merchant initiates a transaction for "20" kr
        And the other merchant has a token from the other customer
        And the other merchant initiates a transaction for "25" kr
        And the merchant requests the report 
        Then the merchant gets the report of "2" payments
        And there is a payment to the merchant with amount "10"
        And the payment does not contain the customer ID
        And there is a payment to the merchant with amount "15"
        And the payment does not contain the customer ID
    
    Scenario: Report DTUPay manager account
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "100"
        And another customer bank account with first name "Alice", last name "Smith", CPR "424242-4242", and balance "100"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        And another merchant bank account with first name "Bob", last name "Jones", CPR "555555-5555", and balance "1000"
        And the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And another customer registers for DTUPay with first name "Alice", last name "Smith", CPR "424242-4242"
        And the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And another merchant registers for DTUPay with first name "Bob", last name "Jones", CPR "555555-5555"
        When the merchant has a token from the customer
        And the merchant initiates a transaction for "10" kr
        And the other merchant has a token from the other customer
        And the other merchant initiates a transaction for "25" kr
        And the manager requests the report
        Then the manager gets the report of the at least "2" payments
        And there is a payment from the customer to the merchant with amount "10"
        And there is a payment from the other customer to the other merchant with amount "25"

    Scenario: Customer requests report but has no payments
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the customer requests the report
        Then the customer gets an empty report
    

