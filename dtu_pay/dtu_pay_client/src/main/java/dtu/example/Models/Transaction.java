package dtu.example.Models;

import java.math.BigDecimal;

public record Transaction(String customerId, String merchantId, BigDecimal payment) {

}  