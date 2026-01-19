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

    @When("the customer performs a payment for {string} kr to the merchant")
    public void customer_pays(String amount) {
        String token = state.tokens.get(0);
        merchantClient.pay(token, state.merchant.dtupayUuid(), new BigDecimal(amount));
    }

    @Then("the balance of the customer at the bank is {string} kr")
    public void customer_balance(String expected) {
        Account customerAccount = bank.getAccount(state.customer.bankAccountUuid());
        assertEquals(new BigDecimal(expected), customerAccount.getBalance());
    }

    @Then("the balance of the merchant at the bank is {string} kr")
    public void merchant_balance(String expected) {
        Account merchantAccount = bank.getAccount(state.merchant.bankAccountUuid());
        assertEquals(new BigDecimal(expected), merchantAccount.getBalance());
    }

    @Then("the payment is not successful")
    public void payment_failed() {
        assertNotNull(state.lastException);
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
