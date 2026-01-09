package org.dtu.models;

public class Transaction {
    private int customerId;
    private int merchantId;
    private int amount;

    public Transaction() {}

    public Transaction(int customerId, int merchantId, int amount) {
        this.customerId = customerId;
        this.merchantId = merchantId;
        this.amount = amount;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}