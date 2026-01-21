package dtu.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

import dtu.models.Transaction;
import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Customer;

public class CustomerService {
    MessageQueue mq;

    private Map<String, CompletableFuture<Event>> pendingRequests = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger(CustomerService.class);

    private final String REGISTER_CUSTOMER_RES_RK = "CustomerRegistered";
    private final String GET_CUSTOMER_RES_RK = "CustomerFetched";
    private final String CUSTOMER_REPORT_RES_RK = "CustomerReportFetched";
    private final String DELETE_CUSTOMER_RES_RK = "CustomerDeleted";
    private final String TOKENS_REGISTER_RES_RK = "TokensGenerated";

    private final String REGISTER_CUSTOMER_REQ_RK = "CustomerRegistrationRequested";
    private final String GET_CUSTOMER_REQ_RK = "CustomerGetRequested";
    private final String CUSTOMER_REPORT_REQ_RK = "CustomerReportRequested";
    private final String DELETE_CUSTOMER_REQ_RK = "CustomerDeletionRequested";
    private final String TOKENS_REGISTER_REQ_RK = "TokensRequested";

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

        Event resultEvent;
        try {
            resultEvent = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            LOG.error("Failed to register customer: " + e.getMessage());
            pendingRequests.remove(correlationId);
            throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal account service unavailable.")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }

        Customer result = resultEvent.getArgument(0, Customer.class);
        LOG.info("Customer registration successful. Assigned ID: " + (result != null ? result.dtupayUuid() : "null"));

        return result;
    }

    public Customer getCustomer(String customerId) {
        LOG.info("Fetching details for customer ID: " + customerId);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(GET_CUSTOMER_REQ_RK, new Object[] { customerId, correlationId }));

        Event resultEvent;
        try {
            resultEvent = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            LOG.error("Failed to get customer: " + e.getMessage());
            pendingRequests.remove(correlationId);
            throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal account service unavailable.")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
        LOG.info("Details retrieved for customer ID: " + customerId);

        return resultEvent.getArgument(0, Customer.class);
    }

    public List<Transaction> getTransactionsForCustomer(String customerId) {
        LOG.info("Requesting transaction report for customer ID: " + customerId);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(CUSTOMER_REPORT_REQ_RK, new Object[] { customerId, correlationId }));

        Event resultEvent;
        try {
            resultEvent = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            LOG.error("Failed to get customer transactions: " + e.getMessage());
            pendingRequests.remove(correlationId);
            throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal reporting service unavailable.")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }

        Transaction[] array = resultEvent.getArgument(0, Transaction[].class);
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

        Event resultEvent;
        try {
            resultEvent = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            LOG.error("Failed to delete customer: " + e.getMessage());
            pendingRequests.remove(correlationId);
            throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal account service unavailable.")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
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

        Event resultEvent;
        try {
            resultEvent = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            LOG.error("Failed to create tokens: " + e.getMessage());
            pendingRequests.remove(correlationId);
            throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal token service unavailable.")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }

        String[] tokens = resultEvent.getArgument(0, String[].class);
        LOG.info("Tokens generated successfully. Count: " + (tokens != null ? tokens.length : 0));

        return Arrays.asList(tokens);
    }
}