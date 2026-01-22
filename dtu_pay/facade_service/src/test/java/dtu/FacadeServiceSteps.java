/**
 * @author s253872
 */

package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dtu.messagingUtils.Event;
import dtu.models.Customer;
import dtu.models.Merchant;
import dtu.models.Transaction;
import dtu.services.CustomerService;
import dtu.services.ManagerService;
import dtu.services.MerchantService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.InternalServerErrorException;

public class FacadeServiceSteps {
    private MockQueue mq = new MockQueue();
    private CustomerService customerService = new CustomerService(mq);
    private MerchantService merchantService = new MerchantService(mq);
    private ManagerService managerService = new ManagerService(mq);

    private Merchant merchant;
    private Merchant resultMerchant;
    private Customer customer;
    private Customer resultCustomer;
    private Exception exception;
    private List<Transaction> transactions = new ArrayList<>();
    private List<Transaction> resultTransactions = new ArrayList<>();

    private final String REGISTER_MERCHANT_REQ_RK = "MerchantRegistrationRequested";
    private final String REGISTER_MERCHANT_RES_RK = "MerchantRegistered";
    private final String REGISTER_CUSTOMER_REQ_RK = "CustomerRegistrationRequested";
    private final String REGISTER_CUSTOMER_RES_RK = "CustomerRegistered";
    private final String MANAGER_REPORT_REQ = "ManagerReportRequested";
    private final String MANAGER_REPORT_RES = "ManagerReportFetched";

    @Given("a merchant with first name {string}, last name {string}, CPR {string}, bank ID {string}")
    public void a_merchant_with_first_name_last_name_cpr_bank_id(String first_name, String last_name, String cpr,
            String bankId) {
        merchant = new Merchant(first_name, last_name, cpr, bankId, null);
    }

    @Given("the message queue is listening for merchant registration events")
    public void the_message_queue_is_listening_for_merchant_registration_events() {
        mq.addHandler(REGISTER_MERCHANT_REQ_RK, event -> {
            Merchant merchant = event.getArgument(0, Merchant.class);
            String correlationId = event.getArgument(1, String.class);
            Merchant responseMerchant = new Merchant(merchant.firstName(),
                    merchant.lastName(), merchant.cpr(), merchant.bankAccountUuid(), UUID.randomUUID().toString());
            mq.publish(new Event(REGISTER_MERCHANT_RES_RK, new Object[] { responseMerchant, correlationId }));
        });
    }

    @When("the merchant registers for DTUPay")
    public void the_merchant_registers_for_dtu_pay() {
        try {
            resultMerchant = merchantService.registerMerchant(merchant);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("the backend service does not reply")
    public void the_backend_service_does_not_reply() {

    }

    @Then("the merchant object is returned with a DTUPay UUID")
    public void the_merchant_object_is_returned_with_a_dtu_pay_uuid() {
        assertNotNull(resultMerchant.dtupayUuid());
        assertFalse(resultMerchant.dtupayUuid().isEmpty());
    }

    @Given("a customer with first name {string}, last name {string}, CPR {string}, bank ID {string}")
    public void a_customer_with_first_name_last_name_cpr_bank_id(String first_name, String last_name, String cpr,
            String bankId) {
        customer = new Customer(first_name, last_name, cpr, bankId, null);
    }

    @Given("the message queue is listening for customer registration events")
    public void the_message_queue_is_listening_for_customer_registration_events() {
        mq.addHandler(REGISTER_CUSTOMER_REQ_RK, event -> {
            Customer customer = event.getArgument(0, Customer.class);
            String correlationId = event.getArgument(1, String.class);
            Customer responseCustomer = new Customer(customer.firstName(),
                    customer.lastName(), customer.cpr(), customer.bankAccountUuid(), UUID.randomUUID().toString());
            mq.publish(new Event(REGISTER_CUSTOMER_RES_RK, new Object[] { responseCustomer, correlationId }));
        });
    }

    @When("the customer registers for DTUPay")
    public void the_customer_registers_for_dtu_pay() {
        try {
            resultCustomer = customerService.registerCustomer(customer);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the customer object is returned with a DTUPay UUID")
    public void the_customer_object_is_returned_with_a_dtu_pay_uuid() {
        assertNotNull(resultCustomer.dtupayUuid());
        assertFalse(resultCustomer.dtupayUuid().isEmpty());
    }

    @Then("the facade gives an error")
    public void check_timeout_exception() {
        assertTrue(exception instanceof InternalServerErrorException);
    }

    @Given("a payment for {string} kr exists")
    public void a_payment_for_kr_exists(String amount) {
        Transaction transaction = new Transaction(null, null, null, amount, null, null);
        transactions.add(transaction);
    }

    @Given("the message queue is listening for manager report request events")
    public void the_message_queue_is_listening_for_manager_report_request_events() {
        mq.addHandler(MANAGER_REPORT_REQ, event -> {
            String correlationId = event.getArgument(0, String.class);
            mq.publish(new Event(MANAGER_REPORT_RES, new Object[] { transactions, correlationId }));
        });
    }

    @When("the manager requests a report of payments")
    public void the_manager_requests_a_report_of_payments() {
        try {
            resultTransactions = managerService.getAllTransactions();
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("a list of all payments are returned")
    public void a_list_of_all_payments_are_returned() {
        assertNotNull(resultTransactions);
        assertFalse(resultTransactions.isEmpty());
        assertEquals(transactions.get(0).amount(), resultTransactions.get(0).amount());
    }
}