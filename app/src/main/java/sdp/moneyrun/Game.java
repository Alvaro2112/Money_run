package sdp.moneyrun;


import android.app.Activity;
import android.content.Intent;
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
    private  List<Riddle> riddles;


    //Aux variables
    private DatabaseReference rootReference;
    private String id;
    private boolean isVisible;
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
        gameDbData = new GameDbData(name, players, maxPlayerNumber, startLocation, new ArrayList<>());
        this.hasBeenAdded = false;
        this.id = "";
    }

    public Game(String name,List<Player> players,int maxPlayerNumber,List<Riddle> riddles,List<Coin> coins,Location startLocation){
        if(name == null || players == null || startLocation == null || riddles == null || coins == null) {
            throw new IllegalArgumentException("Null parameter passed as argument in Game constructor");
        }
        rootReference = FirebaseDatabase.getInstance().getReference();
        gameDbData = new GameDbData(name, players, maxPlayerNumber, startLocation, coins);
        this.hasBeenAdded = false;
        this.id = "";
    }

    public Game(String gameId,
                String name,
                List<Player> players,
                int maxPlayerCount,
                List<Riddle> riddles,
                List<Coin> coins,
                Location startLocation) {
        if(gameId == null){
            throw new IllegalArgumentException("Game id should not be null.");
        }
        if(name == null){
            throw new IllegalArgumentException("Game name should not be null.");
        }
        if(players == null){
            throw new IllegalArgumentException("Players should not be null.");
        }
        if(riddles == null){
            throw new IllegalArgumentException("Riddles should not be null.");
        }
        if(coins == null){
            throw new IllegalArgumentException("Coins should not be null.");
        }
        if(startLocation  == null){
            throw new IllegalArgumentException("Start location should not be null.");
        }

        this.isVisible = true;
        this.gameDbData = new GameDbData(name, players, maxPlayerCount,startLocation, coins);
        this.id = gameId;
        this.riddles = riddles;
    }

    public Game(String gameId,
                String name,
                boolean isVisible,
                List<Player> players,
                int maxPlayerCount,
                List<Riddle> riddles,
                List<Coin> coins,
                Location startLocation) {
        this(gameId, name, players, maxPlayerCount, riddles, coins, startLocation);
        this.isVisible = isVisible;
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
            linkAttributesToDB();
            hasBeenAdded = true;
            return id;
        }
        return getGameId();
    }



    
    /**
     * Links pertinent attributes to the DB instance corresponding to its ID.
     * For now the only pertitent attribute is the player List
     */
    private void linkAttributesToDB(){
        DatabaseReference gameRef = rootReference.child("open_games").child(id);
        gameRef.child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<Player>> t = new GenericTypeIndicator<List<Player>>() {
                };
                List<Player> newData = snapshot.getValue(t);
                gameDbData.setPlayers(newData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "failed " + error.getMessage());
            }
        });
        gameRef.child("coins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<Coin>> coinIndicator = new GenericTypeIndicator<List<Coin>>() {};
                      List<Coin> newCoinData = snapshot.getValue(coinIndicator);
                        gameDbData.setCoins(newCoinData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "failed " + error.getMessage());
            }
        });
    }

    /**
     * Gets a Serialized Game from the DB in an asynchronous manner
     * @param id ID of the Game to retrieve
     * @return a Task containing the serialized Game or a null value if the game not present in DB
     */
    public static Task<DataSnapshot> getGameDataSnapshot(String id) throws NoSuchElementException{
        if(id == null){throw new IllegalArgumentException();}
        DatabaseReference gamesRef = FirebaseDatabase.getInstance()
                .getReference().child("open_games");
        return gamesRef.child(id).get();
    }


    /**
     * Deserializes DB Game data into a Game instance if the Task is Successfull, returns Null otherwise
     *     This function is to be used in combination with getGameDataSnapsho()
     *     the former retrieves a Task with all the game data in the DB, while this function
     *     extracts the Data and gives you a Game class once the task is complete
     *
     *     For this it is recommended to add a success listener on the task and call this function once it
     *     it is ready
     *
     *     Due to the asynchronous code it is better for the caller to call each function independently
     * @param task Task that you get from getGameDataSnapshot()
     * @return A Game instance
     */
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
            retGame.hasBeenAdded = true;
            return retGame;
        }else{
            return null;
        }
    }

    public void addCoinListener(ValueEventListener listener){
        if (listener == null) throw new IllegalArgumentException();
        FirebaseDatabase.getInstance().getReference().child("open_games").child("coins").addValueEventListener(listener);
    }

    public void removeCoinListener(ValueEventListener listener){
        if (listener == null) throw new IllegalArgumentException();
        FirebaseDatabase.getInstance().getReference().child("open_games").child("coins").removeEventListener(listener);
    }

    /**
     * Sets the players for the Game, or for both the Game and the DB if it has been added
     * @param p New List of Players
     */
    public void setPlayers(List<Player> p){
        if(p == null){throw new IllegalArgumentException();}
        if(p.isEmpty()){throw new IllegalArgumentException("Player List can never be empty (There should always be the host)");}
        if(!hasBeenAdded){
            gameDbData.setPlayers(p);
        }else{
            gameDbData.setPlayers(p);
            rootReference.child("open_games").child(id).child("players").setValue(p);
        }

    }

    public void setCoins(List<Coin> coins){
        if(coins == null) throw new IllegalArgumentException();
        gameDbData.setCoins(coins);
    }

    public boolean setCoin(int index, Coin coin){
        if(index < 0  || coin == null) throw new IllegalArgumentException();
        return gameDbData.setCoin(index, coin);
    }

    /**
     * returns the DataBase id for this Game, if it has been added, or the empty String O.W
     * @return the id of the game
     */
    public String getGameId(){
        return id;
    }

    public boolean getIsVisible(){
        return isVisible;
    }

    public int getPlayerCount(){
        return gameDbData.getPlayers().size();
    }

    public static void endGame(List<Coin> collectedCoins, int playerId, Activity currentActivity) {
        Intent endGameIntent = new Intent(currentActivity, EndGameActivity.class);
        ArrayList<Integer> collectedCoinsValues = new ArrayList<>();
        for (int i = 0; i < collectedCoins.size(); ++i) {
            collectedCoinsValues.add(collectedCoins.get(i).getValue());
        }
        endGameIntent.putExtra("collectedCoins", collectedCoinsValues);
        endGameIntent.putExtra("playerId", playerId);
        currentActivity.startActivity(endGameIntent);
        currentActivity.finish();
    }

    public String getName() {
        return gameDbData.getName();
    }

    public int getMaxPlayerCount() {
        return gameDbData.getMaxPlayerNumber();
    }

    public void setIsVisible(boolean isVisible){
        this.isVisible = isVisible;
    }

    // Launched when create game button is pressed
    public void startGame(){}

    public static void startGame(Game game){
        game.startGame();
    }

    public boolean askPlayer(Player player, Riddle riddle){
        String playerResponse = player.ask(riddle.getQuestion());
        return playerResponse.trim().replaceAll(" ", "").toLowerCase().equals(riddle.getAnswer());
    }

    public GameDbData getGameDbData() {
        return new GameDbData(gameDbData);
    }
    
    public void addGameListener(ValueEventListener l){
            if (l == null) {
                throw new IllegalArgumentException();
            }
            if (hasBeenAdded) {
                rootReference.child("open_games").child(id).addValueEventListener(l);
            }
    }

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

    /**
     * @return returns a random riddle from all the possible riddles
     */
    public Riddle getRandomRiddle() {

        if (riddles.isEmpty()) {
            return null;
        }

        int index = (int) (Math.random() * (riddles.size()));
        return riddles.get(index);
    }
}
