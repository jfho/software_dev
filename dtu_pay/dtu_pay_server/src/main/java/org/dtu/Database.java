package org.dtu;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dtu.models.Customer;
import org.dtu.models.Transaction;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankService_Service;

import org.dtu.models.Merchant;
import java.math.BigDecimal;
import dtu.ws.fastmoney.User;
import dtu.ws.fastmoney.BankServiceException_Exception;

/**
 * Simple in-memory application-wide "database".
 * Thread-safe collections are used for concurrent access.
 */
public class Database {
    private static final Database INSTANCE = new Database();

    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Merchant> merchants = new ConcurrentHashMap<>();
    private final List<Transaction> transactions = new CopyOnWriteArrayList<>();
    private BankService bank = new BankService_Service().getBankServicePort();
    // map of bank account id -> apiKey that created it
    private final Map<String, String> accountOwners = new ConcurrentHashMap<>();

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }



    // Customers
    public void addCustomer(Customer c) {
        customers.put(c.getCpr(), c);
    }

    public Customer getCustomer(int cpr) {
        return customers.get(String.valueOf(cpr));
    }

    public boolean hasCustomer(int cpr) {
        return customers.containsKey(String.valueOf(cpr));
    }

    public List<Customer> listCustomers() {
        return List.copyOf(customers.values());
    }

    // Merchants
    public void addMerchant(Merchant m) {
        merchants.put(m.getCpr(), m);
    }

    public Merchant getMerchant(int cpr) {
        return merchants.get(String.valueOf(cpr));
    }

    public boolean hasMerchant(int cpr) {
        return merchants.containsKey(String.valueOf(cpr));
    }

    public List<Merchant> listMerchants() {
        return List.copyOf(merchants.values());
    }

    // Transactions
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public List<Transaction> listTransactions() {
        return List.copyOf(transactions);
    }

    // Bank account helpers
    public String createBankAccountForCustomer(int customerId, String apiKey, int balance) throws BankServiceException_Exception {
        Customer c = getCustomer(customerId);
        if (c == null) {
            return null;
        }

        User user = new User();
        user.setCprNumber(c.getCpr());
        user.setFirstName(c.getFirstName());
        user.setLastName(c.getLastName());

        String accountId = bank.createAccountWithBalance(apiKey, user, BigDecimal.valueOf(balance));
        if (accountId != null) {
            accountOwners.put(accountId, apiKey);
        }
        return accountId;
    }

    public boolean retireBankAccount(String accountId, String apiKey) throws BankServiceException_Exception {
        if (accountId == null) return false;
        String owner = accountOwners.get(accountId);
        if (owner == null) return false;
        if (!owner.equals(apiKey)) return false;

        bank.retireAccount(apiKey, accountId);
        accountOwners.remove(accountId);
        return true;
    }
}
