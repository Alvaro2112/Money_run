package sdp.moneyrun.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseProxy {

    private final DatabaseReference ref;

    public DatabaseProxy() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        ref = db.getReference();
    }

    public DatabaseReference getReference() {
        return ref;
    }
}
