package dtu;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

import dtu.models.CustomerTransaction;
import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Customer;

public class CustomerService {
    MessageQueue mq;

    private Map<String, CompletableFuture<Event>> pendingRequests = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger(CustomerService.class);

    public CustomerService(MessageQueue mq) {
        this.mq = mq;
        this.mq.addHandler("accounts.registerCustomer.response", this::handleResponse);
        this.mq.addHandler("accounts.getCustomer.response", this::handleResponse);
        this.mq.addHandler("reports.customer.response", this::handleResponse);

        this.mq.addHandler("tokens.register.response", this::handleResponse);
    }
    

    public void handleResponse(Event event) {
        String correlationId = event.getArgument(1, String.class);

        CompletableFuture<Event> future = pendingRequests.remove(correlationId);

        if (future != null) {
            future.complete(event);
        } else {
            LOG.warn("Received response for unknown request ID: " + correlationId);
        }
    }

    public Customer registerCustomer(Customer customer) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("facade.customer.register", new Object[] { customer, correlationId }));

        Event resultEvent = future.join();

        return resultEvent.getArgument(0, Customer.class);
    }

    public Customer getCustomer(String customerId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("facade.customer.request", new Object[] { customerId, correlationId }));

        Event resultEvent = future.join();

        return resultEvent.getArgument(0, Customer.class);
    }

    public List<CustomerTransaction> getTransactionsForCustomer(String customerId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("facade.customerreport.request", new Object[] { customerId, correlationId }));

        Event resultEvent = future.join();

        CustomerTransaction[] array = resultEvent.getArgument(0, CustomerTransaction[].class);

        return Arrays.asList(array);
    }

    public void deleteCustomer(String customerId) {
        mq.publish(new Event("facade.customer.delete", new Object[] { customerId }));
    }


    public List<String> createTokens(String customerId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("facade.tokens.register", new Object[] { customerId, correlationId }));

        Event resultEvent = future.join();

        String[] tokens = resultEvent.getArgument(0, String[].class);
        return Arrays.asList(tokens);
    }
}