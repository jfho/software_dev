package dtu.Resources;

import dtu.Controllers.PaymentController;
import dtu.Models.Transaction;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/payments")
public class PaymentResource {
    PaymentController paymentController = PaymentController.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerTransaction(Transaction transaction) throws Exception {
        paymentController.registerTransaction(transaction);
    }
}
