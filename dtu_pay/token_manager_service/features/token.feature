Feature: Token

    Scenario: Initial token generation
        Given a customerId "123" with no tokens
        When the customer requests "6" tokens
        Then the customer has "6" tokens

    Scenario: Invalid token request - non empty token wallet
        Given a customerId "234" with "2" tokens
        When the customer requests "6" tokens
        Then the customer has "2" tokens

    Scenario: Token request at one token
        Given a customerId "345" with "1" tokens
        When the customer requests "6" tokens
        Then the customer has "6" tokens

    Scenario: Requests 100 tokens
        Given a customerId "345" with no tokens
        When the customer requests "100" tokens
        Then the customer has "6" tokens
    
    Scenario: A token is validated
        Given a customerId "345" with "6" tokens
        And a token is known to a merchant
        When the token is asked to be validated
        Then the customerId is returned
        And the customer has "5" tokens
    
    Scenario: A token is validated twice
        Given a customerId "345" with "6" tokens
        And a token is known to a merchant
        And the token is validated
        When the token is asked to be validated
        Then the customerId is returned as null
        And the customer has "5" tokens

    Scenario: Send customerId associated with a token
        Given a customerId "134" with "6" tokens
        And a token is known to a merchant
        And a subscriber for the customerId response event
        When a request customerId event with the known token and correlation ID "6767676" is emitted
        Then a customerId response event with customerId "134" and correlation ID "6767676" is emitted
