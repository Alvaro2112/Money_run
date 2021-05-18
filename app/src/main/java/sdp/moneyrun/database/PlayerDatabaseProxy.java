package sdp.moneyrun.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.player.Player;

@SuppressWarnings("FieldCanBeLocal")
public class PlayerDatabaseProxy extends DatabaseProxy {

    private final String TAG = PlayerDatabaseProxy.class.getSimpleName();

    private final String DATABASE_PLAYER = "players";
    private final String DATABASE_PLAYER_SCORE = "score";

    @NonNull
    private final DatabaseReference playersRef;

    public PlayerDatabaseProxy() {
        super();

        playersRef = getReference().child(DATABASE_PLAYER);
    }

    /**
     * Add a player to the database. If the player id already exists, erases previously kept data
     *
     * @param player the player to be put in the database
     */
    public void putPlayer(@Nullable Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player should not be null");
        }

        playersRef.child(String.valueOf(player.getPlayerId())).setValue(player);
    }

    public void putPlayer(@Nullable Player player, @Nullable OnCompleteListener listener) {
        if (player == null || listener == null) {
            throw new IllegalArgumentException();
        }
        playersRef.child(String.valueOf(player.getPlayerId()))
                .setValue(player)
                .addOnCompleteListener(listener);

    }

    /**
     * Remove a player to the database.
     *
     * @param player the player to be removed in the database
     */
    public void removePlayer(@Nullable Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player should not be null");
        }

        playersRef.child(String.valueOf(player.getPlayerId())).removeValue();
    }

    /**
     * Get the Task (asynchronous !) from data base. The player instance can be retrieved -
     * once the task is completed - by using getPlayerFromTask
     *
     * @param playerId
     * @return Task containing the player data
     */
    @NonNull
    public Task<DataSnapshot> getPlayerTask(String playerId) {
        Task<DataSnapshot> task = playersRef.child(String.valueOf(playerId)).get();

        task.addOnCompleteListener(task1 -> {
            if (!task1.isSuccessful()) {
                Log.e(TAG, "Error getting data", task1.getException());

            } else {
                Log.d(TAG, String.valueOf(task1.getResult().getValue()));
            }
        });

        return task;
    }

    /**
     * get a player from a task
     *
     * @param task the task containing a player
     * @return the player inside the task or null if the task is not complete
     */
    @Nullable
    public Player getPlayerFromTask(@NonNull Task<DataSnapshot> task) {
        if (task.isComplete()) {
            return task.getResult().getValue(Player.class);
        } else {
            return null;
        }

    }

    /**
     * Will trigger an event each time the player is updated in the database
     * This means that the player should be added first
     *
     * @param player   the player who's database entry will be listened
     * @param listener the listener which describes what to do on change
     */
    public void addPlayerListener(@Nullable Player player, @Nullable ValueEventListener listener) {
        Helpers.addOrRemoveListener(player, listener, playersRef, false);

    }


    /**
     * Removes a ValueEventListener from a player entry in the db
     *
     * @param player
     * @param listener
     * @throws IllegalArgumentException on null listener or null player
     */
    public void removePlayerListener(@Nullable Player player, @Nullable ValueEventListener listener) {
        Helpers.addOrRemoveListener(player, listener, playersRef, true);

    }

    /**
     * Returns the top players ordered by their score from the database.
     *
     * @param n the number of players to retrieve from the database
     * @return the task
     */
    @NonNull
    public Task<DataSnapshot> getLeaderboardPlayers(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n should not be negative.");
        }

        return playersRef.orderByChild(DATABASE_PLAYER_SCORE)
                .limitToLast(n)
                .get();
    }
}
