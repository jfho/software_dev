package dtu.Controllers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;
import java.util.Collections;

public class TokenController {
    // customerId -> list of tokenIds
    private final ConcurrentMap<String, List<String>> tokenMap = new ConcurrentHashMap<>();
    // tokenId -> customerId
    private final ConcurrentMap<String, String> tokenToCustomer = new ConcurrentHashMap<>();
    // global set of tokenIds to avoid collisions
    private final Set<String> tokens = ConcurrentHashMap.newKeySet();

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
            return existing;
        }

        // initialize a thread-safe list
        List<String> generatedTokens = new CopyOnWriteArrayList<>();
        
        int amountOfTokensToGenerate = Integer.min(6, amount);

        for (int i = 0; i < amountOfTokensToGenerate; i++) {
            String t = createTokenID();
            generatedTokens.add(t);
            tokenToCustomer.put(t, customerID);
        }

        tokenMap.put(customerID, generatedTokens);
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

    public String customerFromToken(String tokenId) {
        return tokenToCustomer.get(tokenId);
    }

    public void clearData() {
        // customerId -> list of tokenIds
        tokenMap.clear();
        // tokenId -> customerId
        tokenToCustomer.clear();
        // global set of tokenIds to avoid collisions
        tokens.clear();
    }

}