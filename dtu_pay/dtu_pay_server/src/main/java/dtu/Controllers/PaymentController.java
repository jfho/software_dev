package dtu.Controllers;

import java.util.List;

import dtu.Models.Customer;
import dtu.Models.DTUPayException;
import dtu.Models.Database;
import dtu.Models.Merchant;
import dtu.Models.Transaction;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankService_Service;

public class PaymentController {
    private static final PaymentController INSTANCE = new PaymentController();

    private PaymentController() {
    }

    public static PaymentController getInstance() {
        return INSTANCE;
    }

    BankService_Service service = new BankService_Service();
    BankService bank = service.getBankServicePort();
    Database db = Database.getInstance();

    public void registerTransaction(Transaction transaction) throws BankServiceException_Exception, DTUPayException {
        Customer customer = db.getCustomer(transaction.customerId());
        Merchant merchant = db.getMerchant(transaction.merchantId());
        if (customer == null) {
            throw new DTUPayException("customer with id \"" + transaction.customerId() + "\" is unknown");
        }
        if (merchant == null) {
            throw new DTUPayException("merchant with id \"" + transaction.merchantId() + "\" is unknown");
        }
        bank.transferMoneyFromTo(customer.bankAccountUuid(), merchant.bankAccountUuid(), transaction.payment(),
                "Ordinary transfer");
        db.addTransaction(transaction);
    }

    public List<Transaction> getTransactions() {
        return db.listTransactions();
    }
}