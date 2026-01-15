package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    private PaymentService paymentController = new PaymentService(mq, new MockBankClient());

    private Map<String, String> publishedMessages = new HashMap<>();

    private String token;
    private String merchantId;
    private BigDecimal amount;
    private Transaction transaction;

    public PaymentServiceSteps() {
        handleRequest("payments.customerid.request");
        handleRequest("payments.customerbankaccount.request");
        handleRequest("payments.merchantbankaccount.request");
        handleRequest("payments.transaction.report");
        handleRequest("payments.transaction.status");
    }

    private void handleRequest(String topic) {
        mq.addHandler(topic, event -> {
            String payload = event.getArgument(0, String.class);
            publishedMessages.put(topic, payload);
        });
    }

    @Given("a transaction with token {string}, merchant id {string} and amount {string} kr")
    public void a_transaction_with_token_merchant_id_and_amount_kr(String token, String merchantId, String amount) {
        this.merchantId = merchantId;
        this.token = token;
        this.amount = new BigDecimal(amount);
        transaction = new Transaction(this.token, this.merchantId, this.amount);
    }

    @Given("the customer has id {string}")
    public void the_customer_has_id(String customerId) {
        mq.addHandler("payments.customerid.request", event -> {
            String correlationId = event.getArgument(1, String.class);
            mq.publish(new Event("token.customerid.response", new Object[] { customerId, correlationId }));
        });
    }

    @Given("customer bank account with id {string}")
    public void customer_bank_account_with_id(String customerBankAccount) {
        mq.addHandler("payments.customerbankaccount.request", event -> {
            String correlationId = event.getArgument(1, String.class);
            mq.publish(new Event("account.customerbankaccount.response",
                    new Object[] { customerBankAccount, correlationId }));
        });
    }

    @Given("merchant bank account with id {string}")
    public void merchant_bank_account_with_id(String merchantBankAccount) {
        mq.addHandler("payments.merchantbankaccount.request", event -> {
            String correlationId = event.getArgument(1, String.class);
            mq.publish(new Event("account.merchantbankaccount.response",
                    new Object[] { merchantBankAccount, correlationId }));
        });
    }

    @When("we register the transaction")
    public void we_register_the_transaction() throws Exception {
        paymentController.registerTransaction(transaction);
    }

    @Then("the {string} queue has one element with string {string}")
    public void the_queue_has_element_with_string(String routingKey, String expectedMessage) {
        String actualMessage = publishedMessages.get(routingKey);

        assertNotNull("No message was published to " + routingKey, actualMessage);
        assertEquals(expectedMessage, actualMessage);
    }
}