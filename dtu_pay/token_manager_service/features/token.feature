Feature: Token

    Scenario: Initial token generation
        Given a customerId "123" with no tokens
        When the customer requests tokens
        Then the customer has "6" tokens

    Scenario: Invalid token request - non empty token wallet
        Given a customerId "234" with "2" tokens
        When the customer requests tokens
        Then the customer has "2" tokens

