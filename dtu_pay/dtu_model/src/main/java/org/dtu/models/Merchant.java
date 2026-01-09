package org.dtu.models;

public class Merchant {
    private String firstName;
    private String lastName;
    private String cpr;

    public Merchant() {}

    public Merchant(String firstName, String lastName, String cpr) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpr = cpr;
    }

    public Merchant(String string) {
        this.firstName = string;
        this.lastName = "";
        this.cpr = "";
    }

    public int getMerchantId() {
        if (cpr != null && cpr.length() >= 4) {
            return Integer.parseInt(cpr.substring(cpr.length() - 4));
        }
        return 0;
    }   

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getCpr() {
        return cpr;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setCpr(String cpr) {
        this.cpr = cpr;
    }
}
