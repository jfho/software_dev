package dtu.Steps;

import dtu.*;

import java.math.BigDecimal;
import java.util.*;

import dtu.Models.BankAccount;
import dtu.Models.Customer;
import dtu.Models.Merchant;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationSteps {

    private final State state;
    private final CustomerClient customerClient = new CustomerClient();
    private final MerchantClient merchantClient = new MerchantClient();
    private BankClient bank = new BankClient();

    private String customerUuid = null;
    private String merchantUuid = null;

    private Customer customer = null;
    private Merchant merchant = null;


    public RegistrationSteps(State state) {
        this.state = state;
    }

    @Given("a customer bank account with first name {string}, last name {string}, CPR {string}, and balance {string}")
    public void a_customer_bank_account_with_first_name_last_name_cpr_and_balance(String firstName, String lastName,
            String cpr, String balance) {
        BankAccount account = new BankAccount(firstName, lastName, cpr, new BigDecimal(balance));
        customerUuid = bank.register(account);
    }

    @Given("a merchant bank account with first name {string}, last name {string}, CPR {string}, and balance {string}")
    public void a_merchant_bank_account_with_first_name_last_name_cpr_and_balance(String firstName, String lastName,
            String cpr, String balance) {
        BankAccount account = new BankAccount(firstName, lastName, cpr, new BigDecimal(balance));
        merchantUuid = bank.register(account);
    }


    @When("the customer registers for DTUPay with first name {string}, last name {string}, CPR {string}")
    public void customer_registers(String fn, String ln, String cpr) {
        customer = new Customer(fn, ln, cpr, customerUuid, null);
        state.customer = customerClient.register(customer);
        assertNotNull(state.customer.dtupayUuid());
    }

    @When("the merchant registers for DTUPay with first name {string}, last name {string}, CPR {string}")
    public void merchant_registers(String fn, String ln, String cpr) {
        merchant = new Merchant(fn, ln, cpr, merchantUuid, null);
        state.merchant = merchantClient.register(merchant);
        assertNotNull(state.merchant.dtupayUuid());
    }

    @Then("the customer registration is successful")
    public void customer_registration_successful() {
        assertNotNull(state.customer);
    }

    @Then("the merchant registration is successful")
    public void merchant_registration_successful() {
        assertNotNull(state.merchant);
    }

    @When("the customer unregisters for DTUPay")
    public void customer_unregisters() {
        customerClient.unregister(state.customer);
    }

    @When("the merchant unregisters for DTUPay")
    public void merchant_unregisters() {
        merchantClient.unregister(state.merchant);
    }

    @Then("the customer unregistration is successful")
    public void customer_unregistration_successful() {
        Customer retrievedCustomer = customerClient.getCustomer(customer.dtupayUuid());
        assertEquals(state.customer, retrievedCustomer);
    }

    @Then("the merchant unregistration is successful")
    public void merchant_unregistration_successful() {
        Merchant retrievedMerchant = merchantClient.getMerchant(merchant.dtupayUuid());
        assertEquals(state.merchant, retrievedMerchant);
    }

    @Before
    public void setup() {
        customer = null;
        merchant = null;
        customerUuid = null;
        merchantUuid = null;
    }

    @After
    public void cleanup() {
        if (customerUuid != null) {
            bank.unregister(customerUuid);
        }

        if (merchantUuid != null) {
            bank.unregister(merchantUuid);
        }
    }
}
