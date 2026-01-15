package dtu.Resources;

import dtu.Controllers.MerchantsController;
import dtu.MessagingUtils.implementations.RabbitMqQueue;
import dtu.Models.Merchant;

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
    MerchantsController controller = new MerchantsController(new RabbitMqQueue());

    @GET
    @Path("/{merchantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Merchant getMerchant(@PathParam("merchantId") String merchantId) {
        return controller.getMerchant(merchantId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Merchant registerMerchant(Merchant merchant) {
        return controller.registerMerchant(merchant);
    }

    @DELETE
    @Path("/{merchantId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteMerchant(@PathParam("merchantId") String merchantId) {
        controller.deleteMerchant(merchantId);
    }
}
