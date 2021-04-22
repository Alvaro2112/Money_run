package sdp.moneyrun.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.menu.MenuActivity;

import static sdp.moneyrun.game.Game.getGameDataSnapshot;
import static sdp.moneyrun.game.Game.getGameFromTaskSnapshot;

public class GameLobbyActivity extends AppCompatActivity {

    String gameId;
    private ArrayList<Player> playerList = new ArrayList<>();
    private LobbyPlayerListAdapter listAdapter;
    TextView playerMissingView;
    DatabaseProxy dbProxy;
    private Game game;
    private int playerInGame;
    private int missingPlayers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);
        addQuitLobbyButton();
        addAdapter();
        playerInGame = 1;
        gameId = (String) getIntent().getSerializableExtra("currentGameId");
        Task<DataSnapshot> task = getGameDataSnapshot(gameId);
        game = getGameFromTaskSnapshot(task);
        if(game != null) {
            missingPlayers = game.getMaxPlayerCount() - 1;
        }
        playerMissingView = findViewById(R.id.lobby_players_missing_TextView);
        String default_player_missing = getString(R.string.lobby_player_missing, missingPlayers);
        playerMissingView.setText(default_player_missing);

    }


    public void addQuitLobbyButton(){
        findViewById(R.id.leave_lobby_button).setOnClickListener(v ->  {
            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
            finish();
        });

    }
    public LobbyPlayerListAdapter getListAdapter(){
        return listAdapter;
    }


    private void addAdapter(){
        // The adapter lets us add item to a ListView easily.
        listAdapter = new LobbyPlayerListAdapter(this,playerList);
        ListView ldbView = (ListView) findViewById(R.id.lobby_player_list_view);
        ldbView.setAdapter(listAdapter);
    }

    /**
     *
     * @param playerList: players to be added to the leaderboard
     *  Adds players to leaderboard
     */
    public void addPlayerList(ArrayList<Player> playerList){
        if(playerList == null){
            throw new NullPointerException("Player list is null");
        }
        listAdapter.addAll(playerList);
    }

    /**
     *
     * @param player: player to be added to the leaderboard
     *  Adds player to leaderboard
     */
    public void addPlayer(Player player){
        // can't just add a player directly to an adapter, need to put it in a list
        if(player == null){
            throw new IllegalArgumentException("player is null");
        }
        ArrayList<Player> to_add = new ArrayList<>();
        to_add.add(player);
        addPlayerList(to_add);
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
    }



}