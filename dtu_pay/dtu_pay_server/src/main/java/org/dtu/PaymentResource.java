package org.dtu;

import java.util.List;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/payments")
public class PaymentResource {
    private final Database db = Database.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public TransactionResult registerTransaction(Transaction transaction) {
        // Check customer exists
        if (!db.hasCustomer(transaction.customerId())) {
            return new TransactionResult(false, "customer with id \"" + transaction.customerId() + "\" is unknown");
        }

        // Check merchant exists
        if (!db.hasMerchant(transaction.merchantId())) {
            return new TransactionResult(false, "merchant with id \"" + transaction.merchantId() + "\" is unknown");
        }

        db.addTransaction(transaction);
        return new TransactionResult(true, null);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> getTransactions() {
        return db.listTransactions();
    }
}
