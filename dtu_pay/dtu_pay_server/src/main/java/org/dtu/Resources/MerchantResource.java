package org.dtu.Resources;

import org.dtu.Models.Database;
import org.dtu.Models.Merchant;

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

@Path("/merchants")
public class MerchantResource {
    BankService_Service service = new BankService_Service();
    BankService bank = service.getBankServicePort();
    
    private final Database db = Database.getInstance();

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Merchant getMerchant(@PathParam("username") String username) {
        Merchant merchant = db.getMerchant(username);
        if (merchant == null) {
            throw new NotFoundException("Merchant not found");
        }
        return merchant;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerMerchant(Merchant merchant) {
        try {
            bank.getAccount(merchant.bankAccountUuid());
        } catch (BankServiceException_Exception e) {
            throw new BadRequestException("Invalid bank account");
        }
        
        db.addMerchant(merchant);
    }

    @DELETE
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteMerchant(@PathParam("username") String username) {       
        db.deleteMerchant(username);
    }
}
