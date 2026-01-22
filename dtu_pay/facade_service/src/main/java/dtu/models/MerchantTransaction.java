/**
 * @author s243019
 */

package dtu.models;

public record MerchantTransaction(String tokenId, String merchantId, String amount, String transactionId, String timestamp) {
    
}
