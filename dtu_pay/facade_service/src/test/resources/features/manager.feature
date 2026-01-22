# Author: s253872

Feature: Manager Facade Service
    Scenario: Manager report retrieval success
        Given a payment for "10" kr exists
        And the message queue is listening for manager report request events
        When the manager requests a report of payments
        Then a list of all payments are returned

    Scenario: Handle service timeout
        When the manager requests a report of payments
        And the backend service does not reply
        Then the facade gives an error