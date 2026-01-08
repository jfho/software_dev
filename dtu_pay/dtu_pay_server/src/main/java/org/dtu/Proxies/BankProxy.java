package org.dtu.Proxies;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import io.github.cdimascio.dotenv.Dotenv;
import java.math.BigDecimal;
import java.util.List;

import org.dtu.Models.BankAccount;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.AccountInfo;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankService_Service;
import dtu.ws.fastmoney.User;


@Path("/bank")
public class BankProxy {
    private String bankApiKey = "yacht7201";
    
    BankService_Service service = new BankService_Service();
    BankService bank = service.getBankServicePort();

    @GET
    public String status() {
        return "Bank proxy up and running!";
    }

    @GET
    @Path("/accounts/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("accountId") String accountId) {
        try {
            return bank.getAccount(accountId);
        } catch (BankServiceException_Exception e) {
            throw new BadRequestException("Cannot retrieve account");
        }
    }

    @GET
    @Path("/accounts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AccountInfo> getAccounts() {
        return bank.getAccounts();
    }

    @POST
    @Path("/accounts")
    @Consumes(MediaType.APPLICATION_JSON)
    public String register(BankAccount account) {
        User user = new User();
        user.setFirstName(account.firstName());
        user.setLastName(account.lastName());
        user.setCprNumber(account.cpr());

        try {
            return bank.createAccountWithBalance(bankApiKey, user, new BigDecimal(account.balance()));
        } catch (BankServiceException_Exception e) {
            throw new BadRequestException("Could not register account");
        }
    }

    @DELETE
    @Path("/accounts/{accountId}")
    public void unregister(@PathParam("accountId") String accountId) {
        try {
            bank.retireAccount(bankApiKey, accountId);
        } catch (BankServiceException_Exception e) {
            throw new BadRequestException("Could not delete bank account");
        }
    }
}
