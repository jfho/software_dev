package dtu.example;

import java.util.ArrayList;
import java.util.List;

import org.dtu.models.Customer;
import org.dtu.models.Merchant;
import org.dtu.models.Transaction;
import org.dtu.models.TransactionResult;

import static org.junit.Assert.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SimpleDTUPaySteps {
    private Customer customer;
    private Merchant merchant;
    private String customerId, merchantId;
    private SimpleDtuPay dtupay = new SimpleDtuPay();
    private boolean successful = false;
    private String errorMsg = "";
    private List<Transaction> transactions = new ArrayList<>();

    @Given("a customer with name {string}")
    public void aCustomerWithName(String name) {
        customer = new Customer(name);
    }

    @Given("the customer is registered with Simple DTU Pay")
    public void theCustomerIsRegisteredWithSimpleDTUPay() {
        customerId = dtupay.register(customer);
    }

    @Given("a merchant with name {string}")
    public void aMerchantWithName(String name) {
        merchant = new Merchant(name);
    }

    @Given("the merchant is registered with Simple DTU Pay")
    public void theMerchantIsRegisteredWithSimpleDTUPay() {
        merchantId = dtupay.register(merchant);

    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(Integer amount) {
        successful = dtupay.pay(amount, customerId, merchantId).getSuccessful();
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(successful);
    }

    @Given("a customer with name {string}, who is registered with Simple DTU Pay")
    public void a_customer_with_name_who_is_registered_with_simple_dtu_pay(String name) {
        customer = new Customer(name);
        customerId = dtupay.register(customer);
    }

    @Given("a successful payment of {string} kr from the customer to the merchant")
    public void a_successful_payment_of_kr_from_the_customer_to_the_merchant(String amount) {
        successful = dtupay.pay(Integer.parseInt(amount), customerId, merchantId).getSuccessful();
    }

    @When("the manager asks for a list of payments")
    public void the_manager_asks_for_a_list_of_payments() {
        transactions = dtupay.getPayments();
    }

    @Then("the list contains a payments where customer {string} paid {string} kr to merchant {string}")
    public void the_list_contains_a_payments_where_customer_paid_kr_to_merchant(String customerId, String amount, String merchantId) {
        boolean containsPayment = false;
        for (Transaction transaction : transactions) {
            if (transaction.getMerchantId().equals(merchantId) 
                && transaction.getCustomerId().equals(customerId) 
                && transaction.getAmount() == Integer.parseInt(amount)) {
                containsPayment = true;
            }
        }
        assertTrue(containsPayment);
    }

    @Given("a merchant with name {string}, who is registered with Simple DTU Pay")
    public void a_merchant_with_name_who_is_registered_with_simple_dtu_pay(String string) {
        merchant = new Merchant(string);
        merchantId = dtupay.register(merchant);
    }

    @When("the merchant initiates a payment for {string} kr using customer id {string}")
    public void the_merchant_initiates_a_payment_for_kr_using_customer_id(String amount, String name) {
        TransactionResult res = dtupay.pay(Integer.parseInt(amount), name, merchant.getCpr());
        successful = res.getSuccessful();
        errorMsg = res.getError();

    }

    @Then("the payment is not successful")
    public void the_payment_is_not_successful() {
        assertTrue( !successful);    
    }

    @Then("an error message is returned saying {string}")
    public void an_error_message_is_returned_saying(String string) {
        assertTrue(errorMsg.equals(string));
    }



    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void a_customer_with_name_last_name_and_cpr(String name, String lastName, String cpr) {
        Customer customer = new Customer(name, lastName, cpr);
        customerId = dtupay.register(customer);
    }
    
    @Given("the customer {int} is registered with the bank with an initial balance of {int} kr")
    public void the_customer_is_registered_with_the_bank_with_an_initial_balance_of_kr(int customerId, int balance) {
        Customer customer = dtupay.findCustomerById(customerId);
        dtupay.registerBankAccount(customer, balance);
    }
    
    @Given("the customer {int} is registered with Simple DTU Pay using their bank account")
    public void the_customer_is_registered_with_simple_dtu_pay_using_their_bank_account(int customerId) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Given("a merchant with name {string}, last name {string}, and CPR {string}")
    public void a_merchant_with_name_last_name_and_cpr(String string, String string2, String string3) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Given("the merchant {int} is registered with the bank with an initial balance of {int} kr")
    public void the_merchant_is_registered_with_the_bank_with_an_initial_balance_of_kr(int merchantId, int balance) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Given("the merchant {int} is registered with Simple DTU Pay using their bank account")
    public void the_merchant_is_registered_with_simple_dtu_pay_using_their_bank_account(int merchantId) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the balance of the customer at the bank is {int} kr")
    public void the_balance_of_the_customer_at_the_bank_is_kr(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the balance of the merchant at the bank is {int} kr")
    public void the_balance_of_the_merchant_at_the_bank_is_kr(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}