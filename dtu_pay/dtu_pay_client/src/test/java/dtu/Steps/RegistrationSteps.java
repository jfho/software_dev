package dtu.Steps;

import java.math.BigDecimal;
import java.util.*;

import dtu.State;
import dtu.BankClient;
import dtu.MerchantClient;
import dtu.CustomerClient;
import dtu.Models.BankAccount;
import dtu.Models.Customer;
import dtu.Models.Merchant;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import jakarta.ws.rs.NotFoundException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

public class RegistrationSteps {

    private final State state;
    private final CustomerClient customerClient = new CustomerClient();
    private final MerchantClient merchantClient = new MerchantClient();
    private BankClient bank = new BankClient();

    private String customerUuid = null;
    private String merchantUuid = null;

    public RegistrationSteps(State state) {
        this.state = state;
    }

    @Before("@registration")
    public void setup() {
        customerUuid = null;
        merchantUuid = null;

        state.tokens = null;
        state.transactions = null;
        state.lastException = null;
        state.customer = null;
        state.merchant = null;
    }

    @After("@registration")
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

    @Given("a customer bank account with first name {string}, last name {string}, CPR {string}, and balance {string}")
    public void customerBankAccount(String firstName, String lastName, String cpr, String balance) {
        BankAccount account = new BankAccount(firstName, lastName, cpr, new BigDecimal(balance));
        customerUuid = bank.register(account);
    }

    @Given("a merchant bank account with first name {string}, last name {string}, CPR {string}, and balance {string}")
    public void merchantBankAccount(String firstName, String lastName, String cpr, String balance) {
        BankAccount account = new BankAccount(firstName, lastName, cpr, new BigDecimal(balance));
        merchantUuid = bank.register(account);
    }

    @When("the customer registers for DTUPay with first name {string}, last name {string}, CPR {string}")
    public void customerRegisters(String fn, String ln, String cpr) {
        Customer customer = new Customer(fn, ln, cpr, customerUuid, null);

        try {
            state.customer = customerClient.register(customer);
        } catch (Exception e) {
            System.out.println("OK we hit an exeception");
            System.out.println(e.getMessage());
            state.lastException = e;
        }
    }

    @When("the merchant registers for DTUPay with first name {string}, last name {string}, CPR {string}")
    public void merchantRegisters(String fn, String ln, String cpr) {
        Merchant merchant = new Merchant(fn, ln, cpr, merchantUuid, null);
        try {
            state.merchant = merchantClient.register(merchant);
        } catch (Exception e) {
            state.lastException = e;
        }
    }

    @When("the customer unregisters for DTUPay")
    public void customerUnregisters() {
        customerClient.unregister(state.customer);
    }

    @When("the merchant unregisters for DTUPay")
    public void merchantUnregisters() {
        merchantClient.unregister(state.merchant);
    }

    @Then("the customer registration is successful")
    public void customerRegistrationSuccess() {
        assertNotNull(state.customer);
        assertNotNull(state.customer.dtupayUuid());
        Customer retrievedCustomer = customerClient.getCustomer(state.customer.dtupayUuid());
        assertEquals(state.customer, retrievedCustomer);
    }

    @Then("the merchant registration is successful")
    public void merchantRegistrationSuccess() {
        assertNotNull(state.merchant);
        assertNotNull(state.merchant.dtupayUuid());
        Merchant retrievedMerchant = merchantClient.getMerchant(state.merchant.dtupayUuid());
        assertEquals(state.merchant, retrievedMerchant);
    }

    @Then("the customer unregistration is successful")
    public void customerUnregistrationSuccess() {
        Customer retrievedCustomer = customerClient.getCustomer(state.customer.dtupayUuid());
        assertNull(retrievedCustomer);
    }

    @Then("the merchant unregistration is successful")
    public void merchantUnregistrationSuccess() {
        Merchant retrievedMerchant = merchantClient.getMerchant(state.merchant.dtupayUuid());
        assertNull(retrievedMerchant);
    }

    @Then("the customer registration is not successful")
    public void customerRegistrationFailed() {
        assertNotNull(state.lastException);
    }

    @Then("the merchant registration is not successful")
    public void merchantRegistrationFailed() {
        assertNotNull(state.lastException);
    }

    @Then("an error message is returned saying {string}")
    public void errorMessageReturned(String msg) {
        assertNotNull(state.lastException);
        assertEquals(msg, state.lastException.getMessage());
    }
}