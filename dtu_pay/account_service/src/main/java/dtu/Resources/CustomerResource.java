package dtu.Resources;

import dtu.Controllers.CustomerController;
import dtu.MessagingUtils.implementations.RabbitMqQueue;
import dtu.Models.Customer;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/customers")
public class CustomerResource {
    CustomerController controller = new CustomerController(new RabbitMqQueue());

    @GET
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomer(@PathParam("customerId") String customerId) {
        return controller.getCustomer(customerId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Customer registerCustomer(Customer customer) {
        return controller.registerCustomer(customer);
    }

    @DELETE
    @Path("/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteCustomer(@PathParam("customerId") String customerId) {
        controller.deleteCustomer(customerId);
    }
}
