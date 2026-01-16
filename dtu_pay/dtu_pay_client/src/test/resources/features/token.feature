Feature: Token
    Scenario: The customer requests Token
        Given the customer registers for DTUPay with first name "Jeppe", last name "Weikop", CPR "123456-1234"
        When the customer requests "5" tokens
        Then the customer will recive a list of "5" different tokens