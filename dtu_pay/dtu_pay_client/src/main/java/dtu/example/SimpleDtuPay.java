package dtu.example;

import java.util.List;
import org.dtu.models.Customer;
import org.dtu.models.Merchant;
import org.dtu.models.Transaction;
import org.dtu.models.TransactionResult;

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

    public TransactionResult pay(int amount, int customerId, int merchantId) {
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

    public Customer findCustomerById(int customerId) {
        return r.path("customers")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Customer>(){}); 
    }

    public void registerBankAccount(Customer customer, int balance) {
        //BankClient bankClient = new BankClient();
        //bankClient.registerCustomerAccount(customer, balance);
    }
}