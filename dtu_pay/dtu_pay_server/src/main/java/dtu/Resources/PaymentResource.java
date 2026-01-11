package dtu.Resources;

import java.util.List;

import dtu.Controllers.PaymentController;
import dtu.Models.DTUPayException;
import dtu.Models.Transaction;
import dtu.ws.fastmoney.BankServiceException_Exception;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/payments")
public class PaymentResource {
    PaymentController paymentController = PaymentController.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerTransaction(Transaction transaction) {
        try {
            paymentController.registerTransaction(transaction);
        } catch (BankServiceException_Exception e) {
            throw new BadRequestException(e.getMessage());
        } catch (DTUPayException e) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build());
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> getTransactions() {
        return paymentController.getTransactions();
    }
}
