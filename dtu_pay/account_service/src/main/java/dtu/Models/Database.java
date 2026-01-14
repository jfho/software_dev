package dtu.Models;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  Simple in-memory database, emulating a real database.
 */

public class Database {
    private static final Database INSTANCE = new Database();

    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Merchant> merchants = new ConcurrentHashMap<>();

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }

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

    public boolean deleteCustomer(Customer c) {
        if (c == null) return false;
        return customers.remove(c.dtupayUuid(), c);
    }

    public boolean deleteCustomer(String id) {
        return customers.remove(id) != null;
    }

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

    public boolean deleteMerchant(Merchant m) {
        if (m == null) return false;
        return merchants.remove(m.dtupayUuid(), m);
    }

    public boolean deleteMerchant(String id) {
        return merchants.remove(id) != null;
    }
    
    public void clean() {
        customers.clear();
        merchants.clear();
    }
}
