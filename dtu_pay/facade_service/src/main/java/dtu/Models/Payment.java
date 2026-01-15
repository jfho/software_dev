package dtu.Models;

import java.math.BigDecimal;

public record Payment(
    String merchantId,
    String tokenId,
    BigDecimal amount
) {
}