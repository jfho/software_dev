package dtu.models;

public class RecordedPayment {
    public RecordedPayment(
            String customerId, 
            String merchantId, 
            String amount, 
            String tokenId, 
            String transactionId,
            String timestamp,
            Boolean ccessful) {

        this.customerId = customerId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.tokenId = tokenId;
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.successful = successful;
    }

    public String customerId;
    public String merchantId;
    public String amount;
    public String tokenId;
    public String transactionId;
    public String timestamp;
    public Boolean successful;

}

