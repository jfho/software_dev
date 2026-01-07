package dtu.example;

import static org.junit.Assert.assertTrue;

import java.util.*;
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
        successful = dtupay.pay(amount, customerId, merchantId).successful();
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
        successful = dtupay.pay(Integer.parseInt(amount), customerId, merchantId).successful();
    }

    @When("the manager asks for a list of payments")
    public void the_manager_asks_for_a_list_of_payments() {
        transactions = dtupay.getPayments();
    }

    @Then("the list contains a payments where customer {string} paid {string} kr to merchant {string}")
    public void the_list_contains_a_payments_where_customer_paid_kr_to_merchant(String customerId, String amount, String merchantId) {
        boolean containsPayment = false;
        for (Transaction transaction : transactions) {
            if (transaction.merchantId().equals(merchantId) 
                && transaction.customerId().equals(customerId) 
                && transaction.payment() == Integer.parseInt(amount)) {
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
        TransactionResult res = dtupay.pay(Integer.parseInt(amount), name, merchant.merchantId());
        successful = res.successful();
        errorMsg = res.error();

    }

    @Then("the payment is not successful")
    public void the_payment_is_not_successful() {
        assertTrue( !successful);    
    }

    @Then("an error message is returned saying {string}")
    public void an_error_message_is_returned_saying(String string) {
        assertTrue(errorMsg.equals(string));
    }

}