/**
 * @author s243019
 */

package dtu.adapters;

import java.math.BigDecimal;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankService_Service;

public class BankClient implements BankClientInterface {
    BankService_Service service = new BankService_Service();
    BankService bank = service.getBankServicePort();

    public BankClient() {
    }

    public boolean transfer(String customerBankAccountId, String merchantBankAccountId, BigDecimal amount) {
        try {
            bank.transferMoneyFromTo(customerBankAccountId, merchantBankAccountId, amount,
                    "Ordinary transfer");
        } catch (BankServiceException_Exception e) {
            return false;
        }
        return true;
    }
}