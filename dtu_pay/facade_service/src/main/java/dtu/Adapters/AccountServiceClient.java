package dtu.Adapters;

import dtu.Models.Customer;
import dtu.Models.Merchant;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class AccountServiceClient {

    private static final String BASE_URL = "http://nginx:8080";

    private final Client client;

    public AccountServiceClient() {
        this.client = ClientBuilder.newClient();
    }

    /* -------------------- Customers -------------------- */

    public String createCustomer(Customer customer) {
        Response response = client
                .target(BASE_URL)
                .path("/customers")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(customer, MediaType.APPLICATION_JSON));

        if (response.getStatus() != 200 && response.getStatus() != 201) {
            throw new RuntimeException("Failed to create customer: " + response.getStatus());
        }

        return response.readEntity(String.class); // customerId
    }

    public void updateCustomer(Customer customer) {
        Response response = client
                .target(BASE_URL)
                .path("/customers/" + customer.dtupayUuid())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(customer, MediaType.APPLICATION_JSON));

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed to update customer: " + response.getStatus());
        }
    }

    public void deleteCustomer(String customerId) {
        Response response = client
                .target(BASE_URL)
                .path("/customers/" + customerId)
                .request()
                .delete();

        if (response.getStatus() != 204 && response.getStatus() != 200) {
            throw new RuntimeException("Failed to delete customer: " + response.getStatus());
        }
    }

    /* -------------------- Merchants -------------------- */

    public String createMerchant(Merchant merchant) {
        Response response = client
                .target(BASE_URL)
                .path("/merchants")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(merchant, MediaType.APPLICATION_JSON));

        if (response.getStatus() != 200 && response.getStatus() != 201) {
            throw new RuntimeException("Failed to create merchant: " + response.getStatus());
        }

        return response.readEntity(String.class); // merchantId
    }

    public void deleteMerchant(String merchantId) {
        Response response = client
                .target(BASE_URL)
                .path("/merchants/" + merchantId)
                .request()
                .delete();

        if (response.getStatus() != 204 && response.getStatus() != 200) {
            throw new RuntimeException("Failed to delete merchant: " + response.getStatus());
        }
    }
}
