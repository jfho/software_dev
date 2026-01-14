package dtu;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import dtu.Adapters.Event;
import dtu.Adapters.MessageQueue;
import dtu.Models.Transaction;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PaymentServiceSteps {
    MessageQueue mq = new MockQueue();
    PaymentService paymentController = new PaymentService(mq, new MockBankClient());
    private String token;
    private String merchantId;
    private String customerId;
    private String merchantBankAccount;
    private String customerBankAccount;
    private BigDecimal amount;
    private Transaction transaction;

    @Given("a transaction with token {string}, merchant id {string} and amount {string} kr")
    public void a_transaction_with_token_merchant_id_and_amount_kr(String token, String merchantId, String amount) {
        this.merchantId = merchantId;
        this.token = token;
        this.amount = new BigDecimal(amount);
        transaction = new Transaction(this.token, this.merchantId, this.amount);
    }

    @Given("the customer has id {string}")
    public void the_customer_has_id(String customerId) {
        Event event = new Event("token.customerid.response", new Object[] { customerId });
        mq.publish(event);
    }

    @Given("customer bank account with id {string}")
    public void customer_bank_account_with_id(String customerBankAccount) {
        Event event = new Event("account.customerbankaccount.response", new Object[] { customerBankAccount });
        mq.publish(event);
    }

    @Given("merchant bank account with id {string}")
    public void merchant_bank_account_with_id(String merchantBankAccount) {
        Event event = new Event("account.merchantbankaccount.response", new Object[] { merchantBankAccount });
        mq.publish(event);
    }

    @When("we register the transaction")
    public void we_register_the_transaction() throws Exception {
        paymentController.registerTransaction(transaction);
    }

    @Then("the {string} queue has one element with string {string}")
    public void the_queue_has_element_with_string(String routingKey, String expectedMessage) throws Exception {
        CompletableFuture<String> actualMessageFuture = new CompletableFuture<>();
        mq.addHandler(routingKey, event -> {
            String response = event.getArgument(0, String.class);
            actualMessageFuture.complete(response);
        });
        String actualMessage = actualMessageFuture.join();
        assertTrue(actualMessage.equals(expectedMessage));
    }
}