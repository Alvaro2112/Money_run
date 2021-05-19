package sdp.moneyrun.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseProxy {

    @NonNull
    private final DatabaseReference ref;
    @NonNull
    private final FirebaseDatabase db;

    public DatabaseProxy() {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
    }

    @NonNull
    public DatabaseReference getReference() {
        return ref;
    }

    @NonNull
    public FirebaseDatabase getDatabase() {
        return db;
    }
}
