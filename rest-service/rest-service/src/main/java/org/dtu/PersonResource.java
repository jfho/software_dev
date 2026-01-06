package org.dtu;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/person")
public class PersonResource {
    private Person person = new Person("Susan", "USA");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Person getPerson() {
        return this.person;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putPerson(Person person) {
        if (person.getAddress().equals("-none-")) {
            return Response.status(Response.Status.BAD_REQUEST)
                       .entity("Address cannot be \"-none-\"")
                       .build();
        }

        this.person.setName(person.getName());
        this.person.setAddress(person.getAddress());
    
        return Response.ok().build();
    }
}

