package dtu;

import java.math.BigDecimal;
import java.util.List;

import dtu.Models.Customer;
import dtu.Models.Merchant;
import dtu.Models.Transaction;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class MerchantClient extends BaseClient {

    private String MERCHANTS_PATH = "merchants";

    public Merchant register(Merchant merchant) {
        Response res = r.path(MERCHANTS_PATH)
                    .request()
                    .post(Entity.entity(merchant, MediaType.APPLICATION_JSON));

        return res.readEntity(Merchant.class);
    }

    public Response unregister(Merchant merchant) {
        return r.path(MERCHANTS_PATH )
                    .path(String.valueOf(merchant.dtupayUuid()))
                    .request()
                    .delete();
    }

    public Merchant getMerchant(String merchantDtupayUuid) {
        return r.path(MERCHANTS_PATH )
                    .path(merchantDtupayUuid)
                    .request()
                    .get(Merchant.class);
    }

    public Response pay(String tokenUuid, String merchantDtupayUuid, BigDecimal amount) {
        Transaction transaction = new Transaction(tokenUuid, merchantDtupayUuid, amount);
        return r.path(MERCHANTS_PATH + "/" + merchantDtupayUuid + "/payments")
                    .request()
                    .post(Entity.entity(transaction, MediaType.APPLICATION_JSON));
    }

    public List<Transaction> getReports(String merchantDtupayUuid) {
        return r.path(MERCHANTS_PATH + "/" + merchantDtupayUuid + "/reports")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Transaction>>(){}); 
    }
}
