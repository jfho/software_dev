package org.dtu.models;

public class TransactionResult {
    private Boolean successful;
    private String error;

    public TransactionResult(Boolean successful, String error) {
        this.successful = successful;
        this.error = error;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public String getError() {
        return error;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }
    public void setError(String error) {
        this.error = error;
    }
}