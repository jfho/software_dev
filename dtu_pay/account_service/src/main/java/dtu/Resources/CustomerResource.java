package dtu.Resources;

import java.io.IOException;

import dtu.Controllers.CustomerController;
import dtu.Controllers.RabbitMq;
import dtu.Models.Customer;

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
    CustomerController controller = new CustomerController();
    RabbitMq rabbitmqClient = RabbitMq.getInstance();

    @GET
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomer(@PathParam("customerId") String customerId) {
        Customer customer = controller.getCustomer(customerId);

        if (customer == null) {
            throw new NotFoundException("Customer not found");
        }

        return customer;
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
        if (!controller.hasCustomer(customerId)) {
            throw new NotFoundException("Error deleting customer: customer not found");
        }
        
        controller.deleteCustomer(customerId);

        try {
            rabbitmqClient.publishUserDeletedEvent(customerId);
        } catch (IOException e) {
            throw new InternalError("Problem occurred connecting to RabbitMQ.");
        }
    }
}
