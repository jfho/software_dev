package dtu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import dtu.Adapters.BankClientInterface;
import dtu.Adapters.Event;
import dtu.Adapters.MessageQueue;
import dtu.Models.Transaction;

public class PaymentService {
    MessageQueue mq;
    BankClientInterface bankClient;

    private Map<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    public PaymentService(MessageQueue mq, BankClientInterface bankClient) {
        this.mq = mq;
        this.bankClient = bankClient;

        this.mq.addHandler("token.customerid.response", this::handleResponse);
        this.mq.addHandler("account.customerbankaccount.response", this::handleResponse);
        this.mq.addHandler("account.merchantbankaccount.response", this::handleResponse);
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
        if (customerId.isEmpty()) {
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

        if (bankAccountId.isEmpty()) {
            throw new Exception("Unknown customer or merchant");
        }
        return bankAccountId;
    }

    // 1. consumes the token and merchant id and amount done
    public void registerTransaction(Transaction transaction) throws Exception {
        String customerId = getCustomerIdFromToken(transaction.tokenId());

        String customerBankAccountId = getBankAccountIdById(customerId, "customerbankaccount");
        String merchantBankAccountId = getBankAccountIdById(transaction.merchantId(), "merchantbankaccount");

        boolean transferSuccessful = bankClient.transfer(customerBankAccountId, merchantBankAccountId,
                transaction.amount());

        if (transferSuccessful) {
            // 7. send the transaction to the reporting service
            String reportMessage = customerId + "," + transaction.merchantId() + "," + transaction.amount().toString();
            mq.publish(new Event("payments.transaction.report", new Object[] { reportMessage }));
            mq.publish(new Event("payments.transaction.status", new Object[] { "Bank transaction succesful" }));
        } else {
            mq.publish(new Event("payments.transaction.status", new Object[] { "Bank transaction failed" }));
        }
    }
}