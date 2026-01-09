package org.dtu;

import org.dtu.models.Merchant;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/merchants")
public class MerchantResource {
    private final Database db = Database.getInstance();
    private String merchantCpr;
    private int merchantId;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public int registerMerchant(Merchant merchant) {
        db.addMerchant(merchant);
        return merchant.getMerchantId();
    }
}
