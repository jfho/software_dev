package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.logging.Logger;

import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.services.TokenService;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TokenServiceSteps {

    private String customerId;
    private String tokenId;
    private String validationCustomerId;
    private Event receivedEvent;
    private String correlationId;
    private List<String> tokenList;
    
    private MessageQueue mq = new MockQueue();
    private TokenService tc = new TokenService(mq);

    private String customerIdResponse = "tokens.customerid.response";
    private String createTokensResponse = "tokens.createtokens.response";

    private String customerIdRequest = "payments.customerid.request";
    private String createTokensRequest = "facade.createtokens.request";
    
    @After
    public void clearTheData() {
        tc.clearData();
        customerId = null;
        tokenId = null;
        validationCustomerId = null;
        receivedEvent = null;
        tokenList = null;
    }

    @Given("a customerId {string} with no tokens")
    public void aCustomerIdWithNoTokens(String string) {        
        customerId = string;
    }

    @Given("a customerId {string} with {string} tokens")
    public void aCustomerIdWithTokens(String customerId, String amount) {
        this.customerId = customerId;
        tc.createTokens(customerId, Integer.parseInt(amount));
    }

    @Given("the token is validated")
    public void theTokenIsValidated() {
        tc.validateToken(tokenId);
    }

    @Given("a token is known to a merchant")
    public void aTokenIsKnownToAMerchant() {
        tokenId = tc.getAllTokensByCustomer(customerId).get(0);
    }

    @When("the customer requests {string} tokens")
    public void theCustomerRequestsTokens(String string) {
        tc.createTokens(customerId, Integer.parseInt(string));
    }

    @When("the token is asked to be validated")
    public void theTokenIsAskedToBeValidated() {
        validationCustomerId = tc.validateToken(tokenId);
    }

    @When("the payment service requests a customerId")
    public void thePaymentServiceRequestsACustomerId() {
        mq.addHandler(customerIdResponse, event -> {
            this.receivedEvent = event;
        });
    
        correlationId = "string";
        mq.publish(new Event(customerIdResponse, new Object[] {customerId, correlationId} ));
    }

    @When("the customer requests tokens")
    public void theCustomerRequestsTokens() {
        mq.addHandler(createTokensResponse, event -> {
            this.receivedEvent = event;
        });

        correlationId = "string";
        mq.publish(new Event(createTokensRequest, new Object[] {customerId, 6, correlationId} ));
    }

    @Then("the customer has {string} tokens")
    public void theCustomerHasTokens(String string) {
        assertEquals(Integer.parseInt(string), tc.getAllTokensByCustomer(customerId).size());
    }

    @Then("the customerId is returned")
    public void theCustomerIdIsReturned() {
        assertEquals(validationCustomerId, customerId);
    }

    @Then("the customerId is returned as null")
    public void theCustomerIdIsReturnedAsNull() {
        assertTrue(tc.validateToken(tokenId) == null);    
    }

    @Then("a customerId response includes the customerId {string}")
    public void aCustomerIdResponseIncludesTheCustomerId(String string) {
        assertNotNull(receivedEvent);
        String respCustomerId = receivedEvent.getArgument(0, String.class);
        String respCorrId = receivedEvent.getArgument(1, String.class);
        assertEquals(customerId, respCustomerId);
        assertEquals(correlationId, respCorrId);
    }

    @Then("the service generates a list of tokens")
    public void theServiceGeneratesAListOfTokens() {
        tokenList = tc.getAllTokensByCustomer(customerId);
    }

    @Then("a tokens response includes the list of tokens")
    public void aTokensResponseIncludesTheListOfTokens() {
        assertNotNull(receivedEvent);
        
        List<String> respTokenList = receivedEvent.getArgument(0, List.class);
        String respCorrId = receivedEvent.getArgument(1, String.class);
        
        assertEquals(tokenList, respTokenList);
        assertEquals(correlationId, respCorrId);
    }
}