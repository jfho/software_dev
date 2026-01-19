package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import dtu.messagingUtils.Event;
import dtu.models.Transaction;
import dtu.services.PaymentService;
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
    private String correlationId;

    // --- Topic Constants (Matched to Service) ---
    private final String PAYMENTS_REGISTER_REQ_RK = "facade.transaction.request";
    private final String PAYMENTS_REGISTER_RES_RK = "payments.transaction.response";

    private final String TOKEN_CUSTOMERID_REQ_RK = "payments.customerid.request";
    private final String TOKEN_CUSTOMERID_RES_RK = "tokens.customerid.response";

    private final String BANKACCOUNT_CUSTOMER_REQ_RK = "payments.customerbankaccount.request";
    private final String BANKACCOUNT_MERCHANT_REQ_RK = "payments.merchantbankaccount.request";
    private final String BANKACCOUNT_CUSTOMER_RES_RK = "accounts.customerbankaccount.response";
    private final String BANKACCOUNT_MERCHANT_RES_RK = "accounts.merchantbankaccount.response";

    public PaymentServiceSteps() {
        handleEvent(TOKEN_CUSTOMERID_REQ_RK);
        handleEvent(BANKACCOUNT_CUSTOMER_REQ_RK);
        handleEvent(BANKACCOUNT_MERCHANT_REQ_RK);
        handleEvent(PAYMENTS_REGISTER_RES_RK);
    }

    private void handleEvent(String topic) {
        mq.addHandler(topic, event -> publishedEvents.put(topic, event));
    }

    @Given("a customer with id {string}")
    public void a_customer_with_id(String customerId) {
        this.customerId = customerId;
        mq.addHandler(TOKEN_CUSTOMERID_REQ_RK, event -> {
            String correlationId = event.getArgument(1, String.class);
            mq.publish(new Event(TOKEN_CUSTOMERID_RES_RK, new Object[] { customerId, correlationId }));
        });
    }

    @Given("a merchant with id {string}")
    public void a_merchant_with_id(String merchantId) {
        this.merchantId = merchantId;
    }

    @Given("a transaction with token {string} and amount {string} kr")
    public void a_transaction_with_token_and_amount_kr(String tokenId, String amount) {
        transaction = new Transaction(tokenId, merchantId, new BigDecimal(amount), null);
    }

    @Given("a customer bank account with id {string}")
    public void a_customer_bank_account_with_id(String customerBankId) {
        mq.addHandler(BANKACCOUNT_CUSTOMER_REQ_RK, event -> {
            String correlationId = event.getArgument(1, String.class);
            mq.publish(new Event(BANKACCOUNT_CUSTOMER_RES_RK, new Object[] { customerBankId, correlationId }));
        });
    }

    @Given("a merchant bank account with id {string}")
    public void a_merchant_bank_account_with_id(String merchantBankId) {
        mq.addHandler(BANKACCOUNT_MERCHANT_REQ_RK, event -> {
            String correlationId = event.getArgument(1, String.class);
            mq.publish(new Event(BANKACCOUNT_MERCHANT_RES_RK, new Object[] { merchantBankId, correlationId }));
        });
    }

    @When("the payment is registered by the payment service")
    public void the_payment_is_registered_by_the_payment_service() throws Exception {
        correlationId = UUID.randomUUID().toString();
        mq.publish(new Event(PAYMENTS_REGISTER_REQ_RK, new Object[] { transaction, correlationId }));
    }

    @Then("the token service is asked for the customer id")
    public void the_token_service_is_asked_for_the_customer_id() {
        Event event = publishedEvents.get(TOKEN_CUSTOMERID_REQ_RK);

        String actualToken = event.getArgument(0, String.class);
        assertEquals(transaction.tokenId(), actualToken);
    }

    @Then("the account service is asked for the customer bank account")
    public void the_account_service_is_asked_for_the_customer_bank_account() {
        Event event = publishedEvents.get(BANKACCOUNT_CUSTOMER_REQ_RK);

        assertEquals(customerId, event.getArgument(0, String.class));
    }

    @Then("the account service is asked for the merchant bank account")
    public void the_account_service_is_asked_for_the_merchant_bank_account() {
        Event event = publishedEvents.get(BANKACCOUNT_MERCHANT_REQ_RK);

        assertEquals(merchantId, event.getArgument(0, String.class));
    }

    @Then("the reporting service receives the transaction")
    public void the_reporting_service_receives_the_transaction() {
        Event event = publishedEvents.get(PAYMENTS_REGISTER_RES_RK);

        Transaction result = event.getArgument(0, Transaction.class);

        assertEquals(transaction.tokenId(), result.tokenId());
        assertEquals(transaction.merchantId(), result.merchantId());
        assertEquals(transaction.amount(), result.amount());
        assertEquals(customerId, result.customerId());
    }
}