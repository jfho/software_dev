package dtu.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

import dtu.adapters.BankClientInterface;
import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.PendingTransaction;
import dtu.models.Transaction;

public class PaymentService {
    MessageQueue mq;
    BankClientInterface bankClient;

    private Map<String, PendingTransaction> pendingTransactions = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger(PaymentService.class);

    private final String PAYMENTS_REGISTER_REQ_RK = "PaymentRequested";
    private final String PAYMENTS_REGISTER_RES_RK = "MoneyTransferFinished";

    private final String BANKACCOUNT_CUSTOMER_RES_RK = "CustomerBankAccountRetrieved";
    private final String BANKACCOUNT_MERCHANT_RES_RK = "MerchantBankAccountRetrieved";

    public PaymentService(MessageQueue mq, BankClientInterface bankClient) {
        this.mq = mq;
        this.bankClient = bankClient;

        this.mq.addHandler(PAYMENTS_REGISTER_REQ_RK, this::handlePayment);
        this.mq.addHandler(BANKACCOUNT_CUSTOMER_RES_RK, this::handleCustomerBankRetreived);
        this.mq.addHandler(BANKACCOUNT_MERCHANT_RES_RK, this::handleMerchantBankRetreived);
    }

    public void handlePayment(Event event) {
        Transaction receivedTransaction = event.getArgument(0, Transaction.class);
        String correlationId = event.getArgument(1, String.class);

        LOG.info("Payment started for correlationId: " + correlationId);

        pendingTransactions.compute(correlationId, (id, transaction) -> {
            if (transaction == null) {
                transaction = new PendingTransaction();
            }
            transaction.amount = receivedTransaction.amount();
            return transaction;
        });

        evaluatePending(correlationId);
    }

    public void handleCustomerBankRetreived(Event event) {
        String customerId = event.getArgument(0, String.class);
        String correlationId = event.getArgument(1, String.class);

        LOG.info("Customer bank info received for correlationId: " + correlationId);

        pendingTransactions.compute(correlationId, (id, transaction) -> {
            if (transaction == null) {
                transaction = new PendingTransaction();
            }
            transaction.bankCusId = customerId;
            return transaction;
        });

        evaluatePending(correlationId);
    }

    public void handleMerchantBankRetreived(Event event) {
        String merchantId = event.getArgument(0, String.class);
        String correlationId = event.getArgument(1, String.class);

        LOG.info("Merchant bank info received for correlationId: " + correlationId);

        pendingTransactions.compute(correlationId, (id, transaction) -> {
            if (transaction == null) {
                transaction = new PendingTransaction();
            }
            transaction.bankMerId = merchantId;
            return transaction;
        });

        evaluatePending(correlationId);
    }

    private void evaluatePending(String correlationId) {
        PendingTransaction transaction = pendingTransactions.get(correlationId);
        if (transaction == null ||
                transaction.bankCusId == null ||
                transaction.bankMerId == null ||
                transaction.amount == null) {
            return;
        }

        LOG.info("All info present. Initiating bank transfer for correlationId: " + correlationId);
        boolean transferSuccessful = bankClient.transfer(transaction.bankCusId, transaction.bankMerId,
                transaction.amount);

        if (transferSuccessful) {
            LOG.info("Transfer successful for correlationId: " + correlationId);
            mq.publish(new Event(PAYMENTS_REGISTER_RES_RK, new Object[] { true, correlationId }));
        } else {
            LOG.warn("Transaction failed (or incomplete data) for correlationId: " + correlationId);
            mq.publish(new Event(PAYMENTS_REGISTER_RES_RK, new Object[] { false, correlationId }));
        }

        pendingTransactions.remove(correlationId);
    }
}