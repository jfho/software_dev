package dtu.example;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class HelloService {

    public String hello() {
        Client c = ClientBuilder.newClient();
        WebTarget r = c.target("http://localhost:8080/");

        try {
            return r.path("hello")
                    .request()
                    .accept(MediaType.TEXT_PLAIN)
                    .get(String.class);
        } catch (NotFoundException e) {
            throw new NotFoundException();
        }

    }

}
