package org.dtu.Controllers;

import java.util.List;
import java.math.BigDecimal;

import org.dtu.Models.Database;
import org.dtu.Models.Transaction;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankService_Service;

public class PaymentController {
    private static final PaymentController INSTANCE = new PaymentController();

    private PaymentController() {}

    public static PaymentController getInstance() {
        return INSTANCE;
    }

    BankService_Service service = new BankService_Service();
    BankService bank = service.getBankServicePort();
    Database db = Database.getInstance();

    public void registerTransaction(Transaction transaction) throws BankServiceException_Exception {
        
        bank.transferMoneyFromTo(transaction.customerId(), transaction.merchantId(), new BigDecimal(transaction.payment()), "Ordinary transfer");
        db.addTransaction(transaction);
    }

    public List<Transaction> getTransactions() {
        return db.listTransactions();
    }
}