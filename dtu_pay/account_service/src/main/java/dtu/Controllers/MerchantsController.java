package dtu.Controllers;

import java.util.UUID;

import org.jboss.logging.Logger;

import dtu.Models.Merchant;
import jakarta.ws.rs.NotFoundException;
import dtu.MessagingUtils.Event;
import dtu.MessagingUtils.MessageQueue;
import dtu.Models.Database;

public class MerchantsController {
    private final Database db = Database.getInstance();
    MessageQueue queue;

    private String PAYMENT_REQUEST_KEY = "payments.merchantbankaccount.request";
    private String PAYMENT_RESPONSE_KEY = "payments.merchantbankaccount.response";
    private String DELETE_USER_RK = "accounts.customer.deleted";

    private static final Logger LOG = Logger.getLogger(CustomerController.class);

    public MerchantsController(MessageQueue q) {
        queue = q;

        queue.addHandler(PAYMENT_REQUEST_KEY, e -> {
            LOG.info("RabbitConsumer received message");
            String accountId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);

            String bankAccountId = null;
            if (db.hasMerchant(accountId)) {
                bankAccountId = db.getMerchant(accountId).bankAccountUuid();
            }

            queue.publish(new Event(PAYMENT_RESPONSE_KEY, new Object[] { bankAccountId, corrId } ));
		});
    }

    public Merchant getMerchant(String MerchantId) {
        return db.getMerchant(MerchantId);
    }

    public Merchant registerMerchant(Merchant merchant) {
        String dtupayUuid = UUID.randomUUID().toString();
        Merchant registeredMerchant = new Merchant(merchant.firstName(), merchant.lastName(), merchant.cpr(), merchant.bankAccountUuid(), dtupayUuid);
        db.addMerchant(registeredMerchant);
        return registeredMerchant;
    }
    
    public void deleteMerchant(String id) {
        if (!db.hasMerchant(id)) {
            throw new NotFoundException("Merchant not found");
        }
        
        db.deleteMerchant(id);
        queue.publish(new Event(DELETE_USER_RK, new Object[] { id }));
    }

    public boolean hasMerchant(String id) {
        return db.hasMerchant(id);
    }
}