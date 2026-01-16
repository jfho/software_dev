package dtu.services;

import java.util.UUID;

import org.jboss.logging.Logger;

import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.Database;
import dtu.models.Customer;
import dtu.models.Merchant;
import jakarta.ws.rs.NotFoundException;

public class MerchantService {
    private final Database db = Database.getInstance();
    MessageQueue queue;

    private String REGISTER_MERCHANT_REQ_RK = "facade.registerMerchant.request";
    private String GET_MERCHANT_REQ_RK = "facade.getMerchant.request";
    private String DELETE_MERCHANT_REQ_RK = "facade.deleteMerchant.request";

    private String REGISTER_MERCHANT_RES_RK = "facade.registerMerchant.response";
    private String GET_MERCHANT_RES_RK = "facade.getMerchant.response";
    private String DELETE_MERCHANT_RES_RK = "facade.deleteMerchant.response";

    private String BANKACCOUNT_MERCHANT_REQ_RK = "payments.merchantbankaccount.request";
    private String BANKACCOUNT_MERCHANT_RES_RK = "accounts.merchantbankaccount.response";
    private String DELETE_MERCHANT_RK = "accounts.merchant.deleted";

    private static final Logger LOG = Logger.getLogger(MerchantService.class);

    public MerchantService(MessageQueue q) {
        LOG.info("Starting MerchantService");
        
        queue = q;

        // delete merchant handler (facade)
        queue.addHandler(DELETE_MERCHANT_REQ_RK, e -> {
            LOG.info("received merchant delete request");
            String merchantId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);

            boolean success = deleteMerchant(merchantId);

            queue.publish(new Event(DELETE_MERCHANT_RES_RK, new Object[] { success, corrId } ));
		});

        // get merchant by id (facade)
        queue.addHandler(GET_MERCHANT_REQ_RK, e -> {
            LOG.info("received merchant get request");
            String merchantId = e.getArgument(0, String.class);
            String corrId = e.getArgument(1, String.class);

            Merchant merchant = getMerchant(merchantId);

            queue.publish(new Event(GET_MERCHANT_RES_RK, new Object[] { merchant, corrId } ));
		});

        // register merchant handler (facade)
        queue.addHandler(REGISTER_MERCHANT_REQ_RK, e -> {
            LOG.info("received merchant registration request");
            String firstName = e.getArgument(0, String.class);
            String lastName = e.getArgument(1, String.class);
            String cpr = e.getArgument(2, String.class);
            String bankUuid = e.getArgument(3, String.class);
            String corrId = e.getArgument(4, String.class);
            LOG.info("firstName: " + firstName + ", lastName: " + lastName);

            Merchant newMerchant = registerMerchant(new Merchant(firstName, lastName, cpr, bankUuid, null));

            queue.publish(new Event(REGISTER_MERCHANT_RES_RK, new Object[] { newMerchant, corrId } ));
		});

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
        return db.getMerchant(merchantId);
    }

    public Merchant registerMerchant(Merchant merchant) {
        String dtupayUuid = UUID.randomUUID().toString();
        Merchant registeredMerchant = new Merchant(merchant.firstName(), merchant.lastName(), merchant.cpr(), merchant.bankAccountUuid(), dtupayUuid);
        db.addMerchant(registeredMerchant);
        return registeredMerchant;
    }
    
    public boolean deleteMerchant(String id) {       
        return db.deleteMerchant(id);
    }

    public boolean hasMerchant(String id) {
        return db.hasMerchant(id);
    }
}