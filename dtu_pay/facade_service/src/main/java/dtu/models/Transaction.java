/**
 * @author s243019
 */

package dtu.models;

public record Transaction(String tokenId, String customerId, String merchantId, String amount, String transactionId,
        String timestamp) {
}