package dtu.resources;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.models.Transaction;
import dtu.services.ManagerService;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/_manager")
public class ManagerResource {
    private static final ManagerService service = new ManagerService(new RabbitMqQueue());

    @GET
    @Path("/reports")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReports(){
        List<Transaction> transactions = service.getAllTransactions();
        return Response.ok(transactions).build();
    }

}
