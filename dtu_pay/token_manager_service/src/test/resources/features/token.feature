Feature: Token

    Scenario: Initial token generation
        Given a customerId "123" with no tokens
        When the customer requests "5" tokens
        Then the customer has "5" tokens

    Scenario: Invalid token request - non empty token wallet
        Given a customerId "234" with "2" tokens
        When the customer requests "6" tokens
        Then the customer has "2" tokens

    Scenario: Token request at one token
        Given a customerId "345" with "1" tokens
        When the customer requests "5" tokens
        Then the customer has "6" tokens

    Scenario: Requests 100 tokens
        Given a customerId "345" with no tokens
        When the customer requests "100" tokens
        Then the customer has "0" tokens
    
    Scenario: A token is validated
        Given a customerId "345" with "5" tokens
        And a token is known to a merchant
        When the token is asked to be validated
        Then the customerId is returned
        And the customer has "4" tokens
    
    Scenario: A token is validated twice
        Given a customerId "345" with "5" tokens
        And a token is known to a merchant
        And the token is validated
        When the token is asked to be validated
        Then the customerId is returned as null
        And the customer has "4" tokens

    Scenario: Send customerId associated with a token
        Given a customerId "134" with "5" tokens
        And a token is known to a merchant
        When a customerId request event is emitted
        Then a customerId response includes the customerId "134"

    Scenario: Send token list to customer
        Given a customerId "345" with no tokens
        When the customer requests tokens
        Then the service generates a list of tokens
        And a tokens response includes the list of tokens
        
    Scenario: Deleting customers token at customer deletion
        Given a customerId "345" with "5" tokens
        When a customer deletion request event is emitted
        Then the customer and its tokens are deleted
        