package org.dtu.Resources;

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
    BankService_Service service = new BankService_Service();
    BankService bank = service.getBankServicePort();

    private final Database db = Database.getInstance();

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomer(@PathParam("username") String username) {
        Customer customer = db.getCustomer(username);
        if (customer == null) {
            throw new NotFoundException("Customer not found");
        }
        return customer;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerCustomer(Customer customer) {
        try {
            bank.getAccount(customer.bankAccountUuid());
        } catch (BankServiceException_Exception e) {
            throw new BadRequestException("Invalid bank account");
        }
        
        db.addCustomer(customer);
    }

    @DELETE
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteCustomer(@PathParam("username") String username) {       
        db.deleteCustomer(username);
    }
}
