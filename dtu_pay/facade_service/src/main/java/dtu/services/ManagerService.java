/**
 * @author s215698
 */

package dtu.services;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Transaction;

import org.jboss.logging.Logger;

public class ManagerService {
    MessageQueue mq;

    private Map<String, CompletableFuture<Event>> pendingRequests = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger(ManagerService.class);

    public ManagerService(MessageQueue mq) {
        this.mq = mq;
        this.mq.addHandler("ManagerReportFetched", this::handleResponse);
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

    public List<Transaction> getAllTransactions() {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("ManagerReportRequested", new Object[] { correlationId }));

        Event resultEvent;
        try {
            resultEvent = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            LOG.error("Failed to get manager transactions: " + e.getMessage());
            pendingRequests.remove(correlationId);
            throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal reporting service unavailable.")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }

        Transaction[] array = resultEvent.getArgument(0, Transaction[].class);

        return Arrays.asList(array);
    }

}