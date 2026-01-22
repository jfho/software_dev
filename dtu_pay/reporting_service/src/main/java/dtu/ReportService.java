/**
 * @author s253874
 */

package dtu;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Database;
import dtu.models.MerchantTransaction;
import dtu.models.RecordedPayment;
import dtu.util.PaymentConverterMerchant;

public class ReportService {
    MessageQueue queue;
    Database db = Database.getInstance();

    private Map<String, RecordedPayment> pendingRecords = new ConcurrentHashMap<>();

    private String TRANSACTION_COMPLETED_RK = "MoneyTransferFinished";
    private String PAYMENT_REQUESTED = "PaymentRequested";
    private String TOKEN_VALIDATED = "TokenValidated";
    private String MERCHANT_GETTRANSACTIONS_REQ = "MerchantReportRequested";
    private String CUSTOMER_GETTRANSACTIONS_REQ = "CustomerReportRequested";
    private String MANAGER_GETTRANSACTIONS_REQ = "ManagerReportRequested";

    private String MERCHANT_GETTRANSACTIONS_RES = "MerchantReportFetched";
    private String CUSTOMER_GETTRANSACTIONS_RES = "CustomerReportFetched";
    private String MANAGER_GETTRANSACTIONS_RES = "ManagerReportFetched";

    private static final Logger LOG = Logger.getLogger(ReportService.class);

    public ReportService(MessageQueue q) {
        queue = q;

        queue.addHandler(MERCHANT_GETTRANSACTIONS_REQ, e -> {
            LOG.info("Received a merchant request");
            String merchantId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);
            LOG.info("merchantId: " + merchantId);

            List<MerchantTransaction> transactionList = getTransactionsForMerchant(merchantId);
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


        queue.addHandler(PAYMENT_REQUESTED, e -> {

            RecordedPayment pendingRecord = e.getArgument(0, RecordedPayment.class);
            String corrId = e.getArgument(1, String.class);


            pendingRecords.compute(corrId, (id, trans) -> {

                if (trans == null) {
                    String timestamp = Instant.now().toString();
                    String transactionId = UUID.randomUUID().toString();

                    trans = new RecordedPayment(
                        null, 
                        pendingRecord.merchantId, 
                        pendingRecord.amount, 
                        pendingRecord.tokenId, 
                        transactionId, 
                        timestamp, 
                        null);
                } else {
                    trans.amount = pendingRecord.amount;
                    trans.merchantId = pendingRecord.merchantId;
                    trans.tokenId = pendingRecord.tokenId;
                }
            
                return trans;
            });

            evaluatePending(corrId);
        });

        queue.addHandler(TRANSACTION_COMPLETED_RK, e -> {

            Boolean transferSuccessful = e.getArgument(0, Boolean.class);
            String corrId = e.getArgument(1, String.class);


            pendingRecords.compute(corrId, (id, trans) -> {

                if (trans == null) {
                    String timestamp = Instant.now().toString();
                    String transactionId = UUID.randomUUID().toString();

                    trans = new RecordedPayment(
                        null, 
                        null, 
                        null, 
                        null, 
                        transactionId, 
                        timestamp, 
                        transferSuccessful);
                } else {
                    trans.successful = transferSuccessful;
                }
            
                return trans;
            });
            evaluatePending(corrId);

        });

        queue.addHandler(TOKEN_VALIDATED, e -> {
            String customerId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);

            pendingRecords.compute(corrId, (id, trans) -> {

                if (trans == null) {
                    String timestamp = Instant.now().toString();
                    String transactionId = UUID.randomUUID().toString();

                    trans = new RecordedPayment(
                        customerId, 
                        null, 
                        null, 
                        null, 
                        transactionId, 
                        timestamp, 
                        null);
                } else {
                    trans.customerId = customerId;
                }
            
                return trans;
            });
            evaluatePending(corrId);
        });
    }

    private synchronized void evaluatePending(String corrId) {

        pendingRecords.computeIfPresent(corrId, (id, trans) ->{

            if (trans.customerId != null &&
                trans.merchantId != null &&
                trans.amount != null &&
                trans.tokenId != null &&
                trans.transactionId != null &&
                trans.timestamp != null &&
                trans.successful != null
            ) {
                // Remove finished record, only add to db if successful
                if (trans.successful) {
                    db.addPayment(trans);                    
                } 
                return null;
            }    
            return trans;
        });

    }

    public void clearPendingPayments() {
        pendingRecords.clear();
    }

    public List<RecordedPayment> getAllTransactions() {
        return db.listPayments();
    }

    public List<RecordedPayment> getTransactionsForCustomer(String customerId) {
        List<RecordedPayment> payments = db.listPayments();
        return payments.stream().filter(p -> p.customerId.equals(customerId)).toList();
    }

    public List<MerchantTransaction> getTransactionsForMerchant(String merchantId) {
        List<RecordedPayment> payments = db.listPayments();
        List<MerchantTransaction> merchantTransactions = PaymentConverterMerchant.toMerchantTransactions(payments);
        return merchantTransactions.stream().filter(p -> p.merchantId().equals(merchantId)).toList();
    }
}