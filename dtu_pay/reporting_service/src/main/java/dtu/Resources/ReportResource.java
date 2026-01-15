package dtu.Resources;

import dtu.Controllers.ReportController;
import dtu.MessagingUtils.implementations.RabbitMqQueue;
import dtu.Models.Customer;

import java.util.List;
import java.util.ArrayList;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import dtu.Models.RecordedPayment;


@Path("/reports")
public class ReportResource {
    ReportController controller = new ReportController(new RabbitMqQueue());

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RecordedPayment> getAllPayments() {
        return controller.getAllTransactions();
    }

    @GET
    @Path("/customer/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RecordedPayment> getCustomerPaymentHistory(@PathParam("customerId") String customerId) {
        return controller.getTransactionsForCustomer(customerId);
    }

    @GET
    @Path("/merchant/{merchantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RecordedPayment> getMerchantPaymentHistory(@PathParam("merchantId") String merchantId) {
        return controller.getTransactionsForMerchant(merchantId);
    }
}
