package sdp.moneyrun.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.database.GameDatabaseProxy;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;


public class GameLobbyActivity extends AppCompatActivity {
    private Game game;
    private String gameId;
    private Player user;
    private ArrayList<Player> playerList = new ArrayList<>();
    private LobbyPlayerListAdapter listAdapter;
    TextView playerMissingView;
    DatabaseProxy dbProxy;
    private int missingPlayers;
    private ListView playerListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);
        addAdapter();
        playerMissingView = findViewById(R.id.lobby_players_missing_TextView);
        String default_player_missing = getString(R.string.lobby_player_missing, missingPlayers);
        playerMissingView.setText(default_player_missing);
        gameId = (String) getIntent().getStringExtra (getResources().getString(R.string.join_game_lobby_intent_extra_id));
        user = (Player) getIntent().getSerializableExtra(getResources().getString(R.string.join_game_lobby_intent_extra_user));
        runFunctionalities();

    }


    public LobbyPlayerListAdapter getListAdapter(){
        return listAdapter;
    }


    private void addAdapter(){
        // The adapter lets us add item to a ListView easily.
        listAdapter = new LobbyPlayerListAdapter(this,playerList);
        playerListView = (ListView) findViewById(R.id.lobby_player_list_view);
        playerListView.setAdapter(listAdapter);
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


    private void runFunctionalities(){
        setAllFieldsAccordingToGame();

    }

    private void setAllFieldsAccordingToGame(){
        GameDatabaseProxy proxyG = new GameDatabaseProxy();

        proxyG.getGameDataSnapshot(gameId).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                game = proxyG.getGameFromTaskSnapshot(task);

                //Find all the views and assign them values
                findViewById(R.id.leave_lobby_button).setOnClickListener(v -> {
                    game.removePlayer(user,false);
                    UserDatabaseProxy pdp = new UserDatabaseProxy();
                    pdp.getUserTask(user.getPlayerId()).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                User user = pdp.getUserFromTask(task);
                                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                });

                findViewById(R.id.launch_game_button).setOnClickListener(v -> {

                    if(game.getHost().equals(user)){
                        game.setStarted(true, false);
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        intent.putExtra("player", user);
                        intent.putExtra("gameId", gameId);
                        intent.putExtra("host", true);
                        startActivity(intent);
                        finish();
                    }
                });

                TextView name = (TextView) findViewById(R.id.lobby_title);
                name.setText(game.getName());

                //Player List is dynamic with DB
                // TextView playerList = (TextView) findViewById(R.id.player_list_textView);
                TextView playersMissing = (TextView) findViewById(R.id.players_missing_TextView);
                proxyG.addGameListener(game, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<List<Player>> t = new GenericTypeIndicator<List<Player>>(){};
                        List<Player> newPlayers = snapshot.child(getResources().getString(R.string.database_game_players))
                                .getValue(t);
                        listAdapter.clear();
                        addPlayerList(new ArrayList<Player>(newPlayers));
                        String newPlayersMissing = getString(R.string.lobby_player_missing,game.getMaxPlayerCount() - newPlayers.size());

                        playersMissing.setText(newPlayersMissing);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(Game.class.getSimpleName(), error.getMessage());
                    }
                });
            }else{
                Log.e(Game.class.getSimpleName(),task.getException().getMessage());
            }
        });
    }

}