package dtu.example;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import dtu.example.Models.BankAccount;
import dtu.ws.fastmoney.Account;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SimpleDTUPaySteps {   
    private SimpleDtuPay dtupay = new SimpleDtuPay();

    private String customerUuid = null;
    private String merchantUuid = null;
    private boolean successful = true;

    @Before
    public void setup() {
        customerUuid = null;
        merchantUuid = null;
    }

    @After
    public void cleanup() {
        if (customerUuid != null) {
            dtupay.bankUnregister(customerUuid);
        }

        if (merchantUuid != null) {
            dtupay.bankUnregister(merchantUuid);
        }
    }

    @Given("a customer with name {string}, last name {string}, CPR {string}, and balance {string}")
    public void a_customer_with_name_last_name_cpr_and_balance(String firstName, String lastName, String cpr, String balance) {
        BankAccount account = new BankAccount(firstName, lastName, cpr, Double.parseDouble(balance));
        customerUuid = dtupay.bankRegister(account);
    }
   
    @Given("a merchant with name {string}, last name {string}, CPR {string}, and balance {string}")
    public void a_merchant_with_name_last_name_cpr_and_balance(String firstName, String lastName, String cpr, String balance) {
        BankAccount account = new BankAccount(firstName, lastName, cpr, Double.parseDouble(balance));
        merchantUuid = dtupay.bankRegister(account);
    }

    @When("the merchant initiates a payment for {string} kr by the customer")
    public void the_merchant_initiates_a_payment_for_kr_by_the_customer(String amount) {
        successful = dtupay.pay(customerUuid, merchantUuid, new BigDecimal(amount));
    }

    @Then("the payment is successful")
    public void the_payment_is_successful() {
        assertTrue(successful);
    }

    @Then("the balance of the customer at the bank is {string} kr")
    public void the_balance_of_the_customer_at_the_bank_is_kr(String newBalance) {
        Account customerBankAccount = dtupay.bankAccount(customerUuid);
        assertTrue(customerBankAccount.getBalance().compareTo(new BigDecimal(newBalance)) == 0);

    }

    @Then("the balance of the merchant at the bank is {string} kr")
    public void the_balance_of_the_merchant_at_the_bank_is_kr(String newBalance) {
        Account merchantBankAccount = dtupay.bankAccount(merchantUuid);
        assertTrue(merchantBankAccount.getBalance().compareTo(new BigDecimal(newBalance)) == 0);
    }

}