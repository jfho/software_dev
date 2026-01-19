package dtu.Steps;

import dtu.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReportSteps {

    private final State state;
    private final CustomerClient customerClient = new CustomerClient();
    private final MerchantClient merchantClient = new MerchantClient();
    private final ManagerClient managerClient = new ManagerClient();

    public ReportSteps(State state) {
        this.state = state;
    }

    @When("the customer requests the report")
    public void customer_requests_report() {
        state.transactions = customerClient.getReports(state.customer.dtupayUuid());
    }

    @When("the merchant requests the report")
    public void merchant_requests_report() {
        state.transactions = merchantClient.getReports(state.merchant.dtupayUuid());
    }

    @When("the manager requests the report")
    public void manager_requests_report() {
        state.transactions = managerClient.getReports();
    }

    @Then("the customer gets the report of the {string} payment he has done")
    public void customer_report(String count) {
        assertEquals(Integer.parseInt(count), state.transactions.size());
    }

    @Then("the merchant gets the report of the {string} payment he has done")
    public void merchant_report(String count) {
        assertEquals(Integer.parseInt(count), state.transactions.size());
    }

    @Then("the manager gets the report of all the payments {string} of all the customers")
    public void manager_report(String count) {
        assertEquals(Integer.parseInt(count), state.transactions.size());
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
        if (state.merchant != null) {
            merchantClient.unregister(state.merchant);
        }
    }
}
