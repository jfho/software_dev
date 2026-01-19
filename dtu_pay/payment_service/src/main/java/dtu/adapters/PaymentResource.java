package dtu.adapters;

import dtu.PaymentService;
import dtu.models.Transaction;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/payments")
public class PaymentResource {
    PaymentService paymentService = new PaymentService(new RabbitMqQueue(), new BankClient());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerTransaction(Transaction transaction) throws Exception {
        paymentService.registerTransaction(transaction);
    }
}
