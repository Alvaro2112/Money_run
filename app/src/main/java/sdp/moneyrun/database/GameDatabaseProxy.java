package sdp.moneyrun.database;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import sdp.moneyrun.game.Game;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.player.Player;

public class GameDatabaseProxy extends DatabaseProxy {

    private final String TAG = GameDatabaseProxy.class.getSimpleName();
    private final String DATABASE_GAME = "games";
    private final String DATABASE_GAME_NAME = "name";
    private final String DATABASE_GAME_HOST = "host";
    private final String DATABASE_GAME_MAX_PLAYER_COUNT = "maxPlayerCount";
    private final String DATABASE_GAME_PLAYERS = "players";
    private final String DATABASE_GAME_START_LOCATION = "startLocation";
    private final String DATABASE_GAME_IS_VISIBLE = "isVisible";
    private final String DATABASE_LOCATION_LATITUDE = "latitude";
    private final String DATABASE_LOCATION_LONGITUDE = "longitude";
    private final String DATABASE_COIN = "coins";

    private final DatabaseReference gamesRef;

    public GameDatabaseProxy(){
        super();

        gamesRef = getReference().child(DATABASE_GAME);
    }

    /**
     * Adds the GameData to the database if not already present
     * @return the Id of this game in the DB
     */
    public String putGame(Game game){
        if(game == null){
            throw new IllegalArgumentException("game should not be null.");
        }
        if(game.getHasBeenAdded()) {
            return game.getId();
        }

        String id = game.getId() != null ? game.getId() : gamesRef.push().getKey();
        if(id == null){
            throw new IllegalArgumentException("Could not add game to database, id is null.");
        game.setId(id);
        gamesRef.child(id).setValue(game.getGameDbData());
        linkPlayersToDB(game);
        linkCoinsToDB(game);
        game.setHasBeenAdded(true);
        game.setStarted(false,false);
        return id;
    }

    public void updateGameInDatabase(Game game, @Nullable OnCompleteListener listener){
        if(game == null) throw new IllegalArgumentException();
        if(!game.getHasBeenAdded()){
            putGame(game);
        }
        if(listener != null){
            gamesRef.child(game.getId()).setValue(game.getGameDbData()).addOnCompleteListener(listener);
        }
        else{
            gamesRef.child(game.getId()).setValue(game.getGameDbData());
        }
    }

    /**
     * Links pertinent attributes to the DB instance corresponding to its ID.
     * For now the only pertinent attribute is the player List
     */
    private void linkPlayersToDB(Game game){
        if(game == null){
            throw new IllegalArgumentException("game should not be null.");
        }

        gamesRef.child(game.getId())
                .child(DATABASE_GAME_PLAYERS)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Player> newData = snapshot.getValue(new GenericTypeIndicator<List<Player>>(){});
                if(newData != null){
                    game.setPlayers(newData, true);
                }            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "failed " + error.getMessage());
            }
        });
    }

    private void linkCoinsToDB(Game game) {
        gamesRef.child(game.getId())
                .child(DATABASE_COIN)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<List<Player>> t = new GenericTypeIndicator<List<Player>>() {
                        };
                        GenericTypeIndicator<List<Coin>> coinIndicator = new GenericTypeIndicator<List<Coin>>() {
                        };
                        List<Coin> newCoinData = snapshot.getValue(coinIndicator);
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
    public Task<DataSnapshot> getGameDataSnapshot(String id) throws NoSuchElementException {
        if(id == null){
            throw new IllegalArgumentException("id should not be null.");
        }

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
    public Game getGameFromTaskSnapshot(Task<DataSnapshot> task){
        if(task == null){
            throw new IllegalArgumentException("task should not be null.");
        }

        if(task.isSuccessful()){
            DataSnapshot ds = task.getResult();

            String retName = ds.child(DATABASE_GAME_NAME).getValue(String.class);
            Player retHost = ds.child(DATABASE_GAME_HOST).getValue(Player.class);
            List<Player> retPlayers = ds.child(DATABASE_GAME_PLAYERS).getValue(new GenericTypeIndicator<List<Player>>(){});
            List<Coin> retCoin = ds.child(DATABASE_COIN).getValue(new GenericTypeIndicator<List<Coin>>(){});
            if(retCoin == null){
                retCoin = new ArrayList<>();
            }
            Integer retMaxPlayerCount = ds.child(DATABASE_GAME_MAX_PLAYER_COUNT).getValue(Integer.class);
            Double retLatitude = ds.child(DATABASE_GAME_START_LOCATION)
                    .child(DATABASE_LOCATION_LATITUDE)
                    .getValue(Double.class);
            Double retLongitude = ds.child(DATABASE_GAME_START_LOCATION)
                    .child(DATABASE_LOCATION_LONGITUDE)
                    .getValue(Double.class);
            Boolean retIsVisible = ds.child(DATABASE_GAME_IS_VISIBLE).getValue(Boolean.class);
            if(retName == null){
                throw new IllegalArgumentException("name should not be null.");
            }
            if(retHost == null){
                throw new IllegalArgumentException("host should not be null.");
            }
            if(retPlayers == null){
                throw new IllegalArgumentException("players should not be null.");
            }
            if(retMaxPlayerCount == null){
                throw new IllegalArgumentException("max player count should not be null.");
            }
            if(retLatitude == null){
                throw new IllegalArgumentException("latitude should not be null.");
            }
            if(retLongitude == null){
                throw new IllegalArgumentException("longitude should not be null.");
            }
            if(retIsVisible == null){
                throw new IllegalArgumentException("is visible should not be null.");
            }

            //Cant deserialize Location properly so we do it manually
            Location retLocation = new Location("");
            retLocation.setLatitude(retLatitude);
            retLocation.setLongitude(retLongitude);

            //name, host, maxPlayerCount, startLocation, isVisible

            Game retGame = new Game(retName, retHost, retPlayers, retMaxPlayerCount, retLocation, retIsVisible, retCoin);
            retGame.setId(ds.getKey());
            retGame.setHasBeenAdded(true);

            return retGame;
        }else{
            return null;
        }
    }

    public void addGameListener(Game game, ValueEventListener l){
        if(game == null){
            throw new IllegalArgumentException("game should not be null.");
        }
        if (l == null) {
            throw new IllegalArgumentException("event listener should not be null.");
        }
        if (game.getHasBeenAdded()) {
            gamesRef.child(game.getId()).addValueEventListener(l);
        }
    }

    public void addCoinListener(Game game, ValueEventListener listener){
        if (listener == null || game == null) throw new IllegalArgumentException();
        gamesRef.child(game.getId())
                .child(DATABASE_COIN)
                .addValueEventListener(listener);
    }

    public void removeCoinListener(Game game, ValueEventListener listener){
        if (listener == null || game == null) throw new IllegalArgumentException();
        gamesRef.child(game.getId())
                .child(DATABASE_COIN)
                .removeEventListener(listener);
    }
}
