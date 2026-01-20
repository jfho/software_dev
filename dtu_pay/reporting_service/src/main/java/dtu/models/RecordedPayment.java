package dtu.models;

public record RecordedPayment(String customerId, String merchantId, String amount, String tokenId, String transactionId, String timestamp) {

}

