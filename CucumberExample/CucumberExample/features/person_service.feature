Feature: Person service

    Scenario: Person service returns a person
        When we fetch a person
        Then we get "Susan" from "USA"

    Scenario: Update a person
        When we update a person with "Mathias" and "Denmark"
        And we fetch a person
        Then we get "Mathias" from "Denmark"
    
    Scenario: Bad request
        When we update a person with "John" and "-none-"
        Then we get a "BadRequest" response