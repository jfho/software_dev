# Author: s253872

Feature: Customer Facade Service
    Scenario: Customer registration success
        Given a customer with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321"
        And the message queue is listening for customer registration events
        When the customer registers for DTUPay
        Then the customer object is returned with a DTUPay UUID
    
    Scenario: Handle service timeout
        Given a customer with first name "Bob", last name "Bobby", CPR "654321-4321", bank ID "87654321"
        When the customer registers for DTUPay
        And the backend service does not reply
        Then the facade gives an error