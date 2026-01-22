/**
 * @author s243019
 */

package dtu.adapters;

import java.math.BigDecimal;

public interface BankClientInterface {
    boolean transfer(String customerBankAccountId, String merchantBankAccountId, BigDecimal amount);
}
