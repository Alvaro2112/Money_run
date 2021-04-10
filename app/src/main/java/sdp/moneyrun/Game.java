package sdp.moneyrun;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

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
import java.util.Objects;

// The entirety of the game logic should be implemented in this class
public class Game {
    //Attributes
    private GameDbData gameDbData;

    //Aux variables
    private DatabaseReference rootReference;
    private String id;
    private static final String TAG = Game.class.getSimpleName();
    private boolean hasBeenAdded;

    /**
     * Game constructor
     * @param name game name
     * @param players List of players
     * @param maxPlayerNumber MAximum players
     * @param startLocation game location
     */
    public Game(String name, List<Player> players, int maxPlayerNumber, Location startLocation){
        if(name == null || players == null || startLocation == null) {
            throw new IllegalArgumentException("Null parameter passed as argument in Game constructor");
        }
        rootReference = FirebaseDatabase.getInstance().getReference();
        gameDbData = new GameDbData(name, players, maxPlayerNumber, startLocation);
        this.hasBeenAdded = false;
        this.id = "";
    }

    private Game(GameDbData data, List<Riddle> riddles) {
        if(data == null){throw new IllegalArgumentException("Argument is null");}
        this.gameDbData = new GameDbData(data);
        rootReference = FirebaseDatabase.getInstance().getReference();
        this.hasBeenAdded = true;
    }

    /**
     * Adds the GameData to the database if not already present
     * @return the Id of this game in the DB
     */
    public String addToDB(){
        if(!hasBeenAdded) {
            DatabaseReference openGames = rootReference.child("open_games");
            id = openGames.push().getKey();
            openGames.child(id).setValue(gameDbData);
            //TODO this thing below
            //linkAttributesToDB();

            return id;
        }
        return getGameId();
    }

    /**
     * Links pertinent attributes to the DB instance corresponding to its ID.
     * For now the only pertitent attribute is the player List
     */
    /*private void linkAttributesToDB(){
        DatabaseReference gameRef = rootReference.child("open_games").child(id);
        gameRef.child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<Player>> t = new GenericTypeIndicator<List<Player>>() {};
                List<Player> newData = snapshot.getValue(t);
                gameData.setPlayers(newData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "failed " + error.getMessage());
            }
        });
    }
*/

    public static Task<DataSnapshot> getGameDataSnapshot(String id){
        if(id == null){throw new IllegalArgumentException();}
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

    public static Game getGameFromTaskSnapshot(Task<DataSnapshot> task){
        if(task == null){
            throw new IllegalArgumentException("Null argument");
        }
        if(task.isSuccessful()){
            GenericTypeIndicator<List<Player>> gtP = new GenericTypeIndicator<List<Player>>() {};
            DataSnapshot ds = task.getResult();
            String name = ds.child("name").getValue(String.class);
            List<Player> players = ds.child("players").getValue(gtP);
            int maxPlayers = ds.child("maxPlayerNumber").getValue(Integer.class);

            //Cant deserialize Location properly so we do it manually
            Location locat = new Location("");
            locat.setLatitude(ds.child("startLocation").child("latitude").getValue(Double.class));
            locat.setLongitude(ds.child("startLocation").child("longitude").getValue(Double.class));


            Game retGame = new Game(name,players, maxPlayers, locat);
            retGame.id = ds.getKey();
            return retGame;
        }else{
            return null;
        }
    }


     /**
     * Adds a player to the Game DB representation
     * @param p player to add to the DB
     */
    public void addPlayer(Player p){
        if(p == null){throw new IllegalArgumentException();}
        if(gameDbData.getPlayers().size() < gameDbData.getMaxPlayerNumber() && !gameDbData.getPlayers().contains(p)){
            rootReference.child(id).child("players").child(Integer.toString(p.getPlayerId())).setValue(p);
        }
    }

    /**
     * Removes a player from the Game DB representation
     * @param p The Player to add to the game
    */
    public void removePlayer(Player p){
        if(p == null){throw new IllegalArgumentException();}
        if(gameDbData.getPlayers().contains(p)){
            rootReference.child(id).child("players").child(Integer.toString(p.getPlayerId())).removeValue();
        }
    }


    /**
     * returns the DataBase id for this Game, if it has been added, or the empty String O.W
     * @return the id of the game
     */
    public String getGameId(){
        return id;
    }

    //Launched when start game button is pressed
    public static void startGame(Game game) {
        game.startGame();
    }

    // Launched when create game button is pressed
    public void startGame() {}

    public boolean askPlayer(Player player, Riddle riddle) {
        if(player == null || riddle == null){throw new NullPointerException();}
        String playerResponse = player.ask(riddle.getQuestion());
        return playerResponse.trim().replaceAll(" ", "").toLowerCase().equals(riddle.getAnswer());
    }

    /**
     *
     * @return returns a random riddle from all the possible riddles
     *//*
    public Riddle getRandomRiddle(){
        int index = (int)(Math.random() * (gameData.getRiddles().size()));
        return gameData.getRiddles().get(index);
    }*/

    ///////////////////////////////////////////////
    public GameDbData getGameDbData() {
        return new GameDbData(gameDbData);
    }

    ////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gameDbData.equals(game.gameDbData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameDbData);
    }
}
