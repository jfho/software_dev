package dtu.Steps;

import dtu.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import java.math.BigDecimal;

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
        try{
            state.transactions = customerClient.getReports(state.customer.dtupayUuid());
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @When("the merchant requests the report")
    public void merchant_requests_report() {
        try{
            state.transactions = merchantClient.getReports(state.merchant.dtupayUuid());
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @When("the manager requests the report")
    public void manager_requests_report() {
        try{
            state.transactions = managerClient.getReports();
        } catch (Exception e) {
            state.lastException = e;
        }
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

    @Then("the customer gets an empty report")
    public void customer_gets_empty_report() {
        assertNotNull(state.transactions);
        assertTrue(state.transactions.isEmpty());
    }

    @When("the customer requests the report using customer id {string}")
    public void customer_requests_report_unknown(String customerId) {
        try {
            customerClient.getReports(customerId);
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @When("the merchant requests the report using merchant id {string}")
    public void merchant_requests_report_unknown(String merchantId) {
        try {
            merchantClient.getReports(merchantId);
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @Then("the report request is not successful")
    public void report_request_failed() {
        assertNotNull(state.lastException);
    }

    @Given("the customer has done a single payment with a merchant")
    public void the_customer_has_done_a_single_payment_with_a_merchant() {
        try {
            merchantClient.pay(
                state.tokens.get(0),
                state.merchant.dtupayUuid(),
                new BigDecimal("10")
            );
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @Then("the manager gets the report of all the payments \\({string}) of all the customers")
    public void the_manager_gets_the_report_of_all_the_payments_of_all_the_customers(String numberOfPayments) {
        assertEquals(Integer.parseInt(numberOfPayments), state.transactions.size()); 
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
