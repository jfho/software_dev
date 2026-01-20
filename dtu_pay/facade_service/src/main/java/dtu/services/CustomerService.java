package dtu.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

import dtu.models.CustomerTransaction;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Customer;

public class CustomerService {
    MessageQueue mq;

    private Map<String, CompletableFuture<Event>> pendingRequests = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger(CustomerService.class);

    private final String REGISTER_CUSTOMER_RES_RK = "facade.registerCustomer.response";
    private final String GET_CUSTOMER_RES_RK = "facade.getCustomer.response";
    private final String CUSTOMER_REPORT_RES_RK = "reports.customerreport.response";
    private final String DELETE_CUSTOMER_RES_RK = "facade.deleteCustomer.response";
    private final String TOKENS_REGISTER_RES_RK = "tokens.createtokens.response";

    private final String REGISTER_CUSTOMER_REQ_RK = "facade.registerCustomer.request";
    private final String GET_CUSTOMER_REQ_RK = "facade.getCustomer.request";
    private final String CUSTOMER_REPORT_REQ_RK = "facade.customerreport.request";
    private final String DELETE_CUSTOMER_REQ_RK = "facade.deleteCustomer.request";
    private final String TOKENS_REGISTER_REQ_RK = "facade.createtokens.request";

    public CustomerService(MessageQueue mq) {
        this.mq = mq;
        this.mq.addHandler(REGISTER_CUSTOMER_RES_RK, this::handleResponse);
        this.mq.addHandler(GET_CUSTOMER_RES_RK, this::handleResponse);
        this.mq.addHandler(CUSTOMER_REPORT_RES_RK, this::handleResponse);
        this.mq.addHandler(DELETE_CUSTOMER_RES_RK, this::handleResponse);
        this.mq.addHandler(TOKENS_REGISTER_RES_RK, this::handleResponse);
    }

    public void handleResponse(Event event) {
        String correlationId = event.getArgument(1, String.class);
        LOG.info("Received response event: " + event.getType() + " with CorrelationID: " + correlationId);

        CompletableFuture<Event> future = pendingRequests.remove(correlationId);

        if (future != null) {
            future.complete(event);
        } else {
            LOG.warn("Received response for unknown request ID: " + correlationId);
        }
    }

    public Customer registerCustomer(Customer customer) {
        LOG.info("Requesting registration for customer: " + customer);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(REGISTER_CUSTOMER_REQ_RK, new Object[] { customer, correlationId }));

        Event resultEvent = future.join();

        Customer result = resultEvent.getArgument(0, Customer.class);
        LOG.info("Customer registration successful. Assigned ID: " + (result != null ? result.dtupayUuid() : "null"));

        if (result == null) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                .entity("customer registration failed")
                .type(MediaType.TEXT_PLAIN)
                .build()
            );
        }

        return result;
    }

    public Customer getCustomer(String customerId) {
        LOG.info("Fetching details for customer ID: " + customerId);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(GET_CUSTOMER_REQ_RK, new Object[] { customerId, correlationId }));

        Event resultEvent = future.join();
        LOG.info("Details retrieved for customer ID: " + customerId);

        return resultEvent.getArgument(0, Customer.class);
    }

    public List<CustomerTransaction> getTransactionsForCustomer(String customerId) {
        LOG.info("Requesting transaction report for customer ID: " + customerId);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(CUSTOMER_REPORT_REQ_RK, new Object[] { customerId, correlationId }));

        Event resultEvent = future.join();

        CustomerTransaction[] array = resultEvent.getArgument(0, CustomerTransaction[].class);
        LOG.info("Report received for customer ID: " + customerId + ". Transactions found: "
                + (array != null ? array.length : 0));

        return Arrays.asList(array);
    }

    public boolean deleteCustomer(String customerId) {
        LOG.info("Requesting deletion for customer ID: " + customerId);
        
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(DELETE_CUSTOMER_REQ_RK, new Object[] { customerId, correlationId }));
        
        Event resultEvent = future.join();
        boolean success = resultEvent.getArgument(0, Boolean.class);
        LOG.info("received: success = " + true);

        return success;
    }

    public List<String> createTokens(String customerId, int amount) {
        LOG.info("Requesting new tokens for customer ID: " + customerId);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(TOKENS_REGISTER_REQ_RK, new Object[] { customerId, amount, correlationId }));

        Event resultEvent = future.join();

        String[] tokens = resultEvent.getArgument(0, String[].class);
        LOG.info("Tokens generated successfully. Count: " + (tokens != null ? tokens.length : 0));

        if (tokens.length == 0) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                .entity("invalid token request")
                .type(MediaType.TEXT_PLAIN)
                .build()
            );
        }

        return Arrays.asList(tokens);
    }
}