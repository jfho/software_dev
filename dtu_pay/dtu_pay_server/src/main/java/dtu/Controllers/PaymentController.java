package dtu.Controllers;

import java.util.List;
import java.math.BigDecimal;

import dtu.Models.Database;
import dtu.Models.Transaction;
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
        String customerBankId = db.getCustomer(transaction.customerId()).bankAccountUuid();
        String merchantBankId = db.getMerchant(transaction.merchantId()).bankAccountUuid();
        bank.transferMoneyFromTo(customerBankId, merchantBankId, new BigDecimal(transaction.payment()), "Ordinary transfer");
        db.addTransaction(transaction);
    }

    public List<Transaction> getTransactions() {
        return db.listTransactions();
    }
}