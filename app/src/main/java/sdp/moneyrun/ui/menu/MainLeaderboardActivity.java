package sdp.moneyrun.ui.menu;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.R;
import sdp.moneyrun.database.PlayerDatabaseProxy;
import sdp.moneyrun.menu.MainLeaderboardListAdapter;
import sdp.moneyrun.player.Player;

public class MainLeaderboardActivity extends AppCompatActivity {

    private final int NUM_PLAYERS_LEADERBOARD = 7;

    private ArrayList<Player> playerList = new ArrayList<>();
    private MainLeaderboardListAdapter ldbAdapter;
    private Player user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        user = (Player) getIntent().getSerializableExtra("user");

        addAdapter();
        addPlayersToLeaderboard(NUM_PLAYERS_LEADERBOARD);
    }

    /**
     * @return the leaderboard adapter instance
     */
    public MainLeaderboardListAdapter getLdbAdapter(){
        return ldbAdapter;
    }

    /**
     * @return the player list
     */
    public ArrayList<Player> getPlayerList(){
        return playerList;
    }


    private void addAdapter(){
        // The adapter lets us add item to a ListView easily.
        ldbAdapter = new MainLeaderboardListAdapter(this, playerList, user);
        ListView ldbView = findViewById(R.id.ldblistView);
        ldbView.setAdapter(ldbAdapter);
    }

    private void addPlayersToLeaderboard(int n){
        PlayerDatabaseProxy dp = new PlayerDatabaseProxy();
        dp.getLeaderboardPlayers(n).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                DataSnapshot result = task.getResult();
                if (result == null) {
                    return;
                }

                for (DataSnapshot dataSnapshot : result.getChildren()) {
                    Player player = dataSnapshot.getValue(Player.class);
                    if(player != null){
                        addPlayer(player);
                    }
                }
            }
        });
    }

    /**
     * @param playerList: players to be added to the leaderboard
     *  Adds players to leaderboard
     */
    public void addPlayerList(List<Player> playerList){
        if(playerList == null){
            throw new NullPointerException("player list should not be null.");
        }

        ldbAdapter.addAll(playerList);
    }

    /**
     * @param player: player to be added to the leaderboard
     *  Adds player to leaderboard
     */
    public void addPlayer(Player player){
        if(player == null){
            throw new IllegalArgumentException("player should not be null.");
        }

        List<Player> to_add = new ArrayList<>();
        to_add.add(player);
        addPlayerList(to_add);
    }
}
