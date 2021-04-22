package sdp.moneyrun.database;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sdp.moneyrun.player.Player;

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
