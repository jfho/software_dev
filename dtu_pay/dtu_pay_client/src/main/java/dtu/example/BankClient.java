package dtu.example;

import dtu.example.Models.BankAccount;
import dtu.ws.fastmoney.Account;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class BankClient {
    
    private Client c = ClientBuilder.newClient();
    private WebTarget r = c.target("http://localhost:8080/");
    private String BASEPATH = "bank";

    public String register(BankAccount account) {
        Response response = r.path(BASEPATH)
                            .path("accounts")
                            .request()
                            .post(Entity.entity(account, MediaType.APPLICATION_JSON));
            
        return response.readEntity(String.class); 
    }

    public boolean unregister(String accountUuid) {
        Response res = r.path(BASEPATH)
            .path("accounts")
            .path(accountUuid)
            .request()
            .delete();
        
        return (res.getStatus() >= 200 && res.getStatus() < 300);
    }

    public Account getAccount(String accountUuid) {
        return r.path(BASEPATH)
            .path("accounts")
            .path(accountUuid)
            .request()
            .get(Account.class);
    }
}