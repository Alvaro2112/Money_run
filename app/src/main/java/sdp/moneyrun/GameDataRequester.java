package sdp.moneyrun;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameDataRequester {

    private final String OPEN_GAMES = "open_games";
    private final String OPEN_GAMES_NAME = "name";
    private final String OPEN_GAMES_MAX_PLAYER_NUMBER = "max_player_number";

    private final ArrayList<Game> games = new ArrayList<>();
    private final DatabaseReference databaseReference;

    public GameDataRequester(DatabaseReference databaseReference){
        this.databaseReference = databaseReference;
    }

    public Task<DataSnapshot> getGameList(){
        return databaseReference
                .child(OPEN_GAMES)
                .get()
                .addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }else{
                games.clear();

                DataSnapshot result = task.getResult();
                if(result == null){
                    return;
                }

                for(DataSnapshot dataSnapshot : result.getChildren()){
                    Game game = defineGameFromDatabase(dataSnapshot);
                    games.add(game);
                }
            }
        });
    }

    private Game defineGameFromDatabase(DataSnapshot dataSnapshot){
        String name = dataSnapshot.child(OPEN_GAMES_NAME).getValue(String.class);
        List<Player> players = new ArrayList<>();
        Integer maxPlayerNumberInteger = dataSnapshot.child(OPEN_GAMES_MAX_PLAYER_NUMBER).getValue(Integer.class);
        int maxPlayerNumber = 0;
        if(maxPlayerNumberInteger != null){
            maxPlayerNumber = maxPlayerNumberInteger;
        }
        List<Riddle> riddles = new ArrayList<>();
        Location startLocation = new Location("");
        startLocation.setLatitude(0.0d);
        startLocation.setLongitude(0.0d);

        return new Game(name, players, maxPlayerNumber, riddles, startLocation);
    }

    public List<Game> getCurrentGameList(){
        return new ArrayList<>(games);
    }
}
