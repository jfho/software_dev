/**
 * @author s253872
 */


package dtu;

import java.util.List;

import dtu.Models.Customer;
import dtu.Models.Transaction;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class CustomerClient extends BaseClient {

    private String CUSTOMERS_PATH = "_customer/customers";

    public Customer register(Customer customer) {
        Response res = r.path(CUSTOMERS_PATH)
                .request()
                .post(Entity.entity(customer, MediaType.APPLICATION_JSON));

        if (res.getStatus() >= 400) {
            throw new RuntimeException(res.readEntity(String.class));
        }
        return res.readEntity(Customer.class);
    }

    public Response unregister(Customer customer) {
        return r.path(CUSTOMERS_PATH)
                .path(String.valueOf(customer.dtupayUuid()))
                .request()
                .delete();
    }

    public Customer getCustomer(String customerDtupayUuid) {
        Response res = r.path(CUSTOMERS_PATH)
                .path(customerDtupayUuid)
                .request()
                .get();

        if (res.getStatus() == 404) {
            return null;
        }

        if (res.getStatus() >= 400) {
            throw new RuntimeException(res.readEntity(String.class));
        }
        return res.readEntity(Customer.class);
    }

    public List<Transaction> getReports(String customerDtupayUuid) {
        Response res = r.path(CUSTOMERS_PATH + "/" + customerDtupayUuid + "/reports")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (res.getStatus() == 404) {
            return null;
        }

        if (res.getStatus() >= 400) {
            throw new RuntimeException(res.readEntity(String.class));
        }
        return res.readEntity(new GenericType<List<Transaction>>() {
        });
    }

    public List<String> getTokens(String customerDtupayUuid, int amount) {
        Response res = r.path(CUSTOMERS_PATH + "/" + customerDtupayUuid + "/tokens")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(String.valueOf(amount), MediaType.APPLICATION_JSON));

        if (res.getStatus() >= 400) {
            throw new RuntimeException(res.readEntity(String.class));
        }
        return res.readEntity(new GenericType<List<String>>() {
        });
    }
}
