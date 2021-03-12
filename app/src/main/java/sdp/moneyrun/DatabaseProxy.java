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

    public DatabaseProxy(){
        mDataBase = FirebaseDatabase.getInstance().getReference();
    }

    public void putPlayer(Player player){
        mDataBase.child("players").child(String.valueOf(player.getPlayerId())).setValue(player);
    }

    public String getPlayer(int playerId){
        return mDataBase.child("players").child(String.valueOf(playerId)).get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(!task.isSuccessful()){
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else{
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        }
                    }
                }
        ).getResult().toString();
    }
}
