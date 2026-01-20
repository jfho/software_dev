package dtu;

import java.util.List;
import java.util.UUID;

import org.jboss.logging.Logger;

import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Database;
import dtu.models.RecordedPayment;

public class ReportService {
    MessageQueue queue;
    Database db = Database.getInstance();

    private String TRANSACTION_COMPLETED_RK = "MoneyTransferFinished";
    private String MERCHANT_GETTRANSACTIONS_REQ = "facade.merchantreport.request";
    private String CUSTOMER_GETTRANSACTIONS_REQ = "facade.customerreport.request";
    private String MANAGER_GETTRANSACTIONS_REQ = "facade.managerreport.request";

    private String MERCHANT_GETTRANSACTIONS_RES = "reports.merchantreport.response";
    private String CUSTOMER_GETTRANSACTIONS_RES = "reports.customerreport.response";
    private String MANAGER_GETTRANSACTIONS_RES = "reports.managerreport.response";

    private static final Logger LOG = Logger.getLogger(ReportService.class);

    public ReportService(MessageQueue q) {
        queue = q;

        queue.addHandler(MERCHANT_GETTRANSACTIONS_REQ, e -> {
            LOG.info("Received a merchant request");
            String merchantId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);
            LOG.info("merchantId: " + merchantId);

            List<RecordedPayment> transactionList = getTransactionsForMerchant(merchantId);
            queue.publish(new Event(MERCHANT_GETTRANSACTIONS_RES, new Object[] { transactionList, corrId }));

        });

        queue.addHandler(CUSTOMER_GETTRANSACTIONS_REQ, e -> {
            LOG.info("Received a customer request");
            String customerId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);
            LOG.info("customerId: " + customerId);

            List<RecordedPayment> transactionList = getTransactionsForCustomer(customerId);
            queue.publish(new Event(CUSTOMER_GETTRANSACTIONS_RES, new Object[] { transactionList, corrId }));

        });

        queue.addHandler(MANAGER_GETTRANSACTIONS_REQ, e -> {
            LOG.info("Received a manager request");
            String corrId = e.getArgument(0, String.class);

            List<RecordedPayment> transactionList = getAllTransactions();
            queue.publish(new Event(MANAGER_GETTRANSACTIONS_RES, new Object[] { transactionList, corrId }));

        });

        queue.addHandler(TRANSACTION_COMPLETED_RK, e -> {
            LOG.info("Received a transaction report");
            RecordedPayment receivedPayment = e.getArgument(0, RecordedPayment.class);

            if (receivedPayment == null) {
                // failed transaction - nothing to save
                LOG.info("Ignoring failed transaction");
                return;
            }

            String transactionId = UUID.randomUUID().toString();
            RecordedPayment payment = new RecordedPayment(receivedPayment.customerId(), receivedPayment.merchantId(),
                    receivedPayment.amount(), receivedPayment.tokenId(), transactionId);

            LOG.info("customerId: " + payment.customerId() + ", merchantId: " + payment.merchantId() + ", amount: "
                    + payment.amount());

            db.addPayment(payment);
        });
    }

    public List<RecordedPayment> getAllTransactions() {
        return db.listPayments();
    }

    public List<RecordedPayment> getTransactionsForCustomer(String customerId) {
        List<RecordedPayment> payments = db.listPayments();
        return payments.stream().filter(p -> p.customerId().equals(customerId)).toList();
    }

    public List<RecordedPayment> getTransactionsForMerchant(String merchantId) {
        List<RecordedPayment> payments = db.listPayments();
        return payments.stream().filter(p -> p.merchantId().equals(merchantId)).toList();
    }
}