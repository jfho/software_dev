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

    private String TRANSACTION_COMPLETED_RK = "payments.transaction.report";
    private String TRANSACTION_COMPLETED_RK = "facade.merchant.request";
    private String TRANSACTION_COMPLETED_RK = "facade.customer.request";
    private String TRANSACTION_COMPLETED_RK = "facade.manager.request";

    private static final Logger LOG = Logger.getLogger(ReportService.class);

    public ReportService(MessageQueue q) {
        queue = q;

        

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