package org.dtu.Resources;

import java.util.List;
import java.math.BigDecimal;

import org.dtu.Models.Database;
import org.dtu.Models.Transaction;
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
    BankService_Service service = new BankService_Service();
    BankService bank = service.getBankServicePort();
    Database db = Database.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerTransaction(Transaction transaction) {
        try {
            bank.transferMoneyFromTo(transaction.customerId(), transaction.merchantId(), new BigDecimal(transaction.payment()), "Ordinary transfer");
        } catch (BankServiceException_Exception e) {
           throw new BadRequestException("Transaction denied by the bank");
        }
        db.addTransaction(transaction);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> getTransactions() {
        return db.listTransactions();
    }
}
