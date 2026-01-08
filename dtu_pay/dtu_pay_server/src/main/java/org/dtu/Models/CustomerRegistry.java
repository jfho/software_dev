package org.dtu.Models;

import java.util.HashMap;
import java.util.Map;

public class CustomerRegistry {
    private static CustomerRegistry instance;
    private Map<String, String> customerMap;
    private Map<String, String> uuidMap;

    private CustomerRegistry() {
        customerMap = new HashMap<>();
        uuidMap = new HashMap<>();
    }

    public static synchronized CustomerRegistry getInstance() {
        if (instance == null) {
            instance = new CustomerRegistry();
        }
        return instance;
    }

    public void addCustomer(String customerId, String uuid) {
        customerMap.put(customerId, uuid);
        uuidMap.put(uuid, customerId);
    }

    public String getUuid(String customerId) {
        return customerMap.get(customerId);
    }

    public String getCustomerId(String uuid) {
        return uuidMap.get(uuid);
    }

    public boolean hasCustomer(String customerId) {
        return customerMap.containsKey(customerId);
    }

    public boolean hasUuid(String uuid) {
        return uuidMap.containsKey(uuid);
    }
}