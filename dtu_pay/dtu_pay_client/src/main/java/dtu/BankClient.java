/**
 * @author s214881
 */


package dtu;

import dtu.Models.BankAccount;
import dtu.ws.fastmoney.Account;
import jakarta.ws.rs.BadRequestException;
import io.github.cdimascio.dotenv.Dotenv;
import dtu.ws.fastmoney.BankService_Service;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.User;

public class BankClient {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private String bankApiKey = dotenv.get("BANK_API_KEY");
    BankService_Service service = new BankService_Service();
    BankService bank = service.getBankServicePort();

    public Account getAccount(String accountUuid) {
        try {
            return bank.getAccount(accountUuid);
        } catch (BankServiceException_Exception e) {
            throw new BadRequestException("Cannot retrieve account, error message: " + e.getMessage());
        }
    }

    public String register(BankAccount account) {
        User user = new User();
        user.setFirstName(account.firstName());
        user.setLastName(account.lastName());
        user.setCprNumber(account.cpr());

        try {
            return bank.createAccountWithBalance(bankApiKey, user, account.balance());
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException("Failed registering with the bank, error message: " + e.getMessage());
        }
    }

    public void unregister(String accountUuid) {
        try {
            bank.retireAccount(bankApiKey, accountUuid);
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException("Failed unregistering with the bank, error message: " + e.getMessage());
        }
    }
}