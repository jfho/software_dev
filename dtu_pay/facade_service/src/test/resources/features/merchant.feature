# Author: s253872

Feature: Merchant Facade Service
    Scenario: Merchant registration success
        Given a merchant with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321"
        And the message queue is listening for merchant registration events
        When the merchant registers for DTUPay
        Then the merchant object is returned with a DTUPay UUID
    
    Scenario: Handle service timeout
        Given a merchant with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321"
        When the merchant registers for DTUPay
        And the backend service does not reply
        Then the facade gives an error