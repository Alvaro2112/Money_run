package sdp.moneyrun.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseProxy {

    @NonNull
    private final DatabaseReference ref;

    public DatabaseProxy() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        ref = db.getReference();
    }

    @NonNull
    public DatabaseReference getReference() {
        return ref;
    }
}
