package sdp.moneyrun.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseProxy {

    private final String TAG = DatabaseProxy.class.getSimpleName();

    private final DatabaseReference ref;
    private final FirebaseDatabase db;

    public DatabaseProxy(){
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
    }

    public DatabaseReference getReference(){
        return ref;
    }

    public FirebaseDatabase getDatabase(){
        return db;
    }
}
