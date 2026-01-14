package dtu;

import java.util.concurrent.CompletableFuture;

import dtu.Adapters.BankClientInterface;
import dtu.Adapters.Event;
import dtu.Adapters.MessageQueue;
import dtu.Models.Transaction;

public class PaymentService {
    MessageQueue mq;
    BankClientInterface bankClient;

    private CompletableFuture<String> customerIdFuture;
    private CompletableFuture<String> customerBankIdFuture;
    private CompletableFuture<String> merchantBankIdFuture;

    public PaymentService(MessageQueue mq, BankClientInterface bankClient) {
        this.mq = mq;
        this.bankClient = bankClient;

        mq.addHandler("token.customerid.response", event -> {
            if (customerIdFuture != null) {
                String id = event.getArgument(0, String.class);
                customerIdFuture.complete(id);
            }
        });

        mq.addHandler("account.customerbankaccount.response", event -> {
            if (customerBankIdFuture != null) {
                String id = event.getArgument(0, String.class);
                customerBankIdFuture.complete(id);
            }
        });

        mq.addHandler("account.merchantbankaccount.response", event -> {
            if (merchantBankIdFuture != null) {
                String id = event.getArgument(0, String.class);
                merchantBankIdFuture.complete(id);
            }
        });
    }

    private String getCustomerIdFromToken(String tokenId) throws Exception {
        // 2. passes the token to the token manager done
        customerIdFuture = new CompletableFuture<>();
        Event event = new Event("payments.customerid.request", new Object[] { tokenId });
        mq.publish(event);

        // 3. consumes the customer id from the token manager done
        String customerId = customerIdFuture.join();

        // 4. if not null, send the customer id and the merchant id to the account done
        if (customerId.isEmpty()) {
            throw new Exception("Invalid token");
        }
        return customerId;
    }

    public String getBankAccountIdById(String dtuPayId, String routingKey) throws Exception {
        CompletableFuture<String> bankIdFuture = new CompletableFuture<>();

        Event event = new Event("payments." + routingKey + ".request", new Object[] { dtuPayId });
        mq.publish(event);

        if (routingKey.equals("customerbankaccount")) {
            this.customerBankIdFuture = bankIdFuture;
        } else if (routingKey.equals("merchantbankaccount")) {
            this.merchantBankIdFuture = bankIdFuture;
        }

        // 5. consumes the bank account if not null
        String bankAccountId = bankIdFuture.join();

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

        if (customerBankAccountId.isEmpty() || merchantBankAccountId.isEmpty()) {
            throw new Exception("Unknown customer or merchant");
        }

        // 6. passes the bank account to the ba nk service along with the amount to
        // transfer
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