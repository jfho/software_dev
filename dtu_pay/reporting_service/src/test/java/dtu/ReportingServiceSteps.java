package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.NotFoundException;
import dtu.Controllers.ReportController;
import dtu.MessagingUtils.Event;
import dtu.MessagingUtils.MessageQueue;
import dtu.Models.Customer;
import dtu.Models.Database;
import dtu.Models.Merchant;
import dtu.Models.RecordedPayment;

public class ReportingServiceSteps {
    Database db = Database.getInstance();
    ReportController controller;
    MessageQueue mq;

    Customer customer1 = null;
    Customer customer2 = null;
    Merchant merchant1 = null;
    Merchant merchant2 = null;

    List<RecordedPayment> retrievedPayments = null;

    private String TRANSACTION_COMPLETED_RK = "payments.transaction.report";

    @Before
    public void setup() {
        customer1 = null;
        customer2 = null;
        merchant1 = null;
        merchant2 = null;

        retrievedPayments = null;

        mq = new MockQueue();
        controller = new ReportController(mq);
        db.clean();
    }

    @Given("a customer account with first name {string}, last name {string}, CPR {string}, bank ID {string}, DTUPay ID {string}")
    public void customerIsRegistered(String fn, String ln, String cpr, String bankId, String dtupayId) {
        customer1 = new Customer(fn, ln, cpr, bankId, dtupayId);
    }

    @Given("another customer account with first name {string}, last name {string}, CPR {string}, bank ID {string}, DTUPay ID {string}")
    public void anotherCustomerIsRegistered(String fn, String ln, String cpr, String bankId, String dtupayId) {
        customer2 = new Customer(fn, ln, cpr, bankId, dtupayId);
    }

    @Given("a merchant account with first name {string}, last name {string}, CPR {string}, bank ID {string}, DTUPay ID {string}")
    public void merchantIsRegistered(String fn, String ln, String cpr, String bankId, String dtupayId) {
        merchant1 = new Merchant(fn, ln, cpr, bankId, dtupayId);
    }

    @Given("another merchant account with first name {string}, last name {string}, CPR {string}, bank ID {string}, DTUPay ID {string}")
    public void anotherMerchantIsRegistered(String fn, String ln, String cpr, String bankId, String dtupayId) {
        merchant2 = new Merchant(fn, ln, cpr, bankId, dtupayId);
    }

    @When("a payment event with amount {string} is received from ID {string} to {string}")
    public void paymentEventReceived(String amount, String customerId, String merchantId) {
        Event event = new Event(TRANSACTION_COMPLETED_RK, new Object[] { customerId, merchantId, amount } );
        mq.publish(event);
    }

    @When("the manager requests a list of payments")
    public void managerRequestsListOfPayments() {
        retrievedPayments = controller.getAllTransactions();
    }

    @When("the customer requests a list of payments")
    public void customerRequestsListOfPayments() {
        retrievedPayments = controller.getTransactionsForCustomer(customer1.dtupayUuid());
    }

    @When("the merchant requests a list of payments")
    public void the_merchant_requests_a_list_of_payments() {
        retrievedPayments = controller.getTransactionsForMerchant(merchant1.dtupayUuid());
    }

    @Then("the list contains {int} payments with amounts {string}, {string}, {string}")
    public void the_list_contains_payments_with_amounts(Integer size, String amount1, String amount2, String amount3) {
        assertNotNull(retrievedPayments);
        assertEquals(size.intValue(), retrievedPayments.size());
        assertEquals(amount1, retrievedPayments.get(0).amount());
    }
}