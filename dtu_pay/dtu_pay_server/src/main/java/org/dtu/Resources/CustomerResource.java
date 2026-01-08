package org.dtu.Resources;

import org.dtu.Models.Customer;
import org.dtu.Models.Database;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/customers")
public class CustomerResource {
    private final Database db = Database.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String registerCustomer(Customer customer) {
        db.addCustomer(customer);
        return customer.customerId();
    }
}
