package dtu;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Merchant;
import dtu.models.MerchantTransaction;

public class MerchantService {
    MessageQueue mq;

    private Map<String, CompletableFuture<Event>> pendingRequests = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger(MerchantService.class);

    public MerchantService(MessageQueue mq) {
        this.mq = mq;
        this.mq.addHandler("accounts.registerMerchant.response", this::handleResponse);
        this.mq.addHandler("accounts.getMerchant.response", this::handleResponse);
        this.mq.addHandler("reports.merchant.response", this::handleResponse);
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

    public Merchant registerMerchant(Merchant merchant) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("facade.merchant.register", new Object[] { merchant, correlationId }));

        Event resultEvent = future.join();

        return resultEvent.getArgument(0, Merchant.class);
    }

    public Merchant getMerchant(String merchantId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("facade.merchant.request", new Object[] { merchantId, correlationId }));

        Event resultEvent = future.join();

        return resultEvent.getArgument(0, Merchant.class);
    }

    public List<MerchantTransaction> getTransactionsForMerchant(String merchantId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("facade.merchantreport.request", new Object[] { merchantId, correlationId }));

        Event resultEvent = future.join();

        MerchantTransaction[] array = resultEvent.getArgument(0, MerchantTransaction[].class);

        return Arrays.asList(array);
    }

    public void deleteMerchant(String merchantId) {
        mq.publish(new Event("facade.merchant.delete", new Object[] { merchantId }));
    }

    public void registerTransaction(MerchantTransaction transaction) {
        mq.publish(new Event("payments.payment.register", new Object[] { transaction }));
    }
}