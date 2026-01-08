package dtu.example.Models;

import java.math.BigDecimal;

public record BankAccount(String firstName, String lastName, String cpr, BigDecimal balance) {
    
}
