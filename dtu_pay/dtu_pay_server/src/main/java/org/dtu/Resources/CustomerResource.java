package org.dtu.Resources;

import java.util.UUID;

import org.dtu.Controllers.CustomerController;
import org.dtu.Models.Customer;
import org.dtu.Models.Database;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankService_Service;
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

@Path("/customers")
public class CustomerResource {   
    CustomerController controller = new CustomerController();

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
        controller.deleteCustomer(customerId);
    }
}
