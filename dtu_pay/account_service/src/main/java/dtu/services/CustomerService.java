package dtu.services;

import java.util.UUID;

import org.jboss.logging.Logger;

import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Customer;
import dtu.Database;
import jakarta.ws.rs.NotFoundException;

public class CustomerService {
    private final Database db = Database.getInstance();
    MessageQueue queue;

    private final String BANKACCOUNT_CUSTOMER_REQ_RK = "TokenValidated";
    private final String BANKACCOUNT_CUSTOMER_RES_RK = "CustomerBankAccountRetrieved";

    private String REGISTER_CUSTOMER_REQ_RK = "CustomerRegistrationRequested";
    private String GET_CUSTOMER_REQ_RK = "CustomerGetRequested";
    private String DELETE_CUSTOMER_REQ_RK = "CustomerDeletionRequested";

    private String REGISTER_CUSTOMER_RES_RK = "CustomerRegistered";
    private String GET_CUSTOMER_RES_RK = "CustomerFetched";
    private String DELETE_CUSTOMER_RES_RK = "CustomerDeleted";

    
    private static final Logger LOG = Logger.getLogger(CustomerService.class);

    public CustomerService(MessageQueue q) {
        LOG.info("Starting CustomerService");

        queue = q;

        // get customer by id handler (facade)
        queue.addHandler(GET_CUSTOMER_REQ_RK, e -> {
            LOG.info("received customer get request");
            String customerId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);

            Customer customer = getCustomer(customerId);

            queue.publish(new Event(GET_CUSTOMER_RES_RK, new Object[] { customer, corrId } ));
		});

        // register customer handler (facade)
        queue.addHandler(REGISTER_CUSTOMER_REQ_RK, e -> {
            LOG.info("received customer registration request");
            Customer customerToRegister = e.getArgument(0, Customer.class);
            String corrId = e.getArgument(1, String.class);

            Customer newCustomer = registerCustomer(customerToRegister);

            queue.publish(new Event(REGISTER_CUSTOMER_RES_RK, new Object[] { newCustomer, corrId } ));
		});

        // delete customer handler (facade)
        queue.addHandler(DELETE_CUSTOMER_REQ_RK, e -> {
            LOG.info("received customer delete request");
            String customerId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);

            boolean success = deleteCustomer(customerId);

            queue.publish(new Event(DELETE_CUSTOMER_RES_RK, new Object[] { success, corrId } ));
		});

        // get bankaccount from accountId (payment service)
        queue.addHandler(BANKACCOUNT_CUSTOMER_REQ_RK, e -> {
            LOG.info("received customer bank account request message");
            String customerId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);
            LOG.info("customerId: " + customerId + ", corrId: " + corrId);

            String bankAccountId = null;
            if (customerId != null && db.hasCustomer(customerId)) {
                bankAccountId = db.getCustomer(customerId).bankAccountUuid();
            }

            queue.publish(new Event(BANKACCOUNT_CUSTOMER_RES_RK, new Object[] { bankAccountId, corrId } ));
		});
    }

    public Customer getCustomer(String customerId) {
        return db.getCustomer(customerId);
    }

    public Customer registerCustomer(Customer customer) {
        if (db.hasCustomerWithCpr(customer.cpr())) {
            return null;
        }
        
        String dtupayUuid = UUID.randomUUID().toString();
        Customer registeredCustomer = new Customer(customer.firstName(), customer.lastName(), customer.cpr(), customer.bankAccountUuid(), dtupayUuid);
        db.addCustomer(registeredCustomer);
        return registeredCustomer;
    }
    
    public boolean deleteCustomer(String id) {
        return db.deleteCustomer(id);
    }

    public boolean hasCustomer(String id) {
        return db.hasCustomer(id);
    }
}