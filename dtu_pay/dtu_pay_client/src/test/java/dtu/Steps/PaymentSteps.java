package dtu.Steps;

import dtu.*;
import dtu.Models.Transaction;
import dtu.ws.fastmoney.Account;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentSteps {

    private final State state;
    private BankClient bank = new BankClient();
    private final CustomerClient customerClient = new CustomerClient();
    private final MerchantClient merchantClient = new MerchantClient();

    boolean paymentSuccessful = false;
    
    public PaymentSteps(State state) {
        this.state = state;
    }

    @Before("@payment")
    public void setup() {
        paymentSuccessful = false;
        
        state.tokens = null;
        state.transactions = null;
        state.lastException = null;
    }

    @After("@payment")
    public void cleanup() {
        if (state.customer != null) {
            customerClient.unregister(state.customer);
            bank.unregister(state.customer.bankAccountUuid());
        }
        if (state.merchant != null) {
            merchantClient.unregister(state.merchant);
            bank.unregister(state.merchant.bankAccountUuid());
        }
    }    

    @Given("the merchant has a token from the customer")
    public void merchantHasAToken() {
        state.tokens = customerClient.getTokens(state.customer.dtupayUuid(), 1);
    }

    @When("the merchant initiates a transaction for {string} kr")
    public void merchantInitiatesTransaction(String amount) {
        Response response = merchantClient.pay(
            state.tokens.get(0),
            state.merchant.dtupayUuid(),
            amount
        );

        if (response.getStatus() >= 400) {
            paymentSuccessful = false;
            state.errorMessage = response.readEntity(String.class);
        } else {
            paymentSuccessful = true;
        }
    }

    @When("the merchant initiates a transaction for {string} kr using token id {string}")
    public void merchantInitiatesTransactionWithToken(String amount, String tokenId) {
        Response response = merchantClient.pay(
            tokenId,
            state.merchant.dtupayUuid(),
            amount
        );

        if (response.getStatus() >= 400) {
            paymentSuccessful = false;
            state.errorMessage = response.readEntity(String.class);
        } else {
            paymentSuccessful = true;
        }
    }

    @When("a payment is initiated for {string} kr using merchant id {string}")
    public void paymentWithUnknownMerchant(String amount, String merchantId) {
        Response response = merchantClient.pay(
            state.tokens.get(0),
            merchantId,
            amount
        );

        if (response.getStatus() >= 400) {
            paymentSuccessful = false;
            state.errorMessage = response.readEntity(String.class);
        } else {
            paymentSuccessful = true;
        }
    }

    @Then("the balance of the customer at the bank is {string} kr")
    public void customerBalance(String expected) {
        Account customerAccount = bank.getAccount(state.customer.bankAccountUuid());
        assertEquals(new BigDecimal(expected), customerAccount.getBalance());
    }

    @Then("the balance of the merchant at the bank is {string} kr")
    public void merchantBalance(String expected) {
        Account merchantAccount = bank.getAccount(state.merchant.bankAccountUuid());
        assertEquals(new BigDecimal(expected), merchantAccount.getBalance());
    }

    @Then("the payment is successful")
    public void paymentSuccess() {
        assertTrue(paymentSuccessful);
    }

    @Then("the payment is not successful")
    public void paymentFailed() {
        assertFalse(paymentSuccessful);
    }
}
