package sdp.moneyrun.ui.menu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.database.PlayerDatabaseProxy;
import sdp.moneyrun.menu.LeaderboardListAdapter;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.user.User;

public class LeaderboardActivity extends AppCompatActivity {
    //// for more explanation go to https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#attaching-the-adapter-to-a-listview

    private final List<Player> playerList = new ArrayList<>();
    private final String TAG = LeaderboardActivity.class.getSimpleName();
    User userFromEnd;
    private LeaderboardListAdapter ldbAdapter;
    @Nullable
    private Player user;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Button toMenu = findViewById(R.id.leaderboard_button_end);
        user = (Player) getIntent().getSerializableExtra("user");

        addAdapter();
        setMainPlayer(user);
        //TODO
        // Put addPlayer with local cache
        getEndGamePlayers();
        linkToMenuButton(toMenu);
        DatabaseProxy.addOfflineListener(this, TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseProxy.removeOfflineListener();
    }

    /**
     * @return the leaderboard adapter instance
     */
    public LeaderboardListAdapter getLdbAdapter() {
        return ldbAdapter;
    }

    private void addAdapter() {
        // The adapter lets us add item to a ListView easily.
        ldbAdapter = new LeaderboardListAdapter(this, playerList);
        Helpers.addAdapter(ldbAdapter, this, R.id.ldblistView);
    }

    /**
     * @param playerList: players to be added to the leaderboard
     *                    Adds players to leaderboard
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addPlayerList(@Nullable ArrayList<Player> playerList) {
        Helpers.addObjectListToAdapter(playerList, ldbAdapter);
    }

    /**
     * @param player: player to be added to the leaderboard
     *                Adds player to leaderboard
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addPlayer(@Nullable Player player) {
        // can't just add a player directly to an adapter, we need to put it in a list first
        if (player == null) {
            throw new IllegalArgumentException("player should not be null");
        }
        ArrayList<Player> to_add = new ArrayList<>(Collections.singletonList(player));
        addPlayerList(to_add);
    }

    @NonNull
    public List<Player> getPlayerList() {
        return playerList;
    }

    /**
     * Initializes the player object private instance
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setMainPlayer(@Nullable Player player) {
        if (user == null && player != null) {
            user = player;
        }
        if (user != null)
            addPlayer(user);
    }

    /**
     * @return: returns the player object representing the person that wants to access the leaderboard
     */
    @Nullable
    public Player getUserPlayer() {
        return user;
    }

    /**
     * Gets the players' scores once the game has ended and displays them
     */
    //TODO: when end game is linked to the rest of the game call this method when result button is clicked
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getEndGamePlayers() {
        ldbAdapter.clear();
        int numberOfPlayers = getIntent().getIntExtra("numberOfPlayers", 0);
        for (int i = 0; i < numberOfPlayers; ++i) {
            Player player = (Player) getIntent().getSerializableExtra("players" + i);
            addPlayer(player);
        }
    }

    @Override
    public void onBackPressed() {
        // We disable the user from clicking the back button and force him to use the dedicated button
    }

    /**
     * Sends user to end game screen
     */
    public void linkToMenuButton(@NonNull Button button) {
        userFromEnd = (User) getIntent().getSerializableExtra("userEnd");
        button.setOnClickListener(v -> {
            Intent menuIntent = new Intent(LeaderboardActivity.this, MenuActivity.class);
            menuIntent.putExtra("user", userFromEnd);
            startActivity(menuIntent);
            finish();
        });
    }

}