package org.dtu.Models;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple in-memory application-wide "database".
 * Thread-safe collections are used for concurrent access.
 */
public class Database {
    private static final Database INSTANCE = new Database();

    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Merchant> merchants = new ConcurrentHashMap<>();
    private final List<Transaction> transactions = new CopyOnWriteArrayList<>();

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }

    // Customers
    public void addCustomer(Customer c) {
        customers.put(c.dtupayUuid(), c);
    }

    public Customer getCustomer(String id) {
        return customers.get(id);
    }

    public boolean hasCustomer(String id) {
        return customers.containsKey(id);
    }

    public List<Customer> listCustomers() {
        return List.copyOf(customers.values());
    }

    /**
     * Delete a customer by object. Returns true if the customer was removed.
     */
    public boolean deleteCustomer(Customer c) {
        if (c == null) return false;
        return customers.remove(c.dtupayUuid(), c);
    }

    /**
     * Delete a customer by id. Returns true if the customer was removed.
     */
    public boolean deleteCustomer(String id) {
        return customers.remove(id) != null;
    }

    // Merchants
    public void addMerchant(Merchant m) {
        merchants.put(m.dtupayUuid(), m);
    }

    public Merchant getMerchant(String id) {
        return merchants.get(id);
    }

    public boolean hasMerchant(String id) {
        return merchants.containsKey(id);
    }

    public List<Merchant> listMerchants() {
        return List.copyOf(merchants.values());
    }

    /**
     * Delete a merchant by object. Returns true if the merchant was removed.
     */
    public boolean deleteMerchant(Merchant m) {
        if (m == null) return false;
        return merchants.remove(m.dtupayUuid(), m);
    }

    /**
     * Delete a merchant by id. Returns true if the merchant was removed.
     */
    public boolean deleteMerchant(String id) {
        return merchants.remove(id) != null;
    }

    // Transactions
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public List<Transaction> listTransactions() {
        return List.copyOf(transactions);
    }
}
