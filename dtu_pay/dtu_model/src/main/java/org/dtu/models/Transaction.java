package org.dtu.models;

public class Transaction {
    private String customerId;
    private String merchantId;
    private int amount;

    public Transaction() {}

    public Transaction(String customerId, String merchantId, int amount) {
        this.customerId = customerId;
        this.merchantId = merchantId;
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}