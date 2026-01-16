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
        this.mq.addHandler("facade.registerMerchant.response", this::handleResponse);
        this.mq.addHandler("facade.getMerchant.response", this::handleResponse);
        this.mq.addHandler("reports.merchant.response", this::handleResponse);
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

    public Merchant registerMerchant(Merchant merchant) {
        LOG.info("Requesting registration for merchant: " + merchant);
        
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("facade.registerMerchant.request", new Object[] { merchant, correlationId }));

        Event resultEvent = future.join();
        Merchant result = resultEvent.getArgument(0, Merchant.class);

        LOG.info("Merchant registration successful. Assigned ID: " + (result != null ? result.dtupayUuid() : "null"));

        return result;
    }

    public Merchant getMerchant(String merchantId) {
        LOG.info("Fetching details for merchant ID: " + merchantId);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("facade.getMerchant.request", new Object[] { merchantId, correlationId }));

        Event resultEvent = future.join();
        LOG.info("Details retrieved for merchant ID: " + merchantId);

        return resultEvent.getArgument(0, Merchant.class);
    }

    public List<MerchantTransaction> getTransactionsForMerchant(String merchantId) {
        LOG.info("Requesting transaction report for merchant ID: " + merchantId);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event("facade.merchantreport.request", new Object[] { merchantId, correlationId }));

        Event resultEvent = future.join();

        MerchantTransaction[] array = resultEvent.getArgument(0, MerchantTransaction[].class);
        
        LOG.info("Report received for merchant ID: " + merchantId + ". Transactions found: " + (array != null ? array.length : 0));

        return Arrays.asList(array);
    }

    public void deleteMerchant(String merchantId) {
        LOG.info("Requesting deletion for merchant ID: " + merchantId);
        mq.publish(new Event("facade.deleteMerchant.request", new Object[] { merchantId }));
    }

    public void registerTransaction(MerchantTransaction transaction) {
        LOG.info("Registering new transaction: " + transaction);
        mq.publish(new Event("facade.payments.register", new Object[] { transaction }));
    }
}