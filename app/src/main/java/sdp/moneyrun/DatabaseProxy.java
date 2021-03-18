package sdp.moneyrun;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseProxy {
    private final DatabaseReference mDataBase;
    private final String TAG = DatabaseProxy.class.getSimpleName();
    public DatabaseProxy(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        if(db == null) {
            System.out.println("Db is null");
        }
        mDataBase = FirebaseDatabase.getInstance("https://money-run-4f27f-default-rtdb.firebaseio.com/").getReference();

    }


    public void putPlayer(Player player){
        mDataBase.child("players").child(String.valueOf(player.getPlayerId())).setValue(player);
    }

    public String getPlayer(int playerId){
        String retValue;
          mDataBase.child("players").child(String.valueOf(playerId)).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Error getting data", task.getException());
                        }
                        else {
                            Log.d(TAG, String.valueOf(task.getResult().getValue()));

                        }
                    }
                });

    }
}
