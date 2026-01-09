package org.dtu.Controllers;

import java.util.UUID;

import org.dtu.Models.Customer;
import org.dtu.Models.Database;

public class CustomerController {
    private final Database db = Database.getInstance();

    public Customer getCustomer(String customerId) {
        return db.getCustomer(customerId);
    }

    public Customer registerCustomer(Customer customer) {
        String dtupayUuid = UUID.randomUUID().toString();
        Customer registeredCustomer = new Customer(customer.firstName(), customer.lastName(), customer.cpr(), customer.bankAccountUuid(), dtupayUuid);
        db.addCustomer(registeredCustomer);
        return registeredCustomer;
    }
    
    public void deleteCustomer(String id) {
        db.deleteCustomer(id);
    }
}