package dtu;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import java.util.UUID;

import dtu.messagingUtils.Event;
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
        transaction = new Transaction(tokenId, merchantId, new BigDecimal(amount), null);
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
        mq.publish(new Event(BANKACCOUNT_CUSTOMER_RES_RK, new Object[] {
                customerBankId, correlationId }));
    }

    @When("a merchant bank account with id {string} is received")
    public void a_merchant_bank_account_with_id_is_received(String merchantBankId) {
        mq.publish(new Event(BANKACCOUNT_MERCHANT_RES_RK, new Object[] {
                merchantBankId, correlationId }));
    }

    @Then("the payment is successful")
    public void the_payment_is_successful() {
        assertTrue(successfulTransfer);
    }
}