package dtu.Controllers;

import java.util.UUID;

import org.jboss.logging.Logger;

import dtu.MessagingUtils.Event;
import dtu.MessagingUtils.MessageQueue;
import dtu.Models.Customer;
import dtu.Models.Database;
import jakarta.ws.rs.NotFoundException;

public class CustomerController {
    private final Database db = Database.getInstance();
    MessageQueue queue;

    private String BANKACCOUNT_CUSTOMER_REQ_RK = "payments.customerbankaccount.request";
    private String BANKACCOUNT_CUSTOMER_RES_RK = "accounts.customerbankaccount.response";
    private String DELETE_CUSTOMER_RK = "accounts.customer.deleted";

    private static final Logger LOG = Logger.getLogger(CustomerController.class);

    public CustomerController(MessageQueue q) {
        queue = q;

        queue.addHandler(BANKACCOUNT_CUSTOMER_REQ_RK, e -> {
            LOG.info("RabbitConsumer received message");
            String accountId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);

            String bankAccountId = null;
            if (db.hasCustomer(accountId)) {
                bankAccountId = db.getCustomer(accountId).bankAccountUuid();
            }

            queue.publish(new Event(BANKACCOUNT_CUSTOMER_RES_RK, new Object[] { bankAccountId, corrId } ));
		});
    }

    public Customer getCustomer(String customerId) {
        Customer customer = db.getCustomer(customerId);
        if (customer == null) {
            throw new NotFoundException("merchant not found");
        }
        return customer;
    }

    public Customer registerCustomer(Customer customer) {
        String dtupayUuid = UUID.randomUUID().toString();
        Customer registeredCustomer = new Customer(customer.firstName(), customer.lastName(), customer.cpr(), customer.bankAccountUuid(), dtupayUuid);
        db.addCustomer(registeredCustomer);
        return registeredCustomer;
    }
    
    public void deleteCustomer(String id) {
        if (!db.hasCustomer(id)) {
            throw new NotFoundException("Customer not found");
        }
        
        db.deleteCustomer(id);
        queue.publish(new Event(DELETE_CUSTOMER_RK, new Object[] { id }));
    }

    public boolean hasCustomer(String id) {
        return db.hasCustomer(id);
    }
}