package dtu.services;

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
import dtu.models.Transaction;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class MerchantService {
    MessageQueue mq;

    private Map<String, CompletableFuture<Event>> pendingRequests = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger(MerchantService.class);

    private final String REGISTER_MERCHANT_REQ_RK = "facade.registerMerchant.request";
    private final String REGISTER_MERCHANT_RES_RK = "facade.registerMerchant.response";

    private final String GET_MERCHANT_REQ_RK = "facade.getMerchant.request";
    private final String GET_MERCHANT_RES_RK = "facade.getMerchant.response";

    private final String MERCHANT_REPORT_REQ_RK = "facade.merchantreport.request";
    private final String MERCHANT_REPORT_RES_RK = "reports.merchantreport.response";

    private final String DELETE_MERCHANT_REQ_RK = "facade.deleteMerchant.request";
    private final String DELETE_MERCHANT_RES_RK = "facade.deleteMerchant.response";

    private final String PAYMENTS_REGISTER_REQ_RK = "facade.transaction.request";
    private final String PAYMENTS_REGISTER_RES_RK = "payments.transaction.response";

    public MerchantService(MessageQueue mq) {
        this.mq = mq;
        this.mq.addHandler(REGISTER_MERCHANT_RES_RK, this::handleResponse);
        this.mq.addHandler(GET_MERCHANT_RES_RK, this::handleResponse);
        this.mq.addHandler(DELETE_MERCHANT_RES_RK, this::handleResponse);
        this.mq.addHandler(MERCHANT_REPORT_RES_RK, this::handleResponse);
        this.mq.addHandler(PAYMENTS_REGISTER_RES_RK, this::handleResponse);
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

        mq.publish(new Event(REGISTER_MERCHANT_REQ_RK, new Object[] { merchant, correlationId }));

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

        mq.publish(new Event(GET_MERCHANT_REQ_RK, new Object[] { merchantId, correlationId }));

        Event resultEvent = future.join();
        LOG.info("Details retrieved for merchant ID: " + merchantId);

        return resultEvent.getArgument(0, Merchant.class);
    }

    public List<MerchantTransaction> getTransactionsForMerchant(String merchantId) {
        LOG.info("Requesting transaction report for merchant ID: " + merchantId);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(MERCHANT_REPORT_REQ_RK, new Object[] { merchantId, correlationId }));

        Event resultEvent = future.join();

        MerchantTransaction[] array = resultEvent.getArgument(0, MerchantTransaction[].class);

        LOG.info("Report received for merchant ID: " + merchantId + ". Transactions found: "
                + (array != null ? array.length : 0));

        return Arrays.asList(array);
    }

    public boolean deleteMerchant(String merchantId) {
        LOG.info("Requesting deletion for merchant ID: " + merchantId);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(DELETE_MERCHANT_REQ_RK, new Object[] { merchantId, correlationId }));

        Event resultEvent = future.join();
        boolean success = resultEvent.getArgument(0, Boolean.class);
        LOG.info("received: success = " + true);

        return success;
    }

    public boolean registerTransaction(MerchantTransaction transaction) {
        LOG.info("Registering new transaction: " + transaction);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(PAYMENTS_REGISTER_REQ_RK, new Object[] { transaction, correlationId }));

        Event resultEvent = future.join();
        Transaction resultTransaction = resultEvent.getArgument(0, Transaction.class);
        if (resultTransaction == null) {
            LOG.warn("Transaction failed!");
            return false;
        } else {
            LOG.info("Transaction succeeded!");
            return true;
        }
    }
}