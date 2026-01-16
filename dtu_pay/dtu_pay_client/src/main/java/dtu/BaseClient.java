package dtu;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

public class BaseClient {

    protected Client c = ClientBuilder.newClient();
    protected WebTarget r = c.target("http://localhost:8080/");
}
