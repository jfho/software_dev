package dtu.Steps;

import dtu.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class TokenSteps {

    private final State state;
    private final CustomerClient customerClient = new CustomerClient();

    public TokenSteps(State state) {
        this.state = state;
    }

    @When("the customer requests {string} tokens")
    public void customer_requests_tokens(String count) {
        try{
            state.tokens = customerClient.getTokens(state.customer.dtupayUuid(), Integer.parseInt(count));
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @Then("the customer will recive a list of {string} different tokens")
    public void customer_receives_tokens(String count) {
        assertEquals(Integer.parseInt(count), state.tokens.size());
        assertEquals(state.tokens.size(), state.tokens.stream().distinct().count());
    }

    @Given("And the merchant has a token from the customer")
    public void merchant_has_token() {
        try{
            state.tokens = customerClient.getTokens(state.customer.dtupayUuid(), 1);
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @Then("the token request is not successful")
    public void token_request_failed() {
        assertNotNull(state.lastException);
    }

    @Then("the customer gets a message to {string}")
    public void customer_gets_message(String msg) {
        assertNotNull(state.lastException);
        assertTrue(state.lastException.getMessage().contains(msg));
    }

    @Given("the customer has a valid token")
    public void the_customer_has_a_valid_token() {
        try{
            state.tokens = customerClient.getTokens(state.customer.dtupayUuid(), 6);
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @Given("the customer has {string} tokens")
    public void the_customer_has_tokens(String string) {
        try{
            state.tokens = customerClient.getTokens(state.customer.dtupayUuid(), Integer.parseInt(string));
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @Before
    public void setup() {
        state.tokens = null;
        state.transactions = null;
        state.lastException = null;
        state.customer = null;
        state.merchant = null;
    }

    @After
    public void cleanup() {
        if (state.customer != null) {
            customerClient.unregister(state.customer);
        }
    }
}
