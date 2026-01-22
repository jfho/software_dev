/**
 * @author s253872
 */

package dtu;

import java.math.BigDecimal;
import java.util.List;

import dtu.Models.Customer;
import dtu.Models.Merchant;
import dtu.Models.MerchantTransaction;
import dtu.Models.Transaction;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class MerchantClient extends BaseClient {

    private String MERCHANTS_PATH = "/_merchant/merchants";

    public Merchant register(Merchant merchant) {
        Response res = r.path(MERCHANTS_PATH)
                .request()
                .post(Entity.entity(merchant, MediaType.APPLICATION_JSON));

        if (res.getStatus() >= 400) {
            throw new RuntimeException(res.readEntity(String.class));
        }
        return res.readEntity(Merchant.class);
    }

    public Response unregister(Merchant merchant) {
        return r.path(MERCHANTS_PATH)
                .path(String.valueOf(merchant.dtupayUuid()))
                .request()
                .delete();
    }

    public Merchant getMerchant(String merchantDtupayUuid) {
        Response res = r.path(MERCHANTS_PATH)
                .path(merchantDtupayUuid)
                .request()
                .get();

        if (res.getStatus() == 404) {
            return null;
        }

        if (res.getStatus() >= 400) {
            throw new RuntimeException(res.readEntity(String.class));
        }
        return res.readEntity(Merchant.class);
    }

    public void pay(String tokenUuid, String merchantDtupayUuid, String amount) {
        MerchantTransaction transaction = new MerchantTransaction(tokenUuid, merchantDtupayUuid, amount);
        Response res = r.path(MERCHANTS_PATH + "/" + merchantDtupayUuid + "/payments")
                .request()
                .post(Entity.entity(transaction, MediaType.APPLICATION_JSON));

        if (res.getStatus() >= 400) {
            throw new RuntimeException(res.readEntity(String.class));
        }
    }

    public List<Transaction> getReports(String merchantDtupayUuid) {
        Response res = r.path(MERCHANTS_PATH + "/" + merchantDtupayUuid + "/reports")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (res.getStatus() >= 400) {
            throw new RuntimeException(res.readEntity(String.class));
        }
        return res.readEntity(new GenericType<List<Transaction>>() {
        });
    }
}
