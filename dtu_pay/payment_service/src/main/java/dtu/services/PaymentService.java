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

    private final String PAYMENTS_REGISTER_REQ_RK = "facade.transaction.register";
    private final String PAYMENTS_REGISTER_RES_RK = "payments.transaction.status";

    private final String TOKEN_CUSTOMERID_REQ_RK = "payments.customerid.request";
    private final String TOKEN_CUSTOMERID_RES_RK = "tokens.customerid.response";

    private final String BANKACCOUNT_CUSTOMER_REQ_RK = "payments.customerbankaccount.request";
    private final String BANKACCOUNT_MERCHANT_REQ_RK = "payments.merchantbankaccount.request";
    private final String BANKACCOUNT_CUSTOMER_RES_RK = "accounts.customerbankaccount.response";
    private final String BANKACCOUNT_MERCHANT_RES_RK = "accounts.merchantbankaccount.response";

    private final String PAYMENTS_REPORT_REQ_RK = "payments.transaction.report";

    public PaymentService(MessageQueue mq, BankClientInterface bankClient) {
        this.mq = mq;
        this.bankClient = bankClient;

        this.mq.addHandler(PAYMENTS_REGISTER_REQ_RK, this::handleRegistration);
        this.mq.addHandler(TOKEN_CUSTOMERID_RES_RK, this::handleResponse);
        this.mq.addHandler(BANKACCOUNT_CUSTOMER_RES_RK, this::handleResponse);
        this.mq.addHandler(BANKACCOUNT_MERCHANT_RES_RK, this::handleResponse);
    }

    public void handleRegistration(Event event) {
        Transaction transaction = event.getArgument(0, Transaction.class);
        String correlationId = event.getArgument(1, String.class);
        try {
            LOG.info("Processing payment registration for amount: " + transaction.amount());
            registerTransaction(transaction, correlationId);
        } catch (Exception e) {
            LOG.warn("Registering transaction failed: " + e.getMessage());
        }
    }

    public void handleResponse(Event event) {
        String result = event.getArgument(0, String.class);
        String correlationId = event.getArgument(1, String.class);

        CompletableFuture<String> future = pendingRequests.remove(correlationId);

        if (future != null) {
            future.complete(result);
        } else {
            LOG.warn("Received response for unknown request ID: " + correlationId + ". Event Type: " + event.getType());
        }
    }

    private String getCustomerIdFromToken(String tokenId) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(TOKEN_CUSTOMERID_REQ_RK, new Object[] { tokenId, correlationId }));

        String customerId = future.join();

        if (customerId == null || customerId.isEmpty()) {
            throw new Exception("Invalid token: Customer ID could not be retrieved.");
        }
        return customerId;
    }

    public String getBankAccountId(String dtuPayId, String routingKey) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        mq.publish(new Event(routingKey, new Object[] { dtuPayId, correlationId }));

        String bankAccountId = future.join();

        if (bankAccountId == null || bankAccountId.isEmpty()) {
            throw new Exception("Bank Account ID not found for ID: " + dtuPayId);
        }
        return bankAccountId;
    }

    public void registerTransaction(Transaction transaction, String correlationId) throws Exception {
        LOG.info("Step 1: resolving customerId from token...");
        String customerId = getCustomerIdFromToken(transaction.tokenId());
        LOG.info("Resolved CustomerId: " + customerId);

        LOG.info("Step 2: resolving customer bank account...");
        String customerBankAccountId = getBankAccountId(customerId, BANKACCOUNT_CUSTOMER_REQ_RK);
        LOG.info("Resolved Customer Bank Account: " + customerBankAccountId);

        LOG.info("Step 3: resolving merchant bank account...");
        String merchantBankAccountId = getBankAccountId(transaction.merchantId(), BANKACCOUNT_MERCHANT_REQ_RK);
        LOG.info("Resolved Merchant Bank Account: " + merchantBankAccountId);

        LOG.info("Step 4: Executing bank transfer...");
        boolean transferSuccessful = bankClient.transfer(customerBankAccountId, merchantBankAccountId,
                transaction.amount());
        LOG.info("Transfer result: " + transferSuccessful);

        if (transferSuccessful) {
            LOG.info("Transaction complete. Emitting report.");
            mq.publish(new Event(PAYMENTS_REPORT_REQ_RK,
                    new Object[] { customerId, transaction }));
            mq.publish(new Event(PAYMENTS_REGISTER_RES_RK, new Object[] { "Bank transaction successful" }));
        } else {
            LOG.warn("Transaction failed at bank level.");
            mq.publish(new Event(PAYMENTS_REGISTER_RES_RK, new Object[] { "Bank transaction failed" }));
        }
    }
}