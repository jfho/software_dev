package dtu.Adapters;

import java.math.BigDecimal;

public interface BankClientInterface {
    boolean transfer(String customerBankAccountId, String merchantBankAccountId, BigDecimal amount);
}
