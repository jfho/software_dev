package dtu.resources;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.models.Customer;
import dtu.models.CustomerTransaction;
import dtu.models.TokenRequest;
import dtu.services.CustomerService;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/customers")
public class CustomerResource {
    private static final CustomerService service = new CustomerService(new RabbitMqQueue());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Customer registerCustomer(Customer customer) {
        return service.registerCustomer(customer);
    }

    @GET
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomer(@PathParam("customerId") String customerId) {
        Customer customer = service.getCustomer(customerId);
        if (customer == null) {
            throw new NotFoundException("Customer with id " + customerId + " is unknown.");
        }
        return customer;
    }

    @DELETE
    @Path("/{customerId}")
    public void deleteCustomer(@PathParam("customerId") String customerId) {
        service.deleteCustomer(customerId);
    }

    @GET
    @Path("/{customerId}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomerTransaction> getReport(@PathParam("customerId") String customerId) {
        return service.getTransactionsForCustomer(customerId);
    }

    @POST
    @Path("/{customerId}/tokens")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> createTokens(@PathParam("customerId") String customerId, TokenRequest request) {
        return service.createTokens(customerId, request);
    }
}