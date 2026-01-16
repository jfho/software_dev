package dtu;

import java.math.BigDecimal;

import dtu.adapters.BankClientInterface;

public class MockBankClient implements BankClientInterface {
    public MockBankClient() {
    }

    public boolean transfer(String customerBankAccountId, String merchantBankAccountId, BigDecimal amount) {
        return true;
    }
}
