package dtu.Models;

import java.math.BigDecimal;

public record Transaction(String tokenId, String merchantId, BigDecimal payment) {

}  