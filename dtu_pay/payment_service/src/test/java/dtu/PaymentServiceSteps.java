package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import dtu.messagingUtils.Event;
import dtu.models.PendingTransaction;
import dtu.models.Transaction;
import dtu.services.PaymentService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PaymentServiceSteps {
    private MockQueue mq = new MockQueue();
    private PaymentService paymentService = new PaymentService(mq, new MockBankClient());

    private Transaction transaction;
    private boolean successfulTransfer = false;
    private String correlationId;

    private final String PAYMENTS_REGISTER_REQ_RK = "PaymentRequested";
    private final String PAYMENTS_REGISTER_RES_RK = "MoneyTransferFinished";
    private final String BANKACCOUNT_CUSTOMER_RES_RK = "CustomerBankAccountRetrieved";
    private final String BANKACCOUNT_MERCHANT_RES_RK = "MerchantBankAccountRetrieved";

    @Given("a transaction with token {string}, amount {string} kr and merchant id {string}")
    public void a_transaction_with_token_amount_kr_and_merchant_id(String tokenId, String amount, String merchantId) {
        BigDecimal transactionAmount = amount.isEmpty() ? null : new BigDecimal(amount);
        transaction = new Transaction(tokenId, merchantId, transactionAmount, null);
    }

    @When("the payment is received by the payment service")
    public void the_payment_is_received_by_the_payment_service() {
        mq.addHandler(PAYMENTS_REGISTER_RES_RK, e -> {
            successfulTransfer = e.getArgument(0, boolean.class);
        });
        correlationId = UUID.randomUUID().toString();
        mq.publish(new Event(PAYMENTS_REGISTER_REQ_RK, new Object[] { transaction, correlationId }));
    }

    @When("a customer bank account with id {string} is received")
    public void a_customer_bank_account_with_id_is_received(String customerBankId) {
        customerBankId = (customerBankId.equals("null")) ? null : customerBankId;
        mq.publish(new Event(BANKACCOUNT_CUSTOMER_RES_RK, new Object[] {
                customerBankId, correlationId }));
    }

    @When("a merchant bank account with id {string} is received")
    public void a_merchant_bank_account_with_id_is_received(String merchantBankId) {
        merchantBankId = (merchantBankId.equals("null")) ? null : merchantBankId;
        mq.publish(new Event(BANKACCOUNT_MERCHANT_RES_RK, new Object[] {
                merchantBankId, correlationId }));
    }

    @Then("the payment is successful")
    public void the_payment_is_successful() {
        assertTrue(successfulTransfer);
    }

    @Then("the payment is unsuccessful")
    public void the_payment_is_unsuccessful() {
        assertFalse(successfulTransfer);
    }

    @Then("the transaction is removed from the pending list")
    public void the_transaction_is_removed_from_the_pending_list() {
        Map<String, PendingTransaction> transactions = paymentService.getPendingTransactions();
        assertEquals(null, transactions.get(correlationId));
    }
}