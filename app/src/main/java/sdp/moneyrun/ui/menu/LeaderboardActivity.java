package sdp.moneyrun.ui.menu;

import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sdp.moneyrun.R;
import sdp.moneyrun.database.PlayerDatabaseProxy;
import sdp.moneyrun.menu.LeaderboardListAdapter;
import sdp.moneyrun.player.Player;

public class LeaderboardActivity extends AppCompatActivity {
    //// for more explanation go to https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#attaching-the-adapter-to-a-listview

    private final ArrayList<Player> playerList = new ArrayList<>();
    private LeaderboardListAdapter ldbAdapter;
    @Nullable
    private Player user;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void bestToWorstPlayer(@NonNull List<Player> players) {
        players.sort((o1, o2) -> Integer.compare(o2.getScore(), o1.getScore()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        user = (Player) getIntent().getSerializableExtra("user");

        addAdapter();
//        setUserPlayer();
        setMainPlayer(user);
        //TODO
        // Put addPlayer with local cache
        setDummyPlayers();
        //getEndGamePlayers(); //TODO: this function should be called at the end of the game
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
        ListView ldbView = findViewById(R.id.ldblistView);
        ldbView.setAdapter(ldbAdapter);
    }

    /**
     * @param playerList: players to be added to the leaderboard
     *                    Adds players to leaderboard
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addPlayerList(@Nullable ArrayList<Player> playerList) {
        if (playerList == null) {
            throw new NullPointerException("Player list is null");
        }
        ldbAdapter.addAll(playerList);
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < ldbAdapter.getCount(); ++i)
            players.add(ldbAdapter.getItem(i));
        ldbAdapter.clear();
        bestToWorstPlayer(players);
        ldbAdapter.addAll(players);
    }

    /**
     * @param player: player to be added to the leaderboard
     *                Adds player to leaderboard
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addPlayer(@Nullable Player player) {
        // can't just add a player directly to an adapter, need to put it in a list
        if (player == null) {
            throw new IllegalArgumentException("player is null");
        }
        ArrayList<Player> to_add = new ArrayList<>();
        to_add.add(player);
        addPlayerList(to_add);
    }

    @NonNull
    public ArrayList<Player> getPlayerList() {
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
     * This function will set up players in the leaderboard once we know their player ids and names
     * it will set up dummy players before that so that we have a leaderboard nonetheless every time a player joins
     * the game( up to 6 players for now), on data change listeners will be attached to these players here so that
     * once real players join the leaderboard updates accordingly
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setDummyPlayers() {
        PlayerDatabaseProxy databaseProxy = new PlayerDatabaseProxy();
        String[] dummyPlayerNames = {"Josh", "David", "Helena", "Chris", "Bryan"};
        ArrayList<Player> dummies = new ArrayList<>();
        Player dummy1 = new Player(Integer.toString(1000000));
        dummy1.setName("James");
        dummy1.setScore(700);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        addPlayer(dummy1);
        attachListenerToPlayer(dummy1, databaseProxy);
        Random random = new Random();
        for (int i = 2; i < 6; ++i) {
            Player dummy = new Player(Integer.toString(i * 1000000));
            dummy.setName(dummyPlayerNames[i - 1]);
            dummy.setScore(Math.abs(random.nextInt() % 1000));
            dummies.add(dummy);
        }
        bestToWorstPlayer(dummies);
        addPlayerList(dummies);

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
        for (int i = 0; i < numberOfPlayers; ++i) {
            Player player = (Player) getIntent().getSerializableExtra("players" + i);
            addPlayer(player);
        }
    }
}