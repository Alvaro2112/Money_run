package sdp.moneyrun.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import sdp.moneyrun.R;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.menu.MenuActivity;

public class GameLobbyActivity extends AppCompatActivity {

    String gameId;
    private ArrayList<Player> playerList = new ArrayList<>();
    private LobbyPlayerListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);

        findViewById(R.id.leave_lobby_button).setOnClickListener(v ->  {
            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
            finish();
        });

        gameId = (String) getIntent().getSerializableExtra("currentGameId");
        addAdapter();
        Player player = new Player(2,"gauthier","ok",2,2,2);
        addPlayer(player);
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