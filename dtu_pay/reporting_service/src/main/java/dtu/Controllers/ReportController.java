package dtu.Controllers;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import org.jboss.logging.Logger;

import dtu.MessagingUtils.Event;
import dtu.MessagingUtils.MessageQueue;
import dtu.Models.Customer;
import dtu.Models.Database;
import dtu.Models.RecordedPayment;
import jakarta.ws.rs.NotFoundException;

public class ReportController {
    MessageQueue queue;
    Database db = Database.getInstance();

    private String TRANSACTION_COMPLETED_RK = "payments.transaction.report";

    private static final Logger LOG = Logger.getLogger(ReportController.class);

    public ReportController(MessageQueue q) {
        queue = q;

        queue.addHandler(TRANSACTION_COMPLETED_RK, e -> {
            LOG.info("RabbitConsumer received message");
            String customerId = e.getArgument(0, String.class);
            String merchantId = e.getArgument(1, String.class);
            String amount = e.getArgument(2, String.class);

            RecordedPayment payment = new RecordedPayment(customerId, merchantId, amount);
            db.addPayment(payment);
		});
    }

    public ArrayList<RecordedPayment> getAllTransactions() {
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