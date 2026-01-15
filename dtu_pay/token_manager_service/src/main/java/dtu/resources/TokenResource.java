package dtu.resources;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.services.TokenService;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/tokens")
public class TokenResource {
    private TokenService controller = new TokenService(new RabbitMqQueue());

    @GET
    @Path("/customer/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getTokensByCustomerID(@PathParam("customerId") String customerId) {
        return controller.getAllTokensByCustomer(customerId);
    }

    @POST
    @Path("/customer/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> createTokens(@PathParam("customerId") String customerId) {
        return controller.createTokens(customerId, 6); //INIT finegrain control of tokens to create
    }

    @GET
    @Path("/token/{tokenId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String validateToken(@PathParam("tokenId") String tokenId) {
        return controller.validateToken(tokenId);
    }
}
