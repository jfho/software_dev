package dtu.Resources;

import dtu.Controllers.MerchantsController;
import dtu.Models.Merchant;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/merchants")
public class MerchantResource {
    private MerchantsController controller = new MerchantsController();

    @GET
    @Path("/{merchantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Merchant getMerchant(@PathParam("merchantId") String merchantId) {
        Merchant merchant = controller.getMerchant(merchantId);
        if (merchant == null) {
            throw new NotFoundException("Merchant not found");
        }
        return merchant;
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
