package org.dtu;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/merchants")
public class MerchantResource {
    private final Database db = Database.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String registerMerchant(Merchant merchant) {
        db.addMerchant(merchant);
        return merchant.merchantId();
    }
}
