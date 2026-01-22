/**
 * @author s253872
 */

package dtu.models;

import java.math.BigDecimal;

public record Transaction(String tokenId, String merchantId, BigDecimal amount, String customerId) {

}
