package dtu;

import java.util.List;

import dtu.Models.Transaction;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class ManagerClient extends BaseClient {

    private String MANAGERS_PATH = "_manager/";
    
    
    public List<Transaction> getReports() {
        return r.path(MANAGERS_PATH + "/reports")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Transaction>>(){}); 
    }   
}
