package org.dtu;

import org.dtu.models.Customer;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

@Path("/customers")
public class CustomerResource {
    private final Database db = Database.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public int registerCustomer(Customer customer) {
        db.addCustomer(customer);
        return customer.getCustomerId();
    }

    @GET
    @Path("{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomer(@PathParam("customerId") int customerId) {
        return db.getCustomer(customerId);
    }
}
