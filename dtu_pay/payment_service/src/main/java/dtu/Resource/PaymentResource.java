package dtu.Resource;

import java.util.List;

import dtu.Controller.PaymentController;
import dtu.Models.Transaction;
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
        paymentController.registerTransaction(transaction);
    }
}
