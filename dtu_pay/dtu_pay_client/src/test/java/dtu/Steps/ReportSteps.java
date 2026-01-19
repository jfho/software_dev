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
    private BankClient bank = new BankClient();

    public ReportSteps(State state) {
        this.state = state;
    }

    @Before("@reports")
    public void setup() {
        state.tokens = null;
        state.transactions = null;
        state.lastException = null;
        state.customer = null;
        state.merchant = null;
    }

    @After("@reports")
    public void cleanup() {
        if (state.customer != null) {
            customerClient.unregister(state.customer);
            
            if (state.customer.bankAccountUuid() != null) {
                bank.unregister(state.customer.bankAccountUuid());
            }
        }

        if (state.merchant != null) {
            merchantClient.unregister(state.merchant);
            bank.unregister(state.merchant.bankAccountUuid());
        }
    }

    @When("the customer requests the report")
    public void customerRequestsReport() {
        state.transactions = customerClient.getReports(state.customer.dtupayUuid());
    }

    @When("the merchant requests the report")
    public void merchantRequestsReport() {
        state.transactions = merchantClient.getReports(state.merchant.dtupayUuid());
    }

    @When("the manager requests the report")
    public void managerRequestsReport() {
        state.transactions = managerClient.getReports();
    }

    @When("the merchant requests the report using merchant id {string}")
    public void managerRequestsReportUnknown(String merchantId) {
        state.transactions = merchantClient.getReports(merchantId);
    }

    @When("the customer requests the report using customer id {string}")
    public void customerRequestsReportUnknown(String customerId) {
        state.transactions = customerClient.getReports(customerId);
    }

    @Then("the customer gets the report of the {string} payment he has done")
    public void customerGetsReport(String count) {
        assertEquals(Integer.parseInt(count), state.transactions.size());
    }

    @Then("the merchant gets the report of the {string} payment he has done")
    public void merchantGetsReport(String count) {
        assertEquals(Integer.parseInt(count), state.transactions.size());
    }

    @Then("the manager gets the report of all the {string} payments of all the customers")
    public void managerGetsReport(String count) {
        assertEquals(Integer.parseInt(count), state.transactions.size());
    }

    @Then("the customer gets an empty report")
    public void customerGetsEmptyReport() {
        assertNotNull(state.transactions);
        assertTrue(state.transactions.isEmpty());
    }

    @Then("the report request is not successful")
    public void reportRequestFailed() {
        assertNotNull(state.lastException);
    }
}
