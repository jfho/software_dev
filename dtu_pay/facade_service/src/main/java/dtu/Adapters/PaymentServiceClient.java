package dtu.Adapters;

import dtu.Models.Transaction;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

public class PaymentServiceClient {

    private final Client client = ClientBuilder.newClient();
    private final String baseUrl = "http://nginx:8080";

    public void registerPayment(Transaction transaction) {
        client
            .target(baseUrl)
            .path("/payments")
            .request()
            .post(Entity.entity(transaction, MediaType.APPLICATION_JSON));
    }
}