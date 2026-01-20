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

    @Before("@tokens")
    public void setup() {
        state.tokens = null;
        state.transactions = null;
        state.lastException = null;
        state.customer = null;
        state.merchant = null;
    }

    @After("@tokens")
    public void cleanup() {
        if (state.customer != null) {
            customerClient.unregister(state.customer);
        }
    }

    @Given("the customer has {string} tokens")
    public void customerHasNumberOfToken(String count) {
        state.tokens = customerClient.getTokens(state.customer.dtupayUuid(), Integer.parseInt(count));
    }

    @When("the customer requests {string} tokens")
    public void customerRequestsTokens(String count) {
        try {
            state.tokens = customerClient.getTokens(state.customer.dtupayUuid(), Integer.parseInt(count));
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @Then("the customer will receive a list of {string} different tokens")
    public void customer_receives_tokens(String count) {
        assertEquals(Integer.parseInt(count), state.tokens.size());
        assertEquals(state.tokens.size(), state.tokens.stream().distinct().count());
    }

    @Then("the token request is not successful")
    public void tokenRequestFailed() {
        assertNotNull(state.lastException);
    }
}
