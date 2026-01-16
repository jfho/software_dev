package dtu.Steps;

import dtu.*;
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
        state.tokens = customerClient.getTokens(state.customer.dtupayUuid(), Integer.parseInt(count));
    }

    @Then("the customer will recive a list of {string} different tokens")
    public void customer_receives_tokens(String count) {
        assertEquals(Integer.parseInt(count), state.tokens.size());
        assertEquals(state.tokens.size(), state.tokens.stream().distinct().count());
    }

    @Given("And the merchant has a token from the customer")
    public void merchant_has_token() {
        state.tokens = customerClient.getTokens(state.customer.dtupayUuid(), 1);
    }
}
