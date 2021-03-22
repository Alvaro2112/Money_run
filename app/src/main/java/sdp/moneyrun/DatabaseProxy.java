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


    public void putPlayer(Player player){
        mDataBase.child("players").child(String.valueOf(player.getPlayerId())).setValue(player);
    }


    /**
     * Get the Task from data base. The serialized string can be obtained
     * by doing String.valueOf(task.getResult().getValue())
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

    /*
    Format is {address=FooBarr, numberOfPlayedGames=0, name=John Doe, numberOfDiedGames=0, playerId=1236}
     */
    public Player deserializePlayer(String playerString ){

        String[] split = playerString.split("=");
        int length = split.length;
        for (int i = 1; i < length-1; i++){
            //Starts from 1 because first string will be without values and doesn't go to the last
            //one because there's no , at the end
            split[i] = split[i].substring(0, split[i].indexOf(","));
        }
        split[length - 1] = split[length-1].substring(0, split[length-1].length()-1);
        String address = split[1];
        int nbrPlayedGames = Integer.parseInt(split[2]);
        String name = split[3];
        int numberOfDiedGames = Integer.parseInt(split[4]);
        int playerId = Integer.parseInt(split[5]);
        return new Player(playerId,name,address,numberOfDiedGames,nbrPlayedGames);
    }
}
