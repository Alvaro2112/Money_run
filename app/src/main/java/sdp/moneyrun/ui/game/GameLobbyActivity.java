package sdp.moneyrun.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import sdp.moneyrun.R;
import sdp.moneyrun.database.GameDatabaseProxy;
import sdp.moneyrun.database.PlayerDatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.menu.MenuActivity;

public class GameLobbyActivity extends AppCompatActivity {
    private Game game;
    private String gameId;
    private Player user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);
        gameId = (String) getIntent().getStringExtra (getResources().getString(R.string.join_game_lobby_intent_extra_id));
        user = (Player) getIntent().getSerializableExtra(getResources().getString(R.string.join_game_lobby_intent_extra_user));
        runFunctionalities();
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
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    finish();
                });

                findViewById(R.id.launch_game_button).setOnClickListener(v -> {
                    //TODO launch game
                });


                TextView name = (TextView) findViewById(R.id.lobby_title);
                name.setText(game.getName());

                //Player List is dynamic with DB
                TextView playerList = (TextView) findViewById(R.id.player_list_textView);
                TextView playersMissing = (TextView) findViewById(R.id.players_missing_TextView);
                proxyG.addGameListener(game, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<List<Player>> t = new GenericTypeIndicator<List<Player>>(){};
                        List<Player> newPlayers = snapshot.child(getResources().getString(R.string.database_game_players))
                                .getValue(t);

                        StringBuilder str = new StringBuilder();
                        String prefix = "";
                        for(Player p: newPlayers){
                            str.append(prefix);
                            prefix = "\n";
                            str.append(p.getName());
                        }
                        playerList.setText(str.toString());
                        String newPlayersMissing = "Players missing: " + Integer.toString(game.getMaxPlayerCount() - newPlayers.size());
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