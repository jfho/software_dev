package dtu.resources;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.models.Transaction;
import dtu.services.ManagerService;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/managers")
public class ManagerResource {
    private ManagerService controller = new ManagerService(new RabbitMqQueue());

    @GET
    @Path("/reports")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> getReports(){
        return controller.getAllTransactions();
    }

}
