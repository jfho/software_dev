package dtu.services;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

import dtu.adapters.BankClientInterface;
import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Transaction;

public class PaymentService {
    MessageQueue mq;
    BankClientInterface bankClient;

    private Map<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger(PaymentService.class);

    public PaymentService(MessageQueue mq, BankClientInterface bankClient) {
        this.mq = mq;
        this.bankClient = bankClient;

        this.mq.addHandler("facade.payments.register", this::handleRegistration);
        this.mq.addHandler("tokens.customerid.response", this::handleResponse);
        this.mq.addHandler("accounts.customerbankaccount.response", this::handleResponse);
        this.mq.addHandler("accounts.merchantbankaccount.response", this::handleResponse);
    }

    public void handleRegistration(Event event) {
        Transaction transaction = event.getArgument(0, Transaction.class);
        try {
            registerTransaction(transaction);
        } catch (Exception e) {
            LOG.warn("Registering transaction failed, error " + e.getMessage());
        }
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

    private String getCustomerIdFromToken(String tokenId) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        Event event = new Event("payments.customerid.request", new Object[] { tokenId, correlationId });
        mq.publish(event);

        // 3. consumes the customer id from the token manager done
        String customerId = future.join();

        // 4. if not null, send the customer id and the merchant id to the account done
        if (customerId == null || customerId.isEmpty()) {
            throw new Exception("Invalid token");
        }
        return customerId;
    }

    public String getBankAccountIdById(String dtuPayId, String routingKey) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        Event event = new Event("payments." + routingKey + ".request", new Object[] { dtuPayId, correlationId });
        mq.publish(event);

        // 5. consumes the bank account if not null
        String bankAccountId = future.join();

        if (bankAccountId == null || bankAccountId.isEmpty()) {
            throw new Exception("Unknown customer or merchant");
        }
        return bankAccountId;
    }

    // 1. consumes the token and merchant id and amount done
    public void registerTransaction(Transaction transaction) throws Exception {
        LOG.info("Getting customerId from token");
        String customerId = getCustomerIdFromToken(transaction.tokenId());
        LOG.info("Received: " + customerId);

        LOG.info("Getting customer bank account id");
        String customerBankAccountId = getBankAccountIdById(customerId, "customerbankaccount");
        LOG.info("Received: " + customerBankAccountId);
        LOG.info("Getting merchant bank account id");
        String merchantBankAccountId = getBankAccountIdById(transaction.merchantId(), "merchantbankaccount");
        LOG.info("Received: " + merchantBankAccountId);

        boolean transferSuccessful = bankClient.transfer(customerBankAccountId, merchantBankAccountId,
                transaction.amount());

        LOG.info("Transfer successful? " + transferSuccessful);

        if (transferSuccessful) {
            LOG.info("Emitting event");
            mq.publish(new Event("payments.transaction.report",
                    new Object[] { customerId, transaction.merchantId(), transaction.amount().toString() }));
            mq.publish(new Event("payments.transaction.status", new Object[] { "Bank transaction successful" }));
        } else {
            mq.publish(new Event("payments.transaction.status", new Object[] { "Bank transaction failed" }));
        }
    }
}