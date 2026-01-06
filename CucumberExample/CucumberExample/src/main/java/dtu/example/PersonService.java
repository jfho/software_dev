package dtu.example;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class PersonService {

    private Client c = ClientBuilder.newClient();
    private WebTarget r = c.target("http://localhost:8080/");

    public Person getPerson() {

        try {
            return r.path("person")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(Person.class);
        } catch (NotFoundException e) {
            throw new NotFoundException();
        }
    }

    public void updatePerson(String name, String address) throws PersonServiceException {
        Person person = new Person(name, address);
        try {
            Response response = r.path("person")
                    .request()
                    .put(Entity.entity(person,MediaType.APPLICATION_JSON));

            if (response.getStatus() == 400) {
                throw new PersonServiceException();
            }
        } catch (NotFoundException e) {
            throw new NotFoundException();
        }
    }
}
