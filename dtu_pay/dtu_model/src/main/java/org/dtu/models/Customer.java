package org.dtu.models;

public class Customer {
    private String firstName;
    private String lastName;
    private String cpr;
    
    public Customer(String firstName) {
        this.firstName = firstName;
        this.lastName = "";
        this.cpr = "";
    }
    public Customer(String firstName, String lastName, String cpr) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpr = cpr;
    }
    
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getCpr() {return cpr;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setCpr(String cpr) {this.cpr = cpr;}
}
