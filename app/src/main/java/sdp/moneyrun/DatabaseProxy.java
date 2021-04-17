package sdp.moneyrun;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
     * once the task is completed - by using getPlayerFromTask
     * @param playerId
     * @return Task containing the player data
     */
    public Task<DataSnapshot> getPlayerTask(int playerId){
            Task<DataSnapshot> task = mDataBase.child("players").child(String.valueOf(playerId)).get();
            task.addOnCompleteListener(task1 -> {
                if (!task1.isSuccessful()) {
                    Log.e(TAG, "Error getting data", task1.getException());

                }
                else {
                    Log.d(TAG, String.valueOf(task1.getResult().getValue()));
                }
            });

        return task;
    }

    /** get a player from a task
     * @param task the task containing a player
     * @return the player inside the task or null if the task is not complete
     */
    public Player getPlayerFromTask(Task<DataSnapshot> task){
        if(task.isComplete()){
            return task.getResult().getValue(Player.class);
        }
        else {
            return null;
        }

    }

    /**
     * Will trigger an event each time the player is updated in the database
     * This means that the player should be added first
     * @param player the player who's database entry will be listened
     * @param listener the listener which describes what to do on change
     */
    public void addPlayerListener(Player player, ValueEventListener listener){
        if (listener == null || player == null){
            throw new IllegalArgumentException();
        }
        mDataBase.child("players").child(String.valueOf(player.getPlayerId())).addValueEventListener(listener);
    }


    /**
     * Removes a ValueEventListener from a player entry in the db
     * @param player
     * @param listener
     * @throws IllegalArgumentException on null listener or null player
     */
    public void removePlayerListener(Player player, ValueEventListener listener){
        if (listener == null || player == null){
            throw new IllegalArgumentException();
        }
        mDataBase.child("players").child(String.valueOf(player.getPlayerId())).removeEventListener(listener);
    }

}
