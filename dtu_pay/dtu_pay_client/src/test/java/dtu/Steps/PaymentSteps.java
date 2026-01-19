package dtu.Steps;

import dtu.*;
import dtu.Models.Transaction;
import dtu.ws.fastmoney.Account;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentSteps {

    private final State state;
    private BankClient bank = new BankClient();
    private final MerchantClient merchantClient = new MerchantClient();

    public PaymentSteps(State state) {
        this.state = state;
    }

    /*@When("the customer performs a payment for {string} kr to the merchant")
    public void customer_pays(String amount) {
        try{
            merchantClient.pay(
                state.tokens.get(0),
                state.merchant.dtupayUuid(),
                new BigDecimal(amount)
            );
        } catch (Exception e) {
            state.lastException = e;
        }
    }
    */
    @Then("the balance of the customer at the bank is {string} kr")
    public void customer_balance(String expected) {
        try{
            Account customerAccount = bank.getAccount(state.customer.bankAccountUuid());
            assertEquals(new BigDecimal(expected), customerAccount.getBalance());
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @Then("the balance of the merchant at the bank is {string} kr")
    public void merchant_balance(String expected) {
        try{
            Account merchantAccount = bank.getAccount(state.merchant.bankAccountUuid());
            assertEquals(new BigDecimal(expected), merchantAccount.getBalance());
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @Then("the payment is not successful")
    public void payment_failed() {
        assertNotNull(state.lastException);
    }

    @When("a payment is initiated for {string} kr using merchant id {string}")
    public void payment_with_unknown_merchant(String amount, String merchantId) {
        try {
            merchantClient.pay(
                state.tokens.get(0),
                merchantId,
                new BigDecimal(amount)
            );
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    

    @Then("an error message is returned saying {string}")
    public void error_message_returned(String msg) {
        assertNotNull(state.lastException);
        assertTrue(state.lastException.getMessage().contains(msg));
    }

    @Given("the merchant has a token from the customer")
    public void the_merchant_has_a_token_from_the_customer() {
        try {
            state.tokens = new CustomerClient().getTokens(state.customer.dtupayUuid(), 6);
        } catch (Exception e) {
            state.lastException = e;
        }
    }
    
    //@Given("the merchant initiates a transaction for {string} kr")
    @When("the merchant initiates a transaction for {string} kr")
    public void the_merchant_initiates_a_transaction_for_kr(String string) {
        try {
            if (state.tokens == null || state.tokens.isEmpty()) {
                state.tokens = new CustomerClient().getTokens(state.customer.dtupayUuid(), 6);
            }
            merchantClient.pay(
                state.tokens.get(0),
                state.merchant.dtupayUuid(),
                new BigDecimal(string)
            );
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @When("the merchant initiates a transaction for {string} kr using token id {string}")
    public void the_merchant_initiates_a_transaction_for_kr_using_token_id(String string, String string2) {
        try {
            
            merchantClient.pay(
                string2,
                state.merchant.dtupayUuid(),
                new BigDecimal(string)
            );
        } catch (Exception e) {
            state.lastException = e;
        }
    }


    @Before
    public void setup() {
        state.tokens = null;
        state.transactions = null;
        state.lastException = null;
    }

    @After
    public void cleanup() {
        if (state.customer != null) {
            bank.unregister(state.customer.bankAccountUuid());
        }
        if (state.merchant != null) {
            merchantClient.unregister(state.merchant);
            bank.unregister(state.merchant.bankAccountUuid());
        }
    }
}
