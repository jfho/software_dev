package dtu;

import dtu.Adapters.BankClientInterface;
import dtu.Adapters.MessageQueue;
import dtu.Models.Transaction;

public class PaymentService {
    MessageQueue mq;
    BankClientInterface bankClient;

    public PaymentService(MessageQueue mq, BankClientInterface bankClient) {
        this.mq = mq;
        this.bankClient = bankClient;
    }

    private String getCustomerIdFromToken(String tokenId) throws Exception {
        // 2. passes the token to the token manager done
        mq.produce(tokenId, "payments.customerid.request");

        // 3. consumes the customer id from the token manager done
        String customerId = mq.consume("token.customerid.response");

        // 4. if not null, send the customer id and the merchant id to the account done
        if (customerId.isEmpty()) {
            throw new Exception("Invalid token");
        }
        return customerId;
    }

    public String getBankAccountIdById(String Id, String routingKey) throws Exception {
        mq.produce(Id, "payments." + routingKey + ".request");

        // 5. consumes the bank account if not null
        String bankAccountId = mq.consume("account." + routingKey + ".response");

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

        // 6. passes the bank account to the ba  nk service along with the amount to
        // transfer
        boolean transferSuccessful = bankClient.transfer(customerBankAccountId, merchantBankAccountId,
                transaction.amount());

        if (transferSuccessful) {
            // 7. send the transaction to the reporting service
            String reportMessage = customerId + "," + transaction.merchantId() + ","
                    + transaction.amount().toString();
            mq.produce(reportMessage, "payments.transaction.report");
            mq.produce("Transaction successful", "payments.transaction.status");
        } else {
            mq.produce("Bank transaction failed", "payments.transaction.status");
        }
    }
}