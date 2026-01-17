package dtu.models;

public record CustomerTransaction (String tokenId, String merchantId, String amount, String transactionId, String timestamp) {
    
}
