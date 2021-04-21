package sdp.moneyrun.game;


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

import sdp.moneyrun.database.GameDbData;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.ui.game.EndGameActivity;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.map.Riddle;

// The entirety of the game logic should be implemented in this class
public class Game {

    private static final String TAG = Game.class.getSimpleName();

    //Attributes
    private final GameDbData gameDbData;
    private final List<Riddle> riddles;
    private final List<Coin> coins;

    //Aux variables
    private String id;
    private boolean hasBeenAdded;

    /**
     * This constructor is used to create a game that has never been added to the database.
     * @param name the game name
     * @param host the game host
     * @param maxPlayerCount maximum player count in the game
     * @param riddles riddles of the game
     * @param coins coins in the game
     * @param startLocation location of the game
     * @param isVisible visibility of game in the list
     */
    public Game(String name,
                Player host,
                int maxPlayerCount,
                List<Riddle> riddles,
                List<Coin> coins,
                Location startLocation,
                boolean isVisible) {
        if(name == null){
            throw new IllegalArgumentException("name should not be null.");
        }
        if(host == null){
            throw new IllegalArgumentException("host should not be null.");
        }
        if(riddles == null){
            throw new IllegalArgumentException("riddles should not be null.");
        }
        if(coins == null){
            throw new IllegalArgumentException("coins should not be null.");
        }
        if(startLocation  == null){
            throw new IllegalArgumentException("startLocation should not be null.");
        }
        if(maxPlayerCount <= 0){
            throw new IllegalArgumentException("maxPlayerCount should not be smaller than 1.");
        }

        this.id = null;
        this.hasBeenAdded = false;

        ArrayList<Player> players = new ArrayList<>();
        players.add(host);
        this.gameDbData = new GameDbData(name, host, players, maxPlayerCount, startLocation, isVisible);
        this.riddles = riddles;
        this.coins = coins;
    }

    /**
     * This constructor is used to create an instance of game from retrieved informations
     * from database.
     * @param name the game name
     * @param host the game host
     * @param players players in the game
     * @param maxPlayerCount max player count in the game
     * @param startLocation location of the game
     * @param isVisible visibility of game in the list
     */
    public Game(String name,
                Player host,
                List<Player> players,
                int maxPlayerCount,
                Location startLocation,
                boolean isVisible){
        if(name == null){
            throw new IllegalArgumentException("name should not be null.");
        }
        if(host == null){
            throw new IllegalArgumentException("host should not be null.");
        }
        if(players == null){
            throw new IllegalArgumentException("players should not be null.");
        }
        if(startLocation  == null){
            throw new IllegalArgumentException("startLocation should not be null.");
        }
        if(maxPlayerCount <= 0){
            throw new IllegalArgumentException("maxPlayerCount should not be smaller than 1.");
        }

        this.id = null;
        this.hasBeenAdded = false;

        this.gameDbData = new GameDbData(name, host, players, maxPlayerCount, startLocation, isVisible);
        this.riddles = new ArrayList<>();
        this.coins = new ArrayList<>();
    }

    public String getId(){
        return id;
    }

    public String getName() {
        return gameDbData.getName();
    }

    public Player getHost(){
        return gameDbData.getHost();
    }

    public int getMaxPlayerCount() {
        return gameDbData.getMaxPlayerCount();
    }

    public List<Player> getPlayers(){
        return gameDbData.getPlayers();
    }

    public List<Riddle> getRiddles(){
        return new ArrayList<>(riddles);
    }

    public List<Coin> getCoins(){
        return new ArrayList<>(coins);
    }

    public Location getStartLocation(){
        return gameDbData.getStartLocation();
    }

    public boolean getIsVisible(){
        return gameDbData.getIsVisible();
    }

    public int getPlayerCount(){
        return gameDbData.getPlayers().size();
    }

    public boolean getHasBeenAdded(){
        return hasBeenAdded;
    }

    public GameDbData getGameDbData(){
        return new GameDbData(gameDbData);
    }

    public void setId(String id){
        if(id == null){
            throw new IllegalArgumentException("id should not be null.");
        }

        this.id = id;
    }

    public void setHasBeenAdded(boolean hasBeenAdded){
        this.hasBeenAdded = hasBeenAdded;
    }

    /**
     * Adds the GameData to the database if not already present
     * @return the Id of this game in the DB
     */
    public String addToDB(){
        if(!hasBeenAdded) {
            DatabaseReference openGames = FirebaseDatabase.getInstance().getReference()
                    .child("open_games");
            id = openGames.push().getKey();
            if(id == null){
                throw new NullPointerException("Could not add game to database.");
            }
            openGames.child(id).setValue(gameDbData);
            linkAttributesToDB();
            hasBeenAdded = true;
            return id;
        }
        return getId();
    }

    /**
     * Links pertinent attributes to the DB instance corresponding to its ID.
     * For now the only pertinent attribute is the player List
     */
    private void linkAttributesToDB(){
        DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference()
                .child("open_games")
                .child(id);
        gameRef.child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Player> newData = snapshot.getValue(new GenericTypeIndicator<List<Player>>(){});
                gameDbData.setPlayers(newData);
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
        if(id == null){
            throw new IllegalArgumentException("id should not be null.");
        }

        DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference()
                .child("open_games");
        return gamesRef.child(id).get();
    }

    /**
     * Deserializes DB Game data into a Game instance if the Task is Successfull, returns Null otherwise
     *     This function is to be used in combination with getGameDataSnapshot()
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
            throw new IllegalArgumentException("task should not be null.");
        }

        if(task.isSuccessful()){
            DataSnapshot ds = task.getResult();

            String retName = ds.child("name").getValue(String.class);
            Player retHost = ds.child("host").getValue(Player.class);
            List<Player> retPlayers = ds.child("players").getValue(new GenericTypeIndicator<List<Player>>(){});
            Integer retMaxPlayerCount = ds.child("maxPlayerNumber").getValue(Integer.class);
            Double retLatitude = ds.child("startLocation").child("latitude").getValue(Double.class);
            Double retLongitude = ds.child("startLocation").child("longitude").getValue(Double.class);
            Boolean retIsVisible = ds.child("isVisible").getValue(Boolean.class);
            if(retName == null || retHost == null || retPlayers == null ||
                    retMaxPlayerCount == null || retLatitude == null ||
                    retLongitude == null || retIsVisible == null){
                throw new NullPointerException("Could not retrieve attributes for game.");
            }

            //Cant deserialize Location properly so we do it manually
            Location retLocation = new Location("");
            retLocation.setLatitude(retLatitude);
            retLocation.setLongitude(retLongitude);

            //name, host, maxPlayerCount, startLocation, isVisible

            Game retGame = new Game(retName, retHost, retPlayers, retMaxPlayerCount, retLocation, retIsVisible);
            retGame.setId(ds.getKey());
            retGame.setHasBeenAdded(true);

            return retGame;
        }else{
            return null;
        }
    }

    /**
     * Sets the players for the Game, or for both the Game and the DB if it has been added
     * @param players New List of Players
     */
    public void setPlayers(List<Player> players){
        if(players == null){
            throw new IllegalArgumentException("players should not be null.");
        }
        if(players.isEmpty()){
            throw new IllegalArgumentException("Player List can never be empty (There should always be the host)");
        }

        if(!hasBeenAdded){
            gameDbData.setPlayers(players);
        }else{
            gameDbData.setPlayers(players);
            FirebaseDatabase.getInstance().getReference()
                    .child("open_games")
                    .child(id)
                    .child("players")
                    .setValue(players);
        }
    }

    /**
     * Add a player to the game, updates it in the database if necessary
     * @param player new player
     */
    public void addPlayer(Player player){
        if(player == null){
            throw new IllegalArgumentException("player should not be null.");
        }
        if(getPlayers().contains(player)){
            return;
        }

        List<Player> players = getPlayers();
        players.add(player);

        setPlayers(players);
    }

    /**
     * Remove a player to the game, updates it in the database if necessary
     * @param player the player to be removed
     * @return the player previously at the specified location
     */
    public Player removePlayer(Player player){
        if(player == null){
            throw new IllegalArgumentException("player should not be null.");
        }
        if(!getPlayers().contains(player)){
            return null;
        }

        List<Player> players = getPlayers();
        players.remove(player);

        setPlayers(players);

        return player;
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

    // Launched when create game button is pressed
    public void startGame(){}

    public static void startGame(Game game){
        game.startGame();
    }

    public boolean askPlayer(Player player, Riddle riddle){
        String playerResponse = player.ask(riddle.getQuestion());
        return playerResponse.trim().replaceAll(" ", "").toLowerCase().equals(riddle.getAnswer());
    }

    public void addGameListener(ValueEventListener l){
        if (l == null) {
            throw new IllegalArgumentException();
        }
        if (hasBeenAdded) {
            FirebaseDatabase.getInstance().getReference()
                    .child("open_games")
                    .child(id)
                    .addValueEventListener(l);
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
