package sdp.moneyrun;

import android.location.Location;
import android.renderscript.Sampler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// The entirety of the game logic should be implemented in this class
public class Game {
    //Attributes
    private GameData gameData;

    //Aux variables
    private DatabaseReference rootReference;
    private String id;
    private static final String TAG = Game.class.getSimpleName();
    private boolean hasBeenAdded;

    /*
    Create a Game instance by entering all its attributes -> typically used when creating a game instance
    for the first time
     */
    public Game(String name, List<Player> players, int maxPlayerNumber, List<Riddle> riddles, Location startLocation){
        if(name == null || players == null || riddles == null || startLocation == null) {
            throw new IllegalArgumentException("Null parameter passed as argument in Game constructor");
        }
        rootReference = FirebaseDatabase.getInstance().getReference();
        gameData = new GameData(name, players, maxPlayerNumber, riddles, startLocation);
        this.hasBeenAdded = false;
        this.id = addToDB();
    }

    public Game(GameData data) {
        if(data == null){throw new IllegalArgumentException("Argument is null");}
        this.gameData = new GameData(data);
        rootReference = FirebaseDatabase.getInstance().getReference();
        this.hasBeenAdded = false;
        this.id = addToDB();
    }

    public Game(){}


    /*
    Adds Game to the Database and gives it an ID
    returns the game ID
    */
    private String addToDB(){
        DatabaseReference openGames = rootReference.child("open_games");
        id = openGames.push().getKey();
        openGames.child(id).setValue(gameData);
        linkAttributesToDB();
        return id;
    }

    /*
    Links pertinent attributes to the DB instance corresponding to its ID.
    For now the only pertitent attribute is the player List
   */
    private void linkAttributesToDB(){
        DatabaseReference gameRef = rootReference.child("open_games").child(id);
        gameRef.child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<Player>> t = new GenericTypeIndicator<List<Player>>() {};
                List<Player> newData = snapshot.getValue(t);
                //gameData.setPlayers(newData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO smth
            }
        });

    }
    
    public static Task<DataSnapshot> getGameDataSnapshot(String id){
        if(id == null){throw new NullPointerException();}
        DatabaseReference gamesRef = FirebaseDatabase.getInstance()
                .getReference().child("open_games");

        //check if there is a game in the DB with this ID
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(id)) {
                    throw new NoSuchElementException("There is no game with ID : " + id + " in the DB");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "failed : "+error.getMessage());
            }
        });

        Task<DataSnapshot> returnSnap = gamesRef.child(id).get();
        returnSnap.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "successful " + task.getResult().getValue(String.class));
            }else{
                Log.e(TAG, "failed " + task.getException());
            }
        });
        return returnSnap;
    }

    public GameData getGameFromSnapshot(Task<DataSnapshot> task){
        if(task.isComplete()){
            return task.getResult().getValue(GameData.class);
        }else{
            return null;
        }
    }

    //TODO First test the rest of the class, and test the way that addValue serializes
    //the player list before you go on manually messing up stuff;
    /*
    Adds a player to the DB, which then adds it to all game instances with the same ID via the listener

    public void addPlayer(Player p){
        if(p == null){throw new NullPointerException();}
        if(gameData.getPlayers().size() < gameData.getMaxPlayerNumber() && !gameData.getPlayers().contains(p)){
            rootReference.child(id).child("players").child(Integer.toString(p.getPlayerId())).setValue(p);
        }
    }

    /*
    Removes a player from the DB which in turn will remove it from all game instances with the same ID via
    the listener

    public void removePlayer(Player p){
        if(p == null){throw new NullPointerException();}
        if(gameData.getPlayers().contains(p)){
            rootReference.child(id).child("players").child(Integer.toString(p.getPlayerId())).removeValue();
        }
    }
    */

    public String getGameId(){
        return id;
    }

    // Launched when create game button is pressed - (Rafa) Nope, launched when start game button is pressed
    public void startGame(){

    }

    public boolean askPlayer(Player player, Riddle riddle){
        if(player == null || riddle == null){throw new NullPointerException();}
        String playerResponse = player.ask(riddle.getQuestion());
        return playerResponse.trim().replaceAll(" ", "").toLowerCase().equals(riddle.getAnswer());
    }
}
