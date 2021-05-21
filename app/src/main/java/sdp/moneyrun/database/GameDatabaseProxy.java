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

@SuppressWarnings("FieldCanBeLocal")
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
    private final String DATABASE_GAME_RADIUS = "radius";
    private final String DATABASE_GAME_NUMCOINS = "numCoins";
    private final String DATABASE_GAME_DURATION = "duration";
    private final String DATABASE_GAME_STARTED = "started";

    @NonNull
    private final DatabaseReference gamesRef;

    public GameDatabaseProxy() {
        super();

        gamesRef = getReference().child(DATABASE_GAME);
    }

    /**
     * Adds the GameData to the database if not already present
     *
     * @return the Id of this game in the DB
     */
    @Nullable
    public String putGame(@Nullable Game game) {
        if (game == null) {
            throw new IllegalArgumentException("game should not be null.");
        }
        if (game.getHasBeenAdded()) {
            return game.getId();
        }

        String id = game.getId() != null ? game.getId() : gamesRef.push().getKey();
        if (id == null) {
            throw new IllegalArgumentException("Could not add game to database, id is null.");
        }
        game.setId(id);
        gamesRef.child(id).setValue(game.getGameDbData());
        linkPlayersToDB(game);
        linkCoinsToDB(game);
        game.setHasBeenAdded(true);
        game.setStarted(false, false);
        return id;
    }

    public void updateGameInDatabase(@Nullable Game game, @Nullable OnCompleteListener listener) {
        if (game == null) throw new IllegalArgumentException();
        if (!game.getHasBeenAdded()) {
            putGame(game);
        }
        if (listener != null) {
            gamesRef.child(game.getId()).setValue(game.getGameDbData()).addOnCompleteListener(listener);
        } else {
            gamesRef.child(game.getId()).setValue(game.getGameDbData());
        }
    }

    /**
     * Links pertinent attributes to the DB instance corresponding to its ID.
     * For now the only pertinent attribute is the player List
     */
    private void linkPlayersToDB(@Nullable Game game) {
        if (game == null) {
            throw new IllegalArgumentException("game should not be null.");
        }

        gamesRef.child(game.getId())
                .child(DATABASE_GAME_PLAYERS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Player> newData = snapshot.getValue(new GenericTypeIndicator<List<Player>>() {
                        });
                        if (newData != null) {
                            game.setPlayers(newData, true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "failed " + error.getMessage());
                    }
                });
    }

    private void linkCoinsToDB(@NonNull Game game) {
        gamesRef.child(game.getId())
                .child(DATABASE_COIN)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "failed " + error.getMessage());
                    }
                });
    }


    /**
     * Gets a Serialized Game from the DB in an asynchronous manner
     *
     * @param id ID of the Game to retrieve
     * @return a Task containing the serialized Game or a null value if the game not present in DB
     */
    @NonNull
    public Task<DataSnapshot> getGameDataSnapshot(@Nullable String id) throws NoSuchElementException {
        if (id == null) {
            throw new IllegalArgumentException("id should not be null.");
        }

        return gamesRef.child(id).get();
    }

    /**
     * Deserializes DB Game data into a Game instance if the Task is Successfully, returns Null otherwise
     * This function is to be used in combination with getGameDataSnapshot()
     * the former retrieves a Task with all the game data in the DB, while this function
     * extracts the Data and gives you a Game class once the task is complete
     * <p>
     * For this it is recommended to add a success listener on the task and call this function once it
     * it is ready
     * <p>
     * Due to the asynchronous code it is better for the caller to call each function independently
     *
     * @param task Task that you get from getGameDataSnapshot()
     * @return A Game instance
     */
    @Nullable
    public Game getGameFromTaskSnapshot(@Nullable Task<DataSnapshot> task) {
        if (task == null)
            throw new IllegalArgumentException("task should not be null.");

        Game toReturn = null;

        if (task.isSuccessful()) {

            DataSnapshot ds = task.getResult();

            String retName = getDatabaseValue(ds, DATABASE_GAME_NAME, String.class);
            Player retHost = getDatabaseValue(ds, DATABASE_GAME_HOST, Player.class);
            Integer retMaxPlayerCount = getDatabaseValue(ds, DATABASE_GAME_MAX_PLAYER_COUNT, Integer.class);

            List<Player> retPlayers = ds.child(DATABASE_GAME_PLAYERS).getValue(new GenericTypeIndicator<List<Player>>() {});
            List<Coin> retCoin = ds.child(DATABASE_COIN).getValue(new GenericTypeIndicator<List<Coin>>() {});

            Double retLatitude = ds.child(DATABASE_GAME_START_LOCATION)
                    .child(DATABASE_LOCATION_LATITUDE)
                    .getValue(Double.class);
            Double retLongitude = ds.child(DATABASE_GAME_START_LOCATION)
                    .child(DATABASE_LOCATION_LONGITUDE)
                    .getValue(Double.class);

            Boolean retIsVisible = getDatabaseValue(ds, DATABASE_GAME_IS_VISIBLE, Boolean.class);
            Boolean started = getDatabaseValue(ds, DATABASE_GAME_STARTED, Boolean.class);
            Integer numCoins = getDatabaseValue(ds, DATABASE_GAME_NUMCOINS, Integer.class);
            Double radius = getDatabaseValue(ds, DATABASE_GAME_RADIUS, Double.class);
            Double duration = getDatabaseValue(ds, DATABASE_GAME_DURATION, Double.class);

            if (retCoin == null)
                retCoin = new ArrayList<>();

            if (retPlayers == null)
                throw new IllegalArgumentException("players should not be null.");

                if (retMaxPlayerCount == null)
                throw new IllegalArgumentException("max player count should not be null.");

            if (retLatitude == null)
                throw new IllegalArgumentException("latitude should not be null.");

            if (retLongitude == null)
                throw new IllegalArgumentException("longitude should not be null.");

            //Cant deserialize Location properly so we do it manually
            Location retLocation = new Location("");
            retLocation.setLatitude(retLatitude);
            retLocation.setLongitude(retLongitude);


            Game retGame = new Game(retName, retHost, retPlayers, retMaxPlayerCount, retLocation, retIsVisible, retCoin, numCoins, radius, duration);
            retGame.setId(ds.getKey());
            retGame.setHasBeenAdded(true);
            retGame.setStarted(started, true);

            toReturn = retGame;
        }

        return toReturn;
    }

    public void addGameListener(@Nullable Game game, @Nullable ValueEventListener l) {
      checkGameListenerMethodsArguments(game,l);
        if (game.getHasBeenAdded()) {
            gamesRef.child(game.getId()).addValueEventListener(l);
        }
    }

    public void removeGameListener(Game game, ValueEventListener listener){
      checkGameListenerMethodsArguments(game,listener);
        if (game.getHasBeenAdded()) {
            gamesRef.child(game.getId()).removeEventListener(listener);
        }
    }

    private void checkGameListenerMethodsArguments(Game game, ValueEventListener listener){
        if (game == null) {
            throw new IllegalArgumentException("game should not be null.");
        }
        if (listener == null) {
            throw new IllegalArgumentException("event listener should not be null.");
        }
    }


    public void addCoinListener(@Nullable Game game, @Nullable ValueEventListener listener) {
        if (listener == null || game == null) throw new IllegalArgumentException();
        getDatabaseChildOfGame(game, DATABASE_COIN).addValueEventListener(listener);
    }

    public void removeCoinListener(@Nullable Game game, @Nullable ValueEventListener listener) {
        if (listener == null || game == null) throw new IllegalArgumentException();
        getDatabaseChildOfGame(game, DATABASE_COIN).removeEventListener(listener);
    }

    @NonNull
    public DatabaseReference getDatabaseChildOfGame(@NonNull Game game, @NonNull String variable){
        return gamesRef.child(game.getId())
                .child(variable);
    }

    @Nullable
    public <T> T getDatabaseValue(@NonNull DataSnapshot ds, @NonNull String variable, @NonNull Class<T> type){
        T value = ds.child(variable).getValue(type);
        if (value == null) {
            throw new IllegalArgumentException(variable + " should not be null.");
        }

        return value;
    }
}
