package dtu;

import dtu.Models.Customer;
import dtu.Models.Merchant;
import dtu.Models.Transaction;

import java.util.List;

public class State {
    public Customer customer;
    public Customer customer2;
    public Merchant merchant;
    public Merchant merchant2;

    public List<String> tokens;
    public List<Transaction> transactions;

    public Exception lastException;
}
