/**
 * @author s214881
 */
package dtu.services;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;

import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;
import dtu.models.Transaction;

import java.util.concurrent.ConcurrentMap;
import java.util.Set;
import java.util.Collections;

public class TokenService {
    // customerId -> list of tokenIds
    private final ConcurrentMap<String, List<String>> tokenMap = new ConcurrentHashMap<>();
    // tokenId -> customerId
    private final ConcurrentMap<String, String> tokenToCustomer = new ConcurrentHashMap<>();
    // global set of tokenIds to avoid collisions
    private final Set<String> tokens = ConcurrentHashMap.newKeySet();
    
    private MessageQueue queue;

    private String createTokensRequest = "TokensRequested";
    private String createTokensResponse = "TokensGenerated";
    private String customerIdRequest = "PaymentRequested";
    private String customerIdResponse = "TokenValidated";
    private String CustomerDeleted = "CustomerDeletionRequested";

    private static final Logger LOG = Logger.getLogger(TokenService.class);

    public TokenService(MessageQueue q) {
        LOG.info("Starting token service");
        queue = q;
        queue.addHandler(createTokensRequest, this::policyCreateTokens);
        queue.addHandler(customerIdRequest, this::policyValidateToken);
        queue.addHandler(CustomerDeleted, this::policyCustomerDeleted);
    }


    public void policyCustomerDeleted(Event event) {
        String customerId = event.getArgument(0, String.class);
        
        deleteCustomer(customerId);

    }

    private void deleteCustomer(String customerId) {
        List<String> customerTokens = tokenMap.get(customerId);
        customerTokens.forEach(token -> {
            tokenToCustomer.remove(token);
        });
        tokenMap.remove(customerId);
    }

    public void policyCreateTokens(Event event) {
        LOG.info("Creating tokens");
        String customerId = event.getArgument(0, String.class);
        int amount = event.getArgument(1, Integer.class);
        String corrId = event.getArgument(2, String.class);
        
        List<String> tokenList = createTokens(customerId, amount);

        LOG.info("publishing token list");
        queue.publish(new Event(createTokensResponse, new Object[] {tokenList, corrId} )); 
    }

    public void policyValidateToken(Event event) {
        LOG.info("Checking token");
        Transaction transaction = event.getArgument(0, Transaction.class);
        String tokenId = transaction.tokenId();
        String corrId = event.getArgument(1, String.class);
        String customerId = validateToken(tokenId);

        LOG.info("publishing token: " + customerId);
        queue.publish(new Event(customerIdResponse, new Object[] {customerId, corrId} )); 
    }

    private String createTokenID() {
        String tokenID = UUID.randomUUID().toString();
        if (tokens.contains(tokenID)) {
            return createTokenID();
        } else {
            tokens.add(tokenID);
            return tokenID;
        }
    }

    public List<String> createTokens(String customerID, int amount) {
        // return existing if present or too large
        List<String> existing = tokenMap.get(customerID);
        
        if (existing != null && existing.size() > 1) {
            return Collections.emptyList();
        }

        if (amount > 5 || amount <= 0) {
            return Collections.emptyList();
        }

        // initialize a thread-safe list
        List<String> generatedTokens = new CopyOnWriteArrayList<>();
        
        for (int i = 0; i < amount; i++) {
            String t = createTokenID();
            generatedTokens.add(t);
            tokenToCustomer.put(t, customerID);
        }

        tokenMap.computeIfAbsent(customerID, k -> new CopyOnWriteArrayList<>()).addAll(generatedTokens);
        return generatedTokens;
    }

    public String validateToken(String tokenID) {
        // Pop customerId or null
        String customerId = tokenToCustomer.remove(tokenID);

        // Remove tokenId from customer 
        if (customerId != null) {
            tokenMap.get(customerId).remove(tokenID);
        }

        // return customerId or null
        return customerId;
    }

    public List<String> getAllTokensByCustomer(String customerID) {
        return tokenMap.getOrDefault(customerID, Collections.emptyList());
    }

    public void clearData() {
        // customerId -> list of tokenIds
        tokenMap.clear();
        // tokenId -> customerId
        tokenToCustomer.clear();
        // global set of tokenIds to avoid collisions
        tokens.clear();
    }

    public String getCustomerFromToken(String tokenID) {
        return tokenToCustomer.get(tokenID);
    }

}