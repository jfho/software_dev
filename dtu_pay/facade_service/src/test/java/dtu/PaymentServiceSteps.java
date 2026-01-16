package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import dtu.Adapters.Event;
import dtu.Models.Transaction;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PaymentServiceSteps {
    private MockQueue mq = new MockQueue();
    private PaymentService paymentService = new PaymentService(mq, new MockBankClient());

    private Map<String, Event> publishedEvents = new HashMap<>();

    private String customerId;
    private String merchantId;
    private Transaction transaction;

    public PaymentServiceSteps() {
        handleEvent("payments.customerid.request");
        handleEvent("payments.customerbankaccount.request");
        handleEvent("payments.merchantbankaccount.request");
        handleEvent("payments.transaction.report");
        handleEvent("payments.transaction.status");
    }

    private void handleEvent(String topic) {
        mq.addHandler(topic, event -> publishedEvents.put(topic, event));
    }

    @Given("a customer with id {string}")
    public void a_customer_with_id(String customerId) {
        this.customerId = customerId;

        mq.addHandler("payments.customerid.request", event -> {
            String correlationId = event.getArgument(1, String.class);
            mq.publish(new Event("tokens.customerid.response", new Object[] { customerId, correlationId }));
        });
    }

    @Given("a merchant with id {string}")
    public void a_merchant_with_id(String merchantId) {
        this.merchantId = merchantId;
    }

    @Given("a transaction with token {string} and amount {string} kr")
    public void a_transaction_with_token_and_amount_kr(String tokenId, String amount) {
        transaction = new Transaction(tokenId, merchantId, new BigDecimal(amount));
    }

    @Given("a customer bank account with id {string}")
    public void a_customer_bank_account_with_id(String customerBankId) {
        mq.addHandler("payments.customerbankaccount.request", event -> {
            String correlationId = event.getArgument(1, String.class);
            mq.publish(
                    new Event("accounts.customerbankaccount.response", new Object[] { customerBankId, correlationId }));
        });
    }

    @Given("a merchant bank account with id {string}")
    public void a_merchant_bank_account_with_id(String merchantBankId) {
        mq.addHandler("payments.merchantbankaccount.request", event -> {
            String correlationId = event.getArgument(1, String.class);
            mq.publish(
                    new Event("accounts.merchantbankaccount.response", new Object[] { merchantBankId, correlationId }));
        });
    }

    @When("the payment is registered by the payment service")
    public void the_payment_is_registered_by_the_payment_service() throws Exception {
        paymentService.registerTransaction(transaction);
    }

    @Then("the token service is asked for the customer id")
    public void the_token_service_is_asked_for_the_customer_id() {
        Event event = publishedEvents.get("payments.customerid.request");

        assertTrue(event != null);

        String actualToken = event.getArgument(0, String.class);
        assertEquals(transaction.tokenId(), actualToken);
    }

    @Then("the account service is asked for the customer bank account")
    public void the_account_service_is_asked_for_the_customer_bank_account() {
        Event event = publishedEvents.get("payments.customerbankaccount.request");

        assertTrue(event != null);
        assertEquals(customerId, event.getArgument(0, String.class));
    }

    @Then("the account service is asked for the merchant bank account")
    public void the_account_service_is_asked_for_the_merchant_bank_account() {
        Event event = publishedEvents.get("payments.merchantbankaccount.request");
        assertTrue(event != null);
        assertEquals(merchantId, event.getArgument(0, String.class));
    }

    @Then("the reporting service receives the transaction")
    public void the_reporting_service_receives_the_transaction() {
        Event event = publishedEvents.get("payments.transaction.report");
        assertTrue(event != null);

        assertEquals(customerId, event.getArgument(0, String.class));
        assertEquals(merchantId, event.getArgument(1, String.class));
        assertEquals(transaction.amount().toString(), event.getArgument(2, String.class));

    }

    @Then("the reporting service receives a successful transaction status")
    public void the_reporting_service_receives_a_successful_transaction_status() {
        Event event = publishedEvents.get("payments.transaction.status");
        assertTrue(event != null);
        assertEquals("Bank transaction successful", event.getArgument(0, String.class));
    }
}