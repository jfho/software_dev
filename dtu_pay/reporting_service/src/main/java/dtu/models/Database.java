/**
 * @author s253874
 */

package dtu.models;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

/**
 *  Simple in-memory database, emulating a real database.
 */

public class Database {
    private static final Database INSTANCE = new Database();

    private CopyOnWriteArrayList<RecordedPayment> payments = new CopyOnWriteArrayList<RecordedPayment>();

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }

    public void addPayment(RecordedPayment payment) {
        payments.add(payment);
    }
   
    public List<RecordedPayment> listPayments() {
        return payments;
    }

    public void clean() {
        payments.clear();
    }
}
