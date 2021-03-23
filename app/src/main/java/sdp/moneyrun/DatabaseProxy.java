package sdp.moneyrun;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        mDataBase = FirebaseDatabase.getInstance().getReference();

    }


    /**
     * Add a player to the database. If the player id already exists, erases previously kept data
     * @param player the player to be put in the database
     */
    public void putPlayer(Player player){
        mDataBase.child("players").child(String.valueOf(player.getPlayerId())).setValue(player);
    }


    /**
     * Get the Task (asynchronous !) from data base. The player instance can be retrieved -
     * once the task is completed - by doing String.valueOf(task.getResult().getValue())
     * @param playerId
     * @return Task containing the player data
     */
    public Task<DataSnapshot> getPlayer(int playerId){
        Task<DataSnapshot> task = mDataBase.child("players").child(String.valueOf(playerId)).get()
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
        return task;

    }

}
