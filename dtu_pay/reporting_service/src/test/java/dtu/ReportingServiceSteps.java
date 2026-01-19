package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.NotFoundException;
import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Customer;
import dtu.models.Database;
import dtu.models.Merchant;
import dtu.models.RecordedPayment;

public class ReportingServiceSteps {
    Database db = Database.getInstance();
    ReportService reportService;
    MessageQueue mq;

    Customer customer1 = null;
    Customer customer2 = null;
    Merchant merchant1 = null;
    Merchant merchant2 = null;

    private String TRANSACTION_COMPLETED_RK = "payments.transaction.response";
    private String MERCHANT_GETTRANSACTIONS_REQ = "facade.merchant.request";
    private String CUSTOMER_GETTRANSACTIONS_REQ = "facade.customer.request";
    private String MANAGER_GETTRANSACTIONS_REQ = "facade.manager.request";

    private String MERCHANT_GETTRANSACTIONS_RES = "reports.merchant.response";
    private String CUSTOMER_GETTRANSACTIONS_RES = "reports.customer.response";
    private String MANAGER_GETTRANSACTIONS_RES = "reports.manager.response";

    Gson gson = new Gson();
    Type recordedPaymentListType = new TypeToken<List<RecordedPayment>>(){}.getType();

    List<RecordedPayment> retrievedPayments = null;

    @Before
    public void setup() {
        customer1 = null;
        customer2 = null;
        merchant1 = null;
        merchant2 = null;

        retrievedPayments = null;

        mq = new MockQueue();
        reportService = new ReportService(mq);
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
        mq.addHandler(MANAGER_GETTRANSACTIONS_RES, e -> {
            retrievedPayments = gson.fromJson(gson.toJson(e.getArgument(0, Object.class)), recordedPaymentListType);
        });
        
        Event event = new Event(MANAGER_GETTRANSACTIONS_REQ, new Object[] { UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @When("the customer requests a list of payments")
    public void customerRequestsListOfPayments() {
        mq.addHandler(CUSTOMER_GETTRANSACTIONS_RES, e -> {
            retrievedPayments = gson.fromJson(gson.toJson(e.getArgument(0, Object.class)), recordedPaymentListType);
        });
        
        Event event = new Event(CUSTOMER_GETTRANSACTIONS_REQ, new Object[] { customer1.dtupayUuid(), UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @When("the merchant requests a list of payments")
    public void the_merchant_requests_a_list_of_payments() {
        mq.addHandler(MERCHANT_GETTRANSACTIONS_RES, e -> {
            retrievedPayments = gson.fromJson(gson.toJson(e.getArgument(0, Object.class)), recordedPaymentListType);
        });
        
        Event event = new Event(MERCHANT_GETTRANSACTIONS_REQ, new Object[] { merchant1.dtupayUuid(), UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @Then("the list contains {int} payments with amounts {string}, {string}, {string}")
    public void the_list_contains_payments_with_amounts(Integer size, String amount1, String amount2, String amount3) {
        assertNotNull(retrievedPayments);
        assertEquals(size.intValue(), retrievedPayments.size());
        assertEquals(amount1, retrievedPayments.get(0).amount());
        assertEquals(amount2, retrievedPayments.get(1).amount());
        assertEquals(amount3, retrievedPayments.get(2).amount());
    }
}