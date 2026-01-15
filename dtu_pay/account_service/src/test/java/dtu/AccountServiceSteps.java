package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.NotFoundException;
import dtu.Controllers.CustomerController;
import dtu.Controllers.MerchantsController;
import dtu.MessagingUtils.Event;
import dtu.MessagingUtils.MessageQueue;
import dtu.Models.Customer;
import dtu.Models.Database;
import dtu.Models.Merchant;

public class AccountServiceSteps {
    Database db = Database.getInstance();
    
    MessageQueue mq;
    CustomerController customerController;
    MerchantsController merchantController;

    Customer customer = null;
    Merchant merchant = null;

    boolean receivedDeletedCustomerEvent = false;
    boolean receivedDeletedMerchantEvent = false;
    boolean notFoundExceptionThrown = false;

    Event customerBankAccountResponse = null;
    Event merchantBankAccountResponse = null;

    private String DELETE_CUSTOMER_RK = "accounts.customer.deleted";
    private String DELETE_MERCHANT_RK = "accounts.merchant.deleted";
    private String BANKACCOUNT_CUSTOMER_REQ_RK = "payments.customerbankaccount.request";
    private String BANKACCOUNT_CUSTOMER_RES_RK = "accounts.customerbankaccount.response";
    private String BANKACCOUNT_MERCHANT_REQ_RK = "payments.merchantbankaccount.request";
    private String BANKACCOUNT_MERCHANT_RES_RK = "accounts.merchantbankaccount.response";

    @Before
    public void setup() {
        customer = null;
        merchant = null;

        receivedDeletedCustomerEvent = false;
        receivedDeletedMerchantEvent = false;
        notFoundExceptionThrown = false;

        customerBankAccountResponse = null;
        merchantBankAccountResponse = null;

        mq = new MockQueue();
        customerController = new CustomerController(mq);
        merchantController = new MerchantsController(mq);

        db.clean();
    }

    @Given("a customer account with first name {string}, last name {string}, CPR {string}, bank ID {string}")
    public void customerIsRegistered(String fn, String ln, String cpr, String bankId) {
        customer = customerController.registerCustomer(new Customer(fn, ln, cpr, bankId, null));
    }

    @Given("a merchant account with first name {string}, last name {string}, CPR {string}, bank ID {string}")
    public void merchantIsRegistered(String fn, String ln, String cpr, String bankId) {
        merchant = merchantController.registerMerchant(new Merchant(fn, ln, cpr, bankId, null));
    }

    @Given("the customer unregisters for DTUPay")
    public void customerUnregisters() {
        String customerId = customer.dtupayUuid();
        customerController.deleteCustomer(customerId);
    }    
    
    @When("the merchant unregisters for DTUPay")
    public void merchantUnregisters() {
        String merchantId = merchant.dtupayUuid();
        merchantController.deleteMerchant(merchantId);
    }

    @Given("a subscriber for the customer deletion event")
    public void customerDeletionMqSubscriber() {
        mq.addHandler(DELETE_CUSTOMER_RK, event -> {
            receivedDeletedCustomerEvent = true;
        });
    }

    @Given("a subscriber for the merchant deletion event")
    public void merchantDeletionMqSubscriber() {
        mq.addHandler(DELETE_MERCHANT_RK, event -> {
            receivedDeletedMerchantEvent = true;
        });
    }

    @Given("a subscriber for the customer bank account response event")
    public void customerBankAccountResponseMqSubscriber() {
        mq.addHandler(BANKACCOUNT_CUSTOMER_RES_RK, event -> {
            customerBankAccountResponse = event;
        });
    }

    @Given("a subscriber for the merchant bank account response event")
    public void merchantBankAccountResponseMqSubscriber() {
        mq.addHandler(BANKACCOUNT_MERCHANT_RES_RK, event -> {
            merchantBankAccountResponse = event;
        });
    }

    @When("a customer registers for DTUPay with first name {string}, last name {string}, CPR {string}, bank ID {string}")
    public void customerRegistration(String fn, String ln, String cpr, String bankId) {
        customer = customerController.registerCustomer(new Customer(fn, ln, cpr, bankId, null));
    }

    @When("a merchant registers for DTUPay with first name {string}, last name {string}, CPR {string}, bank ID {string}")
    public void merchantRegistration(String fn, String ln, String cpr, String bankId) {
        merchant = merchantController.registerMerchant(new Merchant(fn, ln, cpr, bankId, null));
    }

    @When("the customer with id {string} is retrieved")
    public void customerRetrieved(String customerId) {
        try {
            customerController.getCustomer(customerId);
        } catch (NotFoundException e) {
            notFoundExceptionThrown = true;
        }
    }

    @When("the merchant with id {string} is retrieved")
    public void merchantRetrieved(String merchantId) {
        try {
            merchantController.getMerchant(merchantId);
        } catch (NotFoundException e) {
            notFoundExceptionThrown = true;
        }
    }

    @When("a bank account request for the customer with correlation ID {string} is emitted")
    public void customerBankAccountRequestEmitted(String corrId) {
        Event event = new Event(BANKACCOUNT_CUSTOMER_REQ_RK, new Object[] { customer.dtupayUuid(), corrId });
        mq.publish(event);
    }

    @When("a bank account request for the merchant with correlation ID {string} is emitted")
    public void merchantBankAccountRequestEmitted(String corrId) {
        Event event = new Event(BANKACCOUNT_MERCHANT_REQ_RK, new Object[] { merchant.dtupayUuid(), corrId });
        mq.publish(event);
    }

    @Then("the customer registration is successful")
    public void customerRegistrationSuccessful() {
        assertNotNull(customer);
        assertNotNull(customer.dtupayUuid());
        assertTrue(Database.getInstance().hasCustomer(customer.dtupayUuid()));
    }

    @Then("the merchant registration is successful")
    public void merchantRegistrationSuccessful() {
        assertNotNull(merchant);
        assertNotNull(merchant.dtupayUuid());
        assertTrue(Database.getInstance().hasMerchant(merchant.dtupayUuid()));
    }

    @Then("the customer account information can be retrieved")
    public void customerAccountInfoRetrieved() {
        Customer retrievedCustomer = customerController.getCustomer(customer.dtupayUuid());
        assertEquals(customer, retrievedCustomer);
    }

    @Then("the merchant account information can be retrieved")
    public void merchantAccountInfoRetrieved() {
        Merchant retrievedMerchant = merchantController.getMerchant(merchant.dtupayUuid());
        assertEquals(merchant, retrievedMerchant);
    }

    @Then("the customer unregistration is successful")
    public void customerUnregistrationSuccessful() {
        String customerId = customer.dtupayUuid();
        assertTrue(!customerController.hasCustomer(customerId));
        assertThrows(NotFoundException.class, () -> {
            customerController.getCustomer(customerId);
        });
    }

    @Then("the merchant unregistration is successful")
    public void merchantUnregistrationSuccessful() {
        String merchantId = merchant.dtupayUuid();
        assertTrue(!merchantController.hasMerchant(merchantId));
        assertThrows(NotFoundException.class, () -> {
            customerController.getCustomer(merchantId);
        });
    }

    @Then("the customer deletion event is received")
    public void customerDeletedEventReceived() {
        assertTrue(receivedDeletedCustomerEvent);
    }

    @Then("the merchant deletion event is received")
    public void merchantDeletedEventReceived() {
        assertTrue(receivedDeletedMerchantEvent);
    }

    @Then("a NotFoundException is thrown")
    public void a_not_found_exception_is_thrown() {
        assertTrue(notFoundExceptionThrown);
    }

    @Then("a bank account response for the customer with bank ID {string} and correlation ID {string} is emitted")
    public void customerBankAccountResponseEmitted(String bankId, String corrId) {
        assertNotNull(customerBankAccountResponse);
        String receivedBankId = customerBankAccountResponse.getArgument(0, String.class);
        String receivedCorrId = customerBankAccountResponse.getArgument(1, String.class);
        assertEquals(bankId, receivedBankId);
        assertEquals(corrId, receivedCorrId);
    }

    @Then("a bank account response for the merchant with bank ID {string} and correlation ID {string} is emitted")
    public void merchantBankAccountResponseEmitted(String bankId, String corrId) {
        assertNotNull(merchantBankAccountResponse);
        String receivedBankId = merchantBankAccountResponse.getArgument(0, String.class);
        String receivedCorrId = merchantBankAccountResponse.getArgument(1, String.class);
        assertEquals(bankId, receivedBankId);
        assertEquals(corrId, receivedCorrId);
    }
}