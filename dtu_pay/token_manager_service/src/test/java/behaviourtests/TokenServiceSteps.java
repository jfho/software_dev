package behaviourtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    private MessageQueue mq = new MockQueue();
    private TokenService tc = new TokenService(mq);

    private String customerIdResponse = "token.customerid.response";
    
    @After
    public void clearTheData() {
        tc.clearData();
        customerId = null;
        tokenId = null;
        validationCustomerId = null;
        receivedEvent = null;
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
        assertNotNull(tokenId);
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

    @Given("a subscriber for the customerId response event")
    public void aSubscriberForTheCustomerResponseIdEvent() {
        mq.addHandler(customerIdResponse, event -> {
            this.receivedEvent = event;
        });
    }

    @When("a request customerId event with the known token and correlation ID {string} is emitted")
    public void aRequestCustomerIdEventWithTheKnownTokenAndCorrelationIDIsEmitted(String string) {
        correlationId = string;
        mq.publish(new Event(customerIdResponse, new Object[] {customerId, correlationId} ));
    }

    @Then("a customerId response event with customerId {string} and correlation ID {string} is emitted")
    public void aCustomerIdResponseEventWithCustomerIdAndCorrelationIDIsEmitted(String string, String string2) {
        assertNotNull(receivedEvent);
        String respCustomerId = receivedEvent.getArgument(0, String.class);
        String respCorrId = receivedEvent.getArgument(1, String.class);
        assertEquals(customerId, respCustomerId);
        assertEquals(correlationId, respCorrId);
    }
}