package dtu.Models;

import java.math.BigDecimal;

public record Transaction(String customerId, String merchantId, BigDecimal amount) {

}  