package sdp.moneyrun.ui.menu;

import android.app.UiAutomation;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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
import java.util.Random;
import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.PlayerDatabaseProxy;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.menu.LeaderboardListAdapter;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.game.EndGameActivity;
import sdp.moneyrun.user.User;

public class LeaderboardActivity extends AppCompatActivity {
    //// for more explanation go to https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#attaching-the-adapter-to-a-listview

    private final List<Player> playerList = new ArrayList<>();
    private LeaderboardListAdapter ldbAdapter;
    @Nullable
    private Player user;
    private String playerId;
    User userFromEnd;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        user = (Player) getIntent().getSerializableExtra("user");

        addAdapter();
        setMainPlayer(user);
        //TODO
        // Put addPlayer with local cache
        getEndGamePlayers();
        linkToMenuButton();
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
     * @param dummy1:        dummy representation of a player that will later evolve into a player that was in a game
     * @param databaseProxy: the proxy database that we use to access Firebase
     *                       Attaches a lister to a player so that once real players join the game the dummy player will represent
     *                       an actual person with all the player object attributes associated with it
     */
    private void attachListenerToPlayer(@NonNull Player dummy1, @NonNull PlayerDatabaseProxy databaseProxy) {
        databaseProxy.addPlayerListener(dummy1, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player update = snapshot.getValue(Player.class);
                System.out.println(snapshot.getValue(Player.class) + "Getting snapshot on data change in leaderboard class");
                if (update != null)
                    dummy1.setName(update.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        userFromEnd = (User) getIntent().getSerializableExtra("userEnd");
        for (int i = 0; i < numberOfPlayers; ++i) {
            Player player = (Player) getIntent().getSerializableExtra("players" + i);
            addPlayer(player);
        }
    }

    @Override
    public void onBackPressed() {
        // We disable the user from clicking the back button and force him to use the dedicated button
        return;
    }

    /**
     * Sends user to end game screen
     */
    public void linkToMenuButton(){
        Button toMenu = findViewById(R.id.leaderboard_button_end);
        toMenu.setOnClickListener( v -> {
            Intent menuIntent = new Intent(LeaderboardActivity.this,MenuActivity.class);
            menuIntent.putExtra("user",userFromEnd);
            startActivity(menuIntent);
            finish();
        });
    }
}