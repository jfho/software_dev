package dtu.example;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PersonServiceSteps {
    Person result;
    PersonService service = new PersonService();
    String errorMessage = "";

    @When("we fetch a person")
    public void weFetchAPerson() {
        result = service.getPerson();
    }

    @Then("we get {string} from {string}")
    public void weGetFrom(String name, String address) {
        assertEquals(name, result.getName());
        assertEquals(address, result.getAddress());
    }

    
    @When("we update a person with {string} and {string}")
    public void weUpdateAPersonWithAnd(String name, String address) {
    // Write code here that turns the phrase above into concrete actions
        try {
            service.updatePerson(name, address);
        }   catch (PersonServiceException e) {
            errorMessage = "BadRequest";
        }
    }

    @Then("we get a {string} response")
    public void weGetAResponse(String expectedError) {
        assertEquals(expectedError, errorMessage);
    }

}
