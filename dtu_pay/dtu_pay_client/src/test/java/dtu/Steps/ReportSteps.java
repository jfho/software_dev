/**
 * @author s215698
 */

package dtu.Steps;

import dtu.*;
import dtu.Models.BankAccount;
import dtu.Models.Customer;
import dtu.Models.Merchant;
import dtu.Models.Transaction;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ReportSteps {

    private final State state;
    private final CustomerClient customerClient = new CustomerClient();
    private final MerchantClient merchantClient = new MerchantClient();
    private final ManagerClient managerClient = new ManagerClient();
    private BankClient bank = new BankClient();

    Transaction tx = null;
    
    private String customer2BankUuid = null;
    private String merchant2BankUuid = null;

    public ReportSteps(State state) {
        this.state = state;
    }

    @Before("@reports")
    public void setup() {
        state.tokens = null;
        state.transactions = null;
        state.lastException = null;
        state.customer = null;
        state.customer2 = null;
        state.merchant = null;
        state.merchant2 = null;

        tx = null;
        customer2BankUuid = null;
        merchant2BankUuid = null;
    }

    @After("@reports")
    public void cleanup() {
        if (state.customer != null) {
            customerClient.unregister(state.customer);
            
            if (state.customer.bankAccountUuid() != null) {
                bank.unregister(state.customer.bankAccountUuid());
            }
        }

        if (state.customer2 != null) {
            customerClient.unregister(state.customer2);
            
            if (customer2BankUuid != null) {
                bank.unregister(customer2BankUuid);
            }
        }

        if (state.merchant != null) {
            merchantClient.unregister(state.merchant);
            bank.unregister(state.merchant.bankAccountUuid());
        }

        if (state.merchant2 != null) {
            merchantClient.unregister(state.merchant2);
            
            if (merchant2BankUuid != null) {
                bank.unregister(merchant2BankUuid);
            }
        }
    }

    @Given("another customer bank account with first name {string}, last name {string}, CPR {string}, and balance {string}")
    public void anotherCustomerBankAccount(String firstName, String lastName, String cpr, String balance) {
        BankAccount account = new BankAccount(firstName, lastName, cpr, new BigDecimal(balance));
        customer2BankUuid = bank.register(account);
    }

    @Given("another customer registers for DTUPay with first name {string}, last name {string}, CPR {string}")
    public void anotherCustomerRegisters(String fn, String ln, String cpr) {
        Customer customer = new Customer(fn, ln, cpr, customer2BankUuid, null);
        state.customer2 = customerClient.register(customer);
    }

    @Given("another merchant bank account with first name {string}, last name {string}, CPR {string}, and balance {string}")
    public void anotherMerchantBankAccount(String firstName, String lastName, String cpr, String balance) {
        BankAccount account = new BankAccount(firstName, lastName, cpr, new BigDecimal(balance));
        merchant2BankUuid = bank.register(account);
    }

    @Given("another merchant registers for DTUPay with first name {string}, last name {string}, CPR {string}")
    public void anotherMerchantRegisters(String fn, String ln, String cpr) {
        Merchant merchant = new Merchant(fn, ln, cpr, merchant2BankUuid, null);
        state.merchant2 = merchantClient.register(merchant);
    }

    @Given("the merchant has a token from the other customer")
    public void merchantHasTokenFromOtherCustomer() {
        state.tokens = customerClient.getTokens(state.customer2.dtupayUuid(), 1);
    }

    @When("the other merchant has a token from the customer")
    public void otherMerchantHasTokenFromCustomer() {
        state.tokens = customerClient.getTokens(state.customer.dtupayUuid(), 1);
    }

    @When("the other merchant has a token from the other customer")
    public void otherMerchantHasTokenFromOtherCustomer() {
        state.tokens = customerClient.getTokens(state.customer2.dtupayUuid(), 1);
    }
    
    @When("the other merchant initiates a transaction for {string} kr")
    public void otherMerchantInitiatesTransaction(String amount) {
        merchantClient.pay(
                state.tokens.get(0),
                state.merchant2.dtupayUuid(),
                amount);
    }

    @When("the customer requests the report")
    public void customerRequestsReport() {
        state.transactions = customerClient.getReports(state.customer.dtupayUuid());
    }

    @When("the merchant requests the report")
    public void merchantRequestsReport() {
        state.transactions = merchantClient.getReports(state.merchant.dtupayUuid());
    }

    @When("the manager requests the report")
    public void managerRequestsReport() {
        state.transactions = managerClient.getReports();
    }

    @Then("the customer gets the report of the {string} payments")
    public void customerGetsReport(String count) {
        assertEquals(Integer.parseInt(count), state.transactions.size());
    }

    @Then("the merchant gets the report of {string} payments")
    public void merchantGetsReport(String count) {
        assertEquals(Integer.parseInt(count), state.transactions.size());
    }

    @Then("the manager gets the report of the at least {string} payments")
    public void managerGetsReport(String count) {
        assertTrue(Integer.parseInt(count) < state.transactions.size());
    }

    @Then("there is a payment from the customer to the merchant with amount {string}")
    public void thereIsAPayment(String amount) {
        boolean success = false;
        for (Transaction tx : state.transactions) {
            if (
                tx != null
                && tx.customerId().equals(state.customer.dtupayUuid())
                && tx.merchantId().equals(state.merchant.dtupayUuid())
                && tx.amount().equals(new BigDecimal(amount))
            ) {
                this.tx = tx;
                success = true;
                break;
            }
        }
        
        assertTrue(success);
    }

    @Then("there is a payment from the other customer to the other merchant with amount {string}")
    public void thereIsAPaymentFromOtherCustomerToOtherMerchant(String amount) {
        boolean success = false;
        for (Transaction tx : state.transactions) {
            if (
                tx != null
                && tx.customerId().equals(state.customer2.dtupayUuid())
                && tx.merchantId().equals(state.merchant2.dtupayUuid())
                && tx.amount().equals(new BigDecimal(amount))
            ) {
                this.tx = tx;
                success = true;
                break;
            }
        }

        assertTrue(success);
    }

    @Then("there is a payment to the merchant with amount {string}")
    public void thereIsAMerchantPayment(String amount) {
        boolean success = false;
        for (Transaction tx : state.transactions) {
            if (
                tx != null
                && tx.merchantId().equals(state.merchant.dtupayUuid())
                && tx.amount().equals(new BigDecimal(amount))
            ) {
                this.tx = tx;
                success = true;
                break;
            }
        }
        
        assertTrue(success);
    }

    @Then("the payment does not contain the customer ID")
    public void the_payment_does_not_contain_the_customer_id() {
        assertNotNull(tx);
        assertNull(tx.customerId());
    }

    @Then("the customer gets an empty report")
    public void customerGetsEmptyReport() {
        assertNotNull(state.transactions);
        assertTrue(state.transactions.isEmpty());
    }
}
