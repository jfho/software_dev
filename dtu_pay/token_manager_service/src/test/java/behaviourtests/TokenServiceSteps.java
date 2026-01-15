package behaviourtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import dtu.Controllers.TokenController;
import dtu.messaging.implementations.RabbitMqQueue;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TokenServiceSteps {
    private String customerId;
    private String tokenId;
    private String validationCustomerId;
    private TokenController tc = new TokenController(new RabbitMqQueue());

    @After
    public void clearTheData() {
        tc.clearData();
        customerId = "";
        tokenId = "";
        validationCustomerId = "";
    }

    @Given("a customerId {string} with no tokens")
    public void aCustomerIdWithNoTokens(String string) {
        customerId = string;
        assertTrue( tc.getAllTokensByCustomer(customerId).isEmpty() );
    }

    @Given("a customerId {string} with {string} tokens")
    public void aCustomerIdWithTokens(String customerId, String amount) {
        this.customerId = customerId;

        tc.createTokens(customerId, Integer.parseInt(amount));

        assertEquals(Integer.parseInt(amount), tc.getAllTokensByCustomer(customerId).size() );
    }

    @When("the customer requests {string} tokens")
    public void theCustomerRequestsTokens(String string) {
        tc.createTokens(customerId, Integer.parseInt(string));
    }

    @Then("the customer has {string} tokens")
    public void theCustomerHasTokens(String string) {
        assertEquals(Integer.parseInt(string), tc.getAllTokensByCustomer(customerId).size());
    }

    @Given("a token is known to a merchant")
    public void aTokenIsKnownToAMerchant() {
        tokenId = tc.getAllTokensByCustomer(customerId).get(0);    
    }

    @When("the token is asked to be validated")
    public void theTokenIsAskedToBeValidated() {
        validationCustomerId = tc.validateToken(tokenId);
    }

    @Then("the customerId is returned")
    public void theCustomerIdIsReturned() {
        assertEquals(validationCustomerId, customerId);
    }

    @Given("the token is validated")
    public void theTokenIsValidated() {
        tc.validateToken(tokenId);
    }

    @Then("the customerId is returned as null")
    public void theCustomerIdIsReturnedAsNull() {
        assertTrue(tc.validateToken(tokenId) == null);    
    }

}