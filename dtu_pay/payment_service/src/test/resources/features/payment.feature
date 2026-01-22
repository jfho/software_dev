# Author: s253872

Feature: Payment Service
  Scenario: Successful payment
    Given a transaction with token "valid-token", amount "10" kr and merchant id "mer-123"
    When the payment is received by the payment service
    And a customer bank account with id "cus-bank-123" is received
    And a merchant bank account with id "mer-bank-123" is received
    Then the payment is successful
    And the transaction is removed from the pending list

  Scenario: Payment fails because customer is null
    Given a transaction with token "invalid-token", amount "10" kr and merchant id "mer-123"
    When the payment is received by the payment service
    And a customer bank account with id "null" is received
    And a merchant bank account with id "mer-bank-123" is received
    Then the payment is unsuccessful
    And the transaction is removed from the pending list

  Scenario: Payment fails because merchant is null
    Given a transaction with token "valid-token", amount "10" kr and merchant id "mer-123"
    When the payment is received by the payment service
    And a customer bank account with id "cus-bank-123" is received
    And a merchant bank account with id "null" is received
    Then the payment is unsuccessful

  Scenario: Payment fails because amount is null
    Given a transaction with token "valid-token", amount "" kr and merchant id "mer-123"
    When the payment is received by the payment service
    And a customer bank account with id "cus-bank-123" is received
    And a merchant bank account with id "mer-bank-123" is received
    Then the payment is unsuccessful
  
  Scenario: Payment fails because amount is negative
    Given a transaction with token "valid-token", amount "-10" kr and merchant id "mer-123"
    When the payment is received by the payment service
    And a customer bank account with id "cus-bank-123" is received
    And a merchant bank account with id "mer-bank-123" is received
    Then the payment is unsuccessful