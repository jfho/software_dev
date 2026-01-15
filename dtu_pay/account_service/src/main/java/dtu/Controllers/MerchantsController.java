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

    private String BANKACCOUNT_MERCHANT_REQ_RK = "payments.merchantbankaccount.request";
    private String BANKACCOUNT_MERCHANT_RES_RK = "accounts.merchantbankaccount.response";
    private String DELETE_MERCHANT_RK = "accounts.merchant.deleted";

    private static final Logger LOG = Logger.getLogger(MerchantsController.class);

    public MerchantsController(MessageQueue q) {
        queue = q;

        queue.addHandler(BANKACCOUNT_MERCHANT_REQ_RK, e -> {
            LOG.info("received merchant bank account request message");
            String merchantId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);
            LOG.info("merchantId: " + merchantId + ", corrId: " + corrId);

            String bankAccountId = null;
            if (db.hasMerchant(merchantId)) {
                bankAccountId = db.getMerchant(merchantId).bankAccountUuid();
            }

            queue.publish(new Event(BANKACCOUNT_MERCHANT_RES_RK, new Object[] { bankAccountId, corrId } ));
		});
    }

    public Merchant getMerchant(String merchantId) {
        Merchant merchant = db.getMerchant(merchantId);
        if (merchant == null) {
            throw new NotFoundException("merchant not found");
        }
        return merchant;
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
        queue.publish(new Event(DELETE_MERCHANT_RK, new Object[] { id }));
    }

    public boolean hasMerchant(String id) {
        return db.hasMerchant(id);
    }
}