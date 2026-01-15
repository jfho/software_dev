package dtu.Models;

import java.util.List;
import java.util.ArrayList;

/**
 *  Simple in-memory database, emulating a real database.
 */

public class Database {
    private static final Database INSTANCE = new Database();

    private ArrayList<RecordedPayment> payments = new ArrayList<RecordedPayment>();

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }

    public void addPayment(RecordedPayment payment) {
        payments.add(payment);
    }
   
    public ArrayList<RecordedPayment> listPayments() {
        return payments;
    }

    public void clean() {
        payments = new ArrayList<RecordedPayment>();
    }
}
