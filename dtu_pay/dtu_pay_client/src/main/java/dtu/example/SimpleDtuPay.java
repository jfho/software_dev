package dtu.example;

import java.math.BigDecimal;
import java.util.List;

import dtu.example.Models.BankAccount;
import dtu.example.Models.Customer;
import dtu.example.Models.Merchant;
import dtu.example.Models.Transaction;
import dtu.ws.fastmoney.Account;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class SimpleDtuPay {
    
    private Client c = ClientBuilder.newClient();
    private WebTarget r = c.target("http://localhost:8080/");
    private String BASEPATH = "bank";

    public String bankRegister(BankAccount account) {
        Response response = r.path(BASEPATH)
                            .path("accounts")
                            .request()
                            .post(Entity.entity(account, MediaType.APPLICATION_JSON));
            
        return response.readEntity(String.class); 
    }

    public boolean bankUnregister(String accountUuid) {
        Response res = r.path(BASEPATH)
            .path("accounts")
            .path(accountUuid)
            .request()
            .delete();
        
        return (res.getStatus() >= 200 && res.getStatus() < 300);
    }

    public Account bankAccount(String accountUuid) {
        return r.path(BASEPATH)
            .path("accounts")
            .path(accountUuid)
            .request()
            .get(Account.class);
    }

	public String register(Customer customer) {
        Response response = r.path("customers")
                    .request()
                    .post(Entity.entity(customer, MediaType.APPLICATION_JSON));
        
        return response.readEntity(String.class);
	}

    public String register(Merchant merchant) {
        Response response = r.path("merchants")
                    .request()
                    .post(Entity.entity(merchant, MediaType.APPLICATION_JSON));
        
        return response.readEntity(String.class);
    }

    public Response unregister(Customer customer) {
        Response res = r.path("customers")
                    .path(String.valueOf(customer.customerId()))
                    .request()
                    .delete();
        
        return res;
    }

    public Response unregister(Merchant merchant) {
        Response res = r.path("merchant")
                    .path(String.valueOf(merchant.merchantId()))
                    .request()
                    .delete();
        
        return res;
    }

    public boolean pay(String customerUuid, String merchantUuid, BigDecimal amount) {
        Transaction transaction = new Transaction(customerUuid, merchantUuid, amount);
        Response res = r.path("payments")
                        .request()
                        .post(Entity.entity(transaction, MediaType.APPLICATION_JSON));

        return (res.getStatus() >= 200 && res.getStatus() < 300);
    }

    public List<Transaction> getPayments() {
        return r.path("payments")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Transaction>>(){}); 
    }
}