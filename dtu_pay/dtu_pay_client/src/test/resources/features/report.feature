Feature: Report
    Scenario: Report DTUPay customer account
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        And the customer has done a single payment with a merchant
        When the customer requests the report 
        Then the customer gets the report of the "1" payment he has done

    Scenario: Report DTUPay merchant account
        Given the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And the merchant has done a single payment with a customer
        When the merchant requests the report 
        Then the merchant gets the report of the "1" payment he has done
    
    Scenario: Report DTUPay manager account
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        Given the merchant registers for DTUPay with first name "Caroline", last name "Strauss", CPR "654321-4321"
        And the customer has done a single payment with a merchant
        When the manager requests the report
        Then the manager gets the report of all the payments ("1") of all the customers

        
