package dtu.Controllers;

import java.util.UUID;

import dtu.Models.Merchant;
import dtu.Models.Database;

public class MerchantsController {
    private final Database db = Database.getInstance();

    public Merchant getMerchant(String MerchantId) {
        Merchant merchant = db.getMerchant(MerchantId);
        return merchant;
    }

    public Merchant registerMerchant(Merchant merchant) {
        String dtupayUuid = UUID.randomUUID().toString();
        Merchant registeredMerchant = new Merchant(merchant.firstName(), merchant.lastName(), merchant.cpr(), merchant.bankAccountUuid(), dtupayUuid);
        db.addMerchant(registeredMerchant);
        return registeredMerchant;
    }
    
    public void deleteMerchant(String id) {
        db.deleteMerchant(id);
    }
}