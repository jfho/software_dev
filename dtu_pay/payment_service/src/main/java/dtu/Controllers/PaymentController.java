package dtu.Controllers;

import dtu.Models.Transaction;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankService_Service;

public class PaymentController {
    private static final PaymentController INSTANCE = new PaymentController();
    RabbitMq rabbitmqClient = RabbitMq.getInstance();

    public PaymentController() {
    }

    public static PaymentController getInstance() {
        return INSTANCE;
    }

    BankService_Service service = new BankService_Service();
    BankService bank = service.getBankServicePort();

    private String getCustomerIdFromToken(String tokenId) throws Exception {
        // 2. passes the token to the token manager done
        rabbitmqClient.produce(tokenId, "payments.customerid.request");

        // 3. consumes the customer id from the token manager done
        String customerId = rabbitmqClient.consume("token.customerid.response");

        // 4. if not null, send the customer id and the merchant id to the account done
        if (customerId.isEmpty()) {
            throw new Exception("Invalid token");
        }
        return customerId;
    }

    public String getBankAccountIdById(String Id, String routingKey) throws Exception {
        rabbitmqClient.produce(Id, "payments." + routingKey + ".request");

        // 5. consumes the bank account if not null
        String bankAccountId = rabbitmqClient.consume("account." + routingKey + ".response");

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

        // 6. passes the bank account to the bank service along with the amount to
        // transfer
        try {
            bank.transferMoneyFromTo(customerBankAccountId, merchantBankAccountId, transaction.payment(),
                    "Ordinary transfer");
        } catch (BankServiceException_Exception e) {
            rabbitmqClient.produce("Bank transaction failed", "payments.transaction.status");
            // throw new Exception("Bank transaction failed: " + e.getMessage());
        }

        // 7. send the transaction to the reporting service
        String reportMessage = customerId + "," + transaction.merchantId() + ","
                + transaction.payment().toString();
        rabbitmqClient.produce(reportMessage, "payments.transaction.report");
        rabbitmqClient.produce("Transaction successful", "payments.transaction.status");
    }
}