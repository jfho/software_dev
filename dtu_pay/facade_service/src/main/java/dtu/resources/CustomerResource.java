package dtu.resources;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.models.Customer;
import dtu.models.Transaction;
import dtu.services.CustomerService;

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

@Path("/_customer")
public class CustomerResource {
    private static final CustomerService service = new CustomerService(new RabbitMqQueue());

    @POST
    @Path("/customers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerCustomer(Customer customer) {
        Customer createdCustomer = service.registerCustomer(customer);

        if (createdCustomer == null) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Customer registration failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }

        return Response.created(URI.create("/customers/" + createdCustomer.dtupayUuid()))
                .entity(createdCustomer)
                .build();
    }

    @GET
    @Path("/customers/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@PathParam("customerId") String customerId) {
        Customer customer = service.getCustomer(customerId);
        if (customer == null) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer with id " + customerId + " is unknown.")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
        return Response.ok(customer).build();
    }

    @DELETE
    @Path("/customers/{customerId}")
    public Response deleteCustomer(@PathParam("customerId") String customerId) {
        boolean success = service.deleteCustomer(customerId);

        if (!success) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer with id " + customerId + " is unknown.")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/customers/{customerId}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReport(@PathParam("customerId") String customerId) {
        List<Transaction> transactions = service.getTransactionsForCustomer(customerId);

        return Response.ok(transactions).build();
    }

    @POST
    @Path("/customers/{customerId}/tokens")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTokens(@PathParam("customerId") String customerId, String amount) {
        List<String> tokens = service.createTokens(customerId, Integer.parseInt(amount));
        if (tokens.isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid token request")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
        return Response.ok(tokens).build();
    }
}