package dtu.resources;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.models.Merchant;
import dtu.models.MerchantTransaction;
import dtu.services.MerchantService;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/_merchant")
public class MerchantResource {
    private static final MerchantService service = new MerchantService(new RabbitMqQueue());

    @POST
    @Path("/merchants")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerMerchant(Merchant merchant) {
        Merchant createdMerchant = service.registerMerchant(merchant);

        if (createdMerchant == null) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Merchant registration failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }

        return Response.created(URI.create("/merchants/" + createdMerchant.dtupayUuid()))
                .entity(createdMerchant)
                .build();
    }

    @GET
    @Path("/merchants/{merchantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMerchant(@PathParam("merchantId") String merchantId) {
        Merchant merchant = service.getMerchant(merchantId);
        if (merchant == null) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Merchant with id " + merchantId + " is unknown.")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
        return Response.ok(merchant).build();
    }

    @DELETE
    @Path("/merchants/{merchantId}")
    public Response deleteMerchant(@PathParam("merchantId") String merchantId) {
        boolean success = service.deleteMerchant(merchantId);

        if (!success) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Merchant with id " + merchantId + " is unknown.")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/merchants/{merchantId}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReport(@PathParam("merchantId") String merchantId) {
        List<MerchantTransaction> transactions = service.getTransactionsForMerchant(merchantId);

        return Response.ok(transactions).build();
    }

    @POST
    @Path("/merchants/{merchantId}/payments")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerTransaction(@PathParam("merchantId") String merchantId, MerchantTransaction transaction) {
        MerchantTransaction resultTransaction = new MerchantTransaction(transaction.tokenId(), merchantId,
                transaction.amount());
        boolean success = service.registerTransaction(resultTransaction);

        if (!success) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Transaction failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build());        }
        return Response.created(URI.create("/merchants/" + merchantId + "/reports")).build();
    }
}