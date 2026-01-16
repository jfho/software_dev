package dtu;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

import dtu.messagingUtils.MessageQueue;
import dtu.models.Transaction;

import org.jboss.logging.Logger;



public class ManagerService {
    MessageQueue mq;
    public ManagerService(MessageQueue mq) {
        this.mq = mq;



    }
    public void handleResponse(Event event) {
        String result = event.getArgument(0, String.class);
        String correlationId = event.getArgument(1, String.class);

        CompletableFuture<String> future = pendingRequests.remove(correlationId);

        if (future != null) {
            future.complete(result);
        } else {
            throw new RuntimeException();
        }
    }

    public List<Transaction> getAllTransactions() {
        return db.listPayments();
    }

}