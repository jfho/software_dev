package dtu.resources;

import dtu.MerchantService;
import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.models.Merchant;
import dtu.models.MerchantTransaction;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/merchants")
public class MerchantResource {
    private MerchantService service = new MerchantService(new RabbitMqQueue());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Merchant registerMerchant(Merchant merchant) {
        return service.registerMerchant(merchant);
    }

    @GET
    @Path("/{merchantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Merchant getMerchant(@PathParam("merchantId") String merchantId) {
        return service.getMerchant(merchantId);
    }

    @DELETE
    @Path("/{merchantId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteMerchant(@PathParam("merchantId") String merchantId) {
        service.deleteMerchant(merchantId);
    }

    @GET
    @Path("/{merchantId}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MerchantTransaction> getReport(@PathParam("merchantId") String merchantId) {
        return service.getTransactionsForMerchant(merchantId);
    }
}
