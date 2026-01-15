package dtu.Adapters;

import dtu.Models.Token;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import jakarta.ws.rs.core.GenericType;

public class TokenManagerClient {

    private final Client client = ClientBuilder.newClient();
    private final String baseUrl = "http://nginx:8080";

    public List<String> requestTokens(String customerId, int amount) {
        return client
            .target(baseUrl)
            .path("/tokens")
            .queryParam("customerId", customerId)
            .queryParam("amount", amount)
            
            .request()
            .get(new GenericType<List<String>>() {});
    }
}
