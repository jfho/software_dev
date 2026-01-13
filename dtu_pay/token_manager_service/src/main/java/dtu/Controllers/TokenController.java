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

    public List<String> createToken(String customerID) {
        // return existing if present
        List<String> existing = tokenMap.get(customerID);
        if (existing != null) {
            return existing;
        }

        // initialize a thread-safe list
        List<String> initTokens = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 6; i++) {
            String t = createTokenID();
            initTokens.add(t);
            tokenToCustomer.put(t, customerID);
        }

        List<String> prev = tokenMap.putIfAbsent(customerID, initTokens);
        return prev == null ? initTokens : prev;
    }

    public boolean validateToken(String tokenID) {
        return tokenToCustomer.containsKey(tokenID);
    }

    public List<String> getAllTokensByCustomer(String customerID) {
        return tokenMap.getOrDefault(customerID, Collections.emptyList());
    }

    public String customerFromToken(String tokenId) {
        return tokenToCustomer.get(tokenId);
    }

    public List<String> createTokenHELPER(String customerID, int amount) {
        // return existing if present
        List<String> existing = tokenMap.get(customerID);
        if (existing != null) {
            return existing;
        }

        // initialize a thread-safe list
        List<String> initTokens = new CopyOnWriteArrayList<>();
        for (int i = 0; i < amount; i++) {
            String t = createTokenID();
            initTokens.add(t);
            tokenToCustomer.put(t, customerID);
        }

        List<String> prev = tokenMap.putIfAbsent(customerID, initTokens);
        return prev == null ? initTokens : prev;
    }
}