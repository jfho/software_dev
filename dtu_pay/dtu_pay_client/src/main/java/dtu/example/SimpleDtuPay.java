package dtu.example;

import java.util.List;

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

    public TransactionResult pay(Integer amount, String customerId, String merchantId) {
        Transaction transaction = new Transaction(customerId, merchantId, amount);
        Response response = r.path("payments")
                    .request()
                    .post(Entity.entity(transaction, MediaType.APPLICATION_JSON));
        
        return response.readEntity(TransactionResult.class);
    }

    public List<Transaction> getPayments() {
        return r.path("payments")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Transaction>>(){}); 
    }
}