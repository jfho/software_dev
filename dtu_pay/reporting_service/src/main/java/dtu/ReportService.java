package dtu;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import org.jboss.logging.Logger;

import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Customer;
import dtu.models.Database;
import dtu.models.RecordedPayment;
import jakarta.ws.rs.NotFoundException;

public class ReportService {
    MessageQueue queue;
    Database db = Database.getInstance();

    private String TRANSACTION_COMPLETED_RK = "payments.transaction.response";
    private String MERCHANT_GETTRANSACTIONS_REQ = "facade.merchant.request";
    private String CUSTOMER_GETTRANSACTIONS_REQ = "facade.customer.request";
    private String MANAGER_GETTRANSACTIONS_REQ = "facade.manager.request";

    private String MERCHANT_GETTRANSACTIONS_RES = "reports.merchant.response";
    private String CUSTOMER_GETTRANSACTIONS_RES = "reports.customer.response";
    private String MANAGER_GETTRANSACTIONS_RES = "reports.manager.response";

    private static final Logger LOG = Logger.getLogger(ReportService.class);

    public ReportService(MessageQueue q) {
        queue = q;
        
        queue.addHandler(MERCHANT_GETTRANSACTIONS_REQ, e -> {
            LOG.info("Received a merchant request");
            String merchantId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);
            LOG.info("merchantId: " + merchantId);

            List<RecordedPayment> transactionList = getTransactionsForMerchant(merchantId);
            queue.publish(new Event(MERCHANT_GETTRANSACTIONS_RES, new Object[] { transactionList, corrId } ));

		});

        queue.addHandler(CUSTOMER_GETTRANSACTIONS_REQ, e -> {
            LOG.info("Received a customer request");
            String customerId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);
            LOG.info("customerId: " + customerId);

            List<RecordedPayment> transactionList = getTransactionsForCustomer(customerId);
            queue.publish(new Event(CUSTOMER_GETTRANSACTIONS_RES, new Object[] { transactionList, corrId } ));

		});

        queue.addHandler(MANAGER_GETTRANSACTIONS_REQ, e -> {
            LOG.info("Received a manager request");
            String corrId = e.getArgument(0, String.class);

            List<RecordedPayment> transactionList = getAllTransactions();
            queue.publish(new Event(MANAGER_GETTRANSACTIONS_RES, new Object[] { transactionList, corrId } ));

		});

        queue.addHandler(TRANSACTION_COMPLETED_RK, e -> {
            LOG.info("Received a transaction report");
            String customerId = e.getArgument(0, String.class);
            String merchantId = e.getArgument(1, String.class);
            String amount = e.getArgument(2, String.class);
            LOG.info("customerId: " + customerId + ", merchantId: " + merchantId + ", amount: " + amount);

            RecordedPayment payment = new RecordedPayment(customerId, merchantId, amount);
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