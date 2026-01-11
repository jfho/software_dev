package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.util.*;
import dtu.Models.BankAccount;
import dtu.Models.Customer;
import dtu.Models.Merchant;
import dtu.Models.Transaction;
import dtu.ws.fastmoney.Account;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

public class SimpleDTUPaySteps {
    private DtuPayClient dtupay = new DtuPayClient();
    private BankClient bank = new BankClient();
    private List<Transaction> transactionsList = new ArrayList<>();
    private String customerUuid = null;
    private String merchantUuid = null;

    private Customer customer = null;
    private Merchant merchant = null;

    Response response;

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
    public void the_customer_registers_for_dtu_pay_with_first_name_last_name_cpr(String firstName, String lastName,
            String cpr) {
        customer = new Customer(firstName, lastName, cpr, customerUuid, null);
        customer = dtupay.register(customer);
    }

    @When("the merchant registers for DTUPay with first name {string}, last name {string}, CPR {string}")
    public void the_merchant_registers_for_dtu_pay_with_first_name_last_name_cpr(String firstName, String lastName,
            String cpr) {
        merchant = new Merchant(firstName, lastName, cpr, merchantUuid, null);
        merchant = dtupay.register(merchant);
    }

    @When("the customer unregisters for DTUPay")
    public void the_customer_unregisters_for_dtu_pay() {
        dtupay.unregister(customer);
    }

    @When("the merchant unregisters for DTUPay")
    public void the_merchant_unregisters_for_dtu_pay() {
        dtupay.unregister(merchant);
    }

    @When("the customer performs a payment for {string} kr to the merchant")
    public void the_customer_performs_a_payment_for_kr_to_the_merchant(String amount) {
        dtupay.pay(customer.dtupayUuid(), merchant.dtupayUuid(), new BigDecimal(amount));
    }

    @Then("the customer registration is successful")
    public void the_customer_registration_is_successful() {
        Customer retrievedCustomer = dtupay.getCustomer(customer.dtupayUuid());

        assertEquals(customer, retrievedCustomer);
    }

    @Then("the merchant registration is successful")
    public void the_merchant_registration_is_successful() {
        Merchant retrievedMerchant = dtupay.getMerchant(merchant.dtupayUuid());

        assertEquals(merchant, retrievedMerchant);
    }

    @Then("the customer unregistration is successful")
    public void the_customer_unregistration_is_successful() {
        assertThrows(NotFoundException.class, () -> {
            dtupay.getCustomer(customer.dtupayUuid());
        });
    }

    @Then("the merchant unregistration is successful")
    public void the_merchant_unregistration_is_successful() {
        assertThrows(NotFoundException.class, () -> {
            dtupay.getMerchant(merchant.dtupayUuid());
        });
    }

    @Then("the balance of the customer at the bank is {string} kr")
    public void the_balance_of_the_customer_at_the_bank_is_kr(String newBalance) {
        Account customerAccount = bank.getAccount(customer.bankAccountUuid());
        assertEquals(new BigDecimal(newBalance), customerAccount.getBalance());
    }

    @Then("the balance of the merchant at the bank is {string} kr")
    public void the_balance_of_the_merchant_at_the_bank_is_kr(String newBalance) {
        Account merchantAccount = bank.getAccount(merchant.bankAccountUuid());
        assertEquals(new BigDecimal(newBalance), merchantAccount.getBalance());
    }

    @When("the manager asks for a list of payments")
    public void the_manager_asks_for_a_list_of_payments() {
        transactionsList = dtupay.getPayments();
    }

    @Then("the list contains payments where the customer paid {string} kr to the merchant")
    public void the_list_contains_payments_where_the_customer_paid_kr_to_the_merchant(String amount) {
        boolean containsPayment = false;
        for (Transaction transaction : transactionsList) {
            if (transaction.payment().equals(new BigDecimal(amount))
                    && transaction.customerId().equals(customer.dtupayUuid())
                    && transaction.merchantId().equals(merchant.dtupayUuid())) {
                containsPayment = true;
                break;
            }
        }
        assertTrue(containsPayment);
    }

    @When("the merchant initiates a payment for {string} kr using customer id {string}")
    public void the_merchant_initiates_a_payment_for_kr_using_customer_id(String amount, String customerId) {
        response = dtupay.pay(customerId, merchant.dtupayUuid(), new BigDecimal(amount));
    }

    @When("the customer initiates a payment for {string} kr using merchant id {string}")
    public void the_customer_initiates_a_payment_for_kr_using_merchant_id(String amount, String merchantId) {
        response = dtupay.pay(customer.dtupayUuid(), merchantId, new BigDecimal(amount));
    }

    @Then("the payment is not successful")
    public void the_payment_is_not_successful() {
        assertTrue(response.getStatus() >= 400 && response.getStatus() <= 499);
    }

    @Then("an error message is returned saying {string}")
    public void an_error_message_is_returned_saying(String message) {
        String error = response.readEntity(String.class);
        assertTrue(error.equals(message));
    }
}