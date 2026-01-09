package org.dtu;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import dtu.ws.fastmoney.BankServiceException_Exception;

/**
 * Minimal REST resource to create and retire bank accounts.
 * Requires callers to provide an X-API-Key header. The key that
 * creates an account is recorded and only that same key may retire it.
 */
@Path("/bank/accounts")
public class BankResource {
    private final Database db = Database.getInstance();

    public static class AccountRequest {
        public int customerId;
        public int balance;

        public AccountRequest() {}
    }

    public static class AccountResponse {
        public String accountId;
        public AccountResponse() {}
        public AccountResponse(String accountId) { this.accountId = accountId; }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(@HeaderParam("X-API-Key") String apiKey, AccountRequest req) {
        if (apiKey == null || apiKey.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("missing X-API-Key header").build();
        }
        if (req == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("missing body").build();
        }

        try {
            String accountId = db.createBankAccountForCustomer(req.customerId, apiKey, req.balance);
            if (accountId == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("customer not found").build();
            }
            return Response.status(Response.Status.CREATED).entity(new AccountResponse(accountId)).build();
        } catch (BankServiceException_Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{accountId}")
    public Response retireAccount(@HeaderParam("X-API-Key") String apiKey, @PathParam("accountId") String accountId) {
        if (apiKey == null || apiKey.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("missing X-API-Key header").build();
        }
        try {
            boolean ok = db.retireBankAccount(accountId, apiKey);
            if (!ok) {
                // not found or not owner
                // check whether account exists by attempting to see owner
                // reuse db internals are not exposed; return 403 for ownership mismatch and 404 if no owner
                // To keep implementation simple, treat missing as 404 and ownership mismatch as 403
                // We'll check owner via retireBankAccount result (it returns false both cases)
                return Response.status(Response.Status.FORBIDDEN).entity("not authorized to retire this account or account does not exist").build();
            }
            return Response.noContent().build();
        } catch (BankServiceException_Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
