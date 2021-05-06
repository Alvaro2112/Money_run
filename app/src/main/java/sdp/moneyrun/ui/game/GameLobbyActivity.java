package sdp.moneyrun.ui.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.database.GameDatabaseProxy;
import sdp.moneyrun.database.PlayerDatabaseProxy;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;


public class GameLobbyActivity extends AppCompatActivity {
    private final String TAG = GameLobbyActivity.class.getSimpleName();
    private final String DB_HOST = "host";
    private final String DB_IS_DELETED = "isDeleted";
    private final String DB_PLAYERS = "players";

    private LobbyPlayerListAdapter listAdapter;
    private Game game;
    private String gameId;
    private Player user;
    private DatabaseReference thisGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);

        addAdapter();

        gameId = getIntent().getStringExtra(getResources().getString(R.string.join_game_lobby_intent_extra_id));
        user = (Player) getIntent().getSerializableExtra(getResources().getString(R.string.join_game_lobby_intent_extra_user));

        this.thisGame = FirebaseDatabase.getInstance().getReference()
                .child(this.getString(R.string.database_games)).child(gameId);
        getGameFromDb();
    }

    public LobbyPlayerListAdapter getListAdapter() {
        return listAdapter;
    }

    private void addAdapter() {
        // The adapter lets us add item to a ListView easily.
        ArrayList<Player> playerList = new ArrayList<>();
        listAdapter = new LobbyPlayerListAdapter(this, playerList);
        ListView playerListView = (ListView) findViewById(R.id.lobby_player_list_view);
        playerListView.setAdapter(listAdapter);
    }

    /**
     * @param playerList: players to be added to the leaderboard
     *                    Adds players to leaderboard
     */
    public void addPlayerList(ArrayList<Player> playerList) {
        if (playerList == null) {
            throw new NullPointerException("Player list is null");
        }
        listAdapter.addAll(playerList);
    }

    private void getGameFromDb() {
        GameDatabaseProxy proxyG = new GameDatabaseProxy();
        proxyG.getGameDataSnapshot(gameId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                this.game = proxyG.getGameFromTaskSnapshot(task);
                setAllFieldsAccordingToGame();
                listenToIsDeleted();
                createDeleteOrLeaveButton();
            } else {
                Log.e(TAG, task.getException().getMessage());
            }
        });
    }

    private void listenToIsDeleted() {
        if (!user.equals(game.getHost())) {
            thisGame.child(DB_IS_DELETED).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if ((boolean) snapshot.getValue()) {
                        game.removePlayer(user, false);
                        UserDatabaseProxy pdp = new UserDatabaseProxy();
                        pdp.getUserTask(user.getPlayerId()).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    User user = pdp.getUserFromTask(task);
                                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage());
                }
            });
        }
    }

    private void createDeleteOrLeaveButton() {
        if (user.equals(game.getHost())) {
            Button leaveButton = (Button) findViewById(R.id.leave_lobby_button);
            leaveButton.setText("Delete");
            leaveButton.setOnClickListener(getDeleteClickListener());
        } else {
            findViewById(R.id.leave_lobby_button).setOnClickListener(getLeaveClickListener());
        }
    }

    private void setAllFieldsAccordingToGame() {
        //Find all the views and assign them values
        TextView name = (TextView) findViewById(R.id.lobby_title);
        name.setText(game.getName());

        findViewById(R.id.launch_game_button).setOnClickListener(v -> {
            if (game.getHost().equals(user)) {
                game.setStarted(true, false);
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("player", user);
                intent.putExtra("gameId", gameId);
                intent.putExtra("host", true);
                startActivity(intent);
                finish();
            }
        });

        //Player List is dynamic with DB
        TextView playersMissing = (TextView) findViewById(R.id.players_missing_TextView);
        thisGame.child(DB_PLAYERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<Player>> t = new GenericTypeIndicator<List<Player>>() {
                };
                List<Player> newPlayers = snapshot.child(getResources().getString(R.string.database_game_players))
                        .getValue(t);
                listAdapter.clear();
                addPlayerList(new ArrayList<Player>(newPlayers));
                String newPlayersMissing = getString(R.string.lobby_player_missing, game.getMaxPlayerCount() - newPlayers.size());

                playersMissing.setText(newPlayersMissing);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private View.OnClickListener getDeleteClickListener() {
        return v -> {
            game.setIsDeleted(true, false);
            thisGame.child(DB_PLAYERS).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<List<Player>> t = new GenericTypeIndicator<List<Player>>() {};
                    List<Player> players = snapshot.getValue(t);
                    if (players.size() == 1) {
                        UserDatabaseProxy pdp = new UserDatabaseProxy();
                        pdp.getUserTask(user.getPlayerId()).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    User user = pdp.getUserFromTask(task);
                                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    finish();

                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage());
                }
            });
        };
    }

    private View.OnClickListener getLeaveClickListener() {
        return v -> {
            game.removePlayer(user, false);
            UserDatabaseProxy pdp = new UserDatabaseProxy();
            pdp.getUserTask(user.getPlayerId()).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = pdp.getUserFromTask(task);
                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        };
    }


}