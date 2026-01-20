package dtu.resources;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.models.Merchant;
import dtu.models.MerchantTransaction;
import dtu.services.MerchantService;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/_merchant")
public class MerchantResource {
    private static final MerchantService service = new MerchantService(new RabbitMqQueue());

    @POST
    @Path("/merchants")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Merchant registerMerchant(Merchant merchant) {
        return service.registerMerchant(merchant);
    }

    @GET
    @Path("/merchants/{merchantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Merchant getMerchant(@PathParam("merchantId") String merchantId) {
        return service.getMerchant(merchantId);
    }

    @DELETE
    @Path("/merchants/{merchantId}")
    public void deleteMerchant(@PathParam("merchantId") String merchantId) {
        service.deleteMerchant(merchantId);
    }

    @GET
    @Path("/merchants/{merchantId}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MerchantTransaction> getReport(@PathParam("merchantId") String merchantId) {
        return service.getTransactionsForMerchant(merchantId);
    }

    @POST
    @Path("/merchants/{merchantId}/payments")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean registerTransaction(@PathParam("merchantId") String merchantId, MerchantTransaction transaction) {
        MerchantTransaction resultTransaction = new MerchantTransaction(transaction.tokenId(), merchantId, transaction.amount());
        return service.registerTransaction(resultTransaction);
    }
}