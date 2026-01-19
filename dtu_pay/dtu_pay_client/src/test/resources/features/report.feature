Feature: Report
    Scenario: Report DTUPay customer account
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "10"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        Given the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And the merchant initiates a transaction for "10" kr
        When the customer requests the report 
        Then the customer gets the report of the "1" payment he has done

    Scenario: Report DTUPay merchant account
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "10"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        Given the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the merchant initiates a transaction for "10" kr
        When the merchant requests the report 
        Then the merchant gets the report of the "1" payment he has done
    
    Scenario: Report DTUPay manager account
        Given a customer bank account with first name "Jeppe", last name "Weikop", CPR "123456-1234", and balance "10"
        And a merchant bank account with first name "Caroline", last name "Strauss", CPR "654321-4321", and balance "1000"
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        Given the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And the customer has done a single payment with a merchant
        When the manager requests the report
        Then the manager gets the report of all the payments ("1") of all the customers

    Scenario: Customer requests report but has no payments
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the customer requests the report
        Then the customer gets an empty report
    

