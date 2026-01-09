package dtu.Resources;

import java.util.List;
import java.math.BigDecimal;

import dtu.Controllers.PaymentController;
import dtu.Models.Database;
import dtu.Models.Transaction;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankService_Service;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/payments")
public class PaymentResource {   
    PaymentController paymentController = PaymentController.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerTransaction(Transaction transaction) {
        try {
            paymentController.registerTransaction(transaction);
        } catch (BankServiceException_Exception e) {
           throw new BadRequestException("Transaction denied by the bank");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> getTransactions() {
        return paymentController.getTransactions();
    }
}
