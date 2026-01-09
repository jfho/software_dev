package dtu.example;

import java.math.BigDecimal;
import java.util.List;

import dtu.example.Models.Customer;
import dtu.example.Models.Merchant;
import dtu.example.Models.Transaction;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class DtuPayClient {

    private String MERCHANTS_PATH = "merchants";
    private String CUSTOMERS_PATH = "customers";
    private String PAYMENTS_PATH = "payments";

    private Client c = ClientBuilder.newClient();
    private WebTarget r = c.target("http://localhost:8080/");



	public void register(Customer customer) {
        r.path(CUSTOMERS_PATH)
                    .request()
                    .post(Entity.entity(customer, MediaType.APPLICATION_JSON));
	}

    public void register(Merchant merchant) {
        r.path(MERCHANTS_PATH)
                    .request()
                    .post(Entity.entity(merchant, MediaType.APPLICATION_JSON));
    }

    public void unregister(Customer customer) {
        r.path(CUSTOMERS_PATH)
                    .path(String.valueOf(customer.username()))
                    .request()
                    .delete();
    }

    public void unregister(Merchant merchant) {
        r.path(MERCHANTS_PATH)
                    .path(String.valueOf(merchant.username()))
                    .request()
                    .delete();
    }

    public Customer getCustomer(String username) {
        return r.path(CUSTOMERS_PATH)
                    .path(username)
                    .request()
                    .get(Customer.class);
    }

    public Merchant getMerchant(String username) {
        return r.path(MERCHANTS_PATH)
                    .path(username)
                    .request()
                    .get(Merchant.class);
    }

    public boolean pay(String customerUuid, String merchantUuid, BigDecimal amount) {
        Transaction transaction = new Transaction(customerUuid, merchantUuid, amount);
        Response res = r.path(PAYMENTS_PATH)
                        .request()
                        .post(Entity.entity(transaction, MediaType.APPLICATION_JSON));

        return (res.getStatus() >= 200 && res.getStatus() < 300);
    }

    public List<Transaction> getPayments() {
        return r.path(PAYMENTS_PATH)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Transaction>>(){}); 
    }
}