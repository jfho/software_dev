package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.NotFoundException;
import dtu.services.CustomerService;
import dtu.services.MerchantService;
import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Customer;
import dtu.Database;
import dtu.models.Merchant;
import dtu.models.Transaction;

public class AccountServiceSteps {
    Database db = Database.getInstance();
    
    MessageQueue mq;
    CustomerService customerService;
    MerchantService merchantService;

    Customer customer = null;
    Merchant merchant = null;

    boolean receivedDeletedCustomerEvent = false;
    boolean receivedDeletedMerchantEvent = false;
    boolean notFoundExceptionThrown = false;

    Event customerBankAccountResponse = null;
    Event merchantBankAccountResponse = null;

    private String BANKACCOUNT_CUSTOMER_REQ_RK = "TokenValidated";
    private String BANKACCOUNT_CUSTOMER_RES_RK = "CustomerBankAccountRetrieved";
    private String BANKACCOUNT_MERCHANT_REQ_RK = "PaymentRequested";
    private String BANKACCOUNT_MERCHANT_RES_RK = "MerchantBankAccountRetrieved";

    private String REGISTER_CUSTOMER_REQ_RK = "CustomerRegistrationRequested";
    private String REGISTER_CUSTOMER_RES_RK = "CustomerRegistered";

    private String GET_CUSTOMER_REQ_RK = "CustomerGetRequested";
    private String GET_CUSTOMER_RES_RK = "CustomerFetched";

    private String DELETE_CUSTOMER_REQ_RK = "CustomerDeletionRequested";
    private String DELETE_CUSTOMER_RES_RK = "CustomerDeleted";
    
    private String REGISTER_MERCHANT_REQ_RK = "MerchantRegistrationRequested";
    private String REGISTER_MERCHANT_RES_RK = "MerchantRegistered";

    private String GET_MERCHANT_REQ_RK = "MerchantGetRequested";
    private String GET_MERCHANT_RES_RK = "MerchantFetched";

    private String DELETE_MERCHANT_REQ_RK = "MerchantDeletionRequested";
    private String DELETE_MERCHANT_RES_RK = "MerchantDeleted";

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
        customerService = new CustomerService(mq);
        merchantService = new MerchantService(mq);

        db.clean();
    }

    @Given("a customer account with first name {string}, last name {string}, CPR {string}, bank ID {string}")
    public void customerIsRegistered(String fn, String ln, String cpr, String bankId) {
        mq.addHandler(REGISTER_CUSTOMER_RES_RK, e -> {
            customer = e.getArgument(0, Customer.class);
        });
        
        Event event = new Event(REGISTER_CUSTOMER_REQ_RK, new Object[] { new Customer(fn, ln, cpr, bankId, null), UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @Given("a merchant account with first name {string}, last name {string}, CPR {string}, bank ID {string}")
    public void merchantIsRegistered(String fn, String ln, String cpr, String bankId) {
        mq.addHandler(REGISTER_MERCHANT_RES_RK, e -> {
            merchant = e.getArgument(0, Merchant.class);
        });
        
        Event event = new Event(REGISTER_MERCHANT_REQ_RK, new Object[] { new Merchant(fn, ln, cpr, bankId, null), UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @When("the customer unregisters for DTUPay")
    public void customerUnregisters() {
        mq.addHandler(DELETE_CUSTOMER_RES_RK, event -> {
            receivedDeletedCustomerEvent = true;
        });

        String customerId = customer.dtupayUuid();
        Event event = new Event(DELETE_CUSTOMER_REQ_RK, new Object[] { customerId, UUID.randomUUID().toString() } );
        mq.publish(event);
    }    
    
    @When("the merchant unregisters for DTUPay")
    public void merchantUnregisters() {
        mq.addHandler(DELETE_MERCHANT_RES_RK, event -> {
            receivedDeletedMerchantEvent = true;
        });

        String merchantId = merchant.dtupayUuid();
        Event event = new Event(DELETE_MERCHANT_REQ_RK, new Object[] { merchantId, UUID.randomUUID().toString() } );
        mq.publish(event);
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
        mq.addHandler(REGISTER_CUSTOMER_RES_RK, e -> {
            customer = e.getArgument(0, Customer.class);
        });

        Event event = new Event(REGISTER_CUSTOMER_REQ_RK, new Object[] { new Customer(fn, ln, cpr, bankId, null), UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @When("a merchant registers for DTUPay with first name {string}, last name {string}, CPR {string}, bank ID {string}")
    public void merchantRegistration(String fn, String ln, String cpr, String bankId) {
        mq.addHandler(REGISTER_MERCHANT_RES_RK, e -> {
            merchant = e.getArgument(0, Merchant.class);
        });
        
        Event event = new Event(REGISTER_MERCHANT_REQ_RK, new Object[] { new Merchant(fn, ln, cpr, bankId, null), UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @When("the customer with id {string} is retrieved")
    public void customerRetrieved(String customerId) {
        mq.addHandler(GET_CUSTOMER_RES_RK, e -> {
            customer = e.getArgument(0, Customer.class);
        });

        Event event = new Event(GET_CUSTOMER_REQ_RK, new Object[] { customerId, UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @When("the merchant with id {string} is retrieved")
    public void merchantRetrieved(String merchantId) {
        mq.addHandler(GET_MERCHANT_RES_RK, e -> {
            merchant = e.getArgument(0, Merchant.class);
        });

        Event event = new Event(GET_MERCHANT_REQ_RK, new Object[] { merchantId, UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @When("a bank account request for the customer with correlation ID {string} is emitted")
    public void customerBankAccountRequestEmitted(String corrId) {
        Event event = new Event(BANKACCOUNT_CUSTOMER_REQ_RK, new Object[] { customer.dtupayUuid(), corrId });
        mq.publish(event);
    }

    @When("a bank account request for the merchant with correlation ID {string} is emitted")
    public void merchantBankAccountRequestEmitted(String corrId) {
        Transaction transaction = new Transaction(null, merchant.dtupayUuid(), null);
        Event event = new Event(BANKACCOUNT_MERCHANT_REQ_RK, new Object[] { transaction, corrId });
        mq.publish(event);
    }

    @Then("the customer registration is successful")
    public void customerRegistrationSuccessful() {
        assertNotNull(customer);
        assertNotNull(customer.dtupayUuid());
        assertTrue(customerService.hasCustomer(customer.dtupayUuid()));
    }

    @Then("the merchant registration is successful")
    public void merchantRegistrationSuccessful() {
        assertNotNull(merchant);
        assertNotNull(merchant.dtupayUuid());
        assertTrue(merchantService.hasMerchant(merchant.dtupayUuid()));
    }

    @Then("the customer account information can be retrieved")
    public void customerAccountInfoRetrieved() {
        mq.addHandler(GET_CUSTOMER_RES_RK, e -> {
            Customer retrievedCustomer = e.getArgument(0, Customer.class);
            assertEquals(customer, retrievedCustomer);
        });

        Event event = new Event(GET_CUSTOMER_REQ_RK, new Object[] { customer.dtupayUuid(), UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @Then("the merchant account information can be retrieved")
    public void merchantAccountInfoRetrieved() {
        mq.addHandler(GET_MERCHANT_RES_RK, e -> {
            Merchant retrievedMerchant = e.getArgument(0, Merchant.class);
            assertEquals(merchant, retrievedMerchant);
        });

        Event event = new Event(GET_MERCHANT_REQ_RK, new Object[] { merchant.dtupayUuid(), UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @Then("the customer unregistration is successful")
    public void customerUnregistrationSuccessful() {
        assertTrue(receivedDeletedCustomerEvent);
        assertFalse(customerService.hasCustomer(customer.dtupayUuid()));
        assertNull(customerService.getCustomer(customer.dtupayUuid()));

        mq.addHandler(GET_CUSTOMER_RES_RK, e -> {
            Customer retrievedCustomer = e.getArgument(0, Customer.class);
            assertNull(retrievedCustomer);
        });

        Event event = new Event(GET_CUSTOMER_REQ_RK, new Object[] { customer.dtupayUuid(), UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @Then("the merchant unregistration is successful")
    public void merchantUnregistrationSuccessful() {
        assertTrue(receivedDeletedMerchantEvent);
        assertFalse(merchantService.hasMerchant(merchant.dtupayUuid()));
        assertNull(merchantService.getMerchant(merchant.dtupayUuid()));

        mq.addHandler(GET_MERCHANT_RES_RK, e -> {
            Merchant retrievedMerchant = e.getArgument(0, Merchant.class);
            assertNull(retrievedMerchant);
        });

        Event event = new Event(GET_MERCHANT_REQ_RK, new Object[] { merchant.dtupayUuid(), UUID.randomUUID().toString() });
        mq.publish(event);
    }

    @Then("the customer is null")
    public void customerIsNull() {
        assertNull(customer);
    }

    @Then("the merchant is null")
    public void merchantIsNull() {
        assertNull(merchant);
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