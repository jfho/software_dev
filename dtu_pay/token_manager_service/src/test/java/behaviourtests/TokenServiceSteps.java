package behaviourtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.util.*;

import dtu.Controllers.TokenController;
import dtu.Models.Customer;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

public class TokenServiceSteps {
    private String customerId;
    private TokenController tc = new TokenController();


    @Given("a customerId {string} with no tokens")
    public void aCustomerIdWithNoTokens(String string) {
        customerId = string;
        assertTrue( tc.getAllTokensByCustomer(customerId).isEmpty() );
    }

    @Given("a customerId {string} with {string} tokens")
    public void aCustomerIdWithTokens(String customerId, String amount) {
        this.customerId = customerId;

        tc.createTokenHELPER(customerId, Integer.parseInt(amount));

        assertEquals(Integer.parseInt(amount), tc.getAllTokensByCustomer(customerId).size() );
    }

    @When("the customer requests tokens")
    public void theCustomerRequestsTokens() {
        tc.createToken(customerId);
    }

    @Then("the customer has {string} tokens")
    public void theCustomerHasTokens(String string) {
        assertEquals(Integer.parseInt(string), tc.getAllTokensByCustomer(customerId).size());
    }

}