package sdp.moneyrun.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseProxy {

    private final static String TAG = DatabaseProxy.class.getSimpleName();
    private static final String ONLINE_MESSAGE = "You are now online";
    private static final String OFFLINE_MESSAGE = "You are now offline";
    private final static DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    @Nullable
    private static ValueEventListener listener = null;
    private static boolean isConnected = false;
    @NonNull
    private final DatabaseReference ref;
    @NonNull
    private final FirebaseDatabase db;

    public DatabaseProxy() {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
    }

    /**
     * @param context
     * @param TAG
     */
    public static void addOfflineListener(Context context, String TAG) {

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class); //this is the db value
                if (connected && !isConnected) {
                    Log.d(TAG, "connected");
                    Toast.makeText(context, ONLINE_MESSAGE, Toast.LENGTH_SHORT).show();
                } else if (!connected && isConnected) {
                    Log.d(TAG, "not connected");
                    Toast.makeText(context, OFFLINE_MESSAGE, Toast.LENGTH_SHORT).show();
                }
                isConnected = connected;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        };
        connectedRef.addValueEventListener(listener);
    }

    /**
     * removes the offline listener if it was attached
     */
    public static void removeOfflineListener() {
        if (listener != null) {
            connectedRef.removeEventListener(listener);
        }
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
