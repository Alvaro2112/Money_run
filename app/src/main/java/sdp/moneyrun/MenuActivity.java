package sdp.moneyrun;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {
    private Button profileButton;

    private Button joinGame;
    private Button newGame;
    private String[] result;
    private Player player;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        NavigationView navigationView = findViewById(R.id.nav_view);
        profileButton = findViewById(R.id.go_to_profile_button);
        joinGame = findViewById(R.id.join_game);
        newGame = findViewById(R.id.new_game);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerProfileIntent = new Intent(MenuActivity.this, PlayerProfileActivity.class);
                playerProfileIntent.putExtra("profile", result);
                startActivity(playerProfileIntent);
            }
        });

        joinGame.setOnClickListener(v -> onClickShowJoinGamePopupWindow(v));
        newGame.setOnClickListener(v -> onNewGamePopupWindowLoadGameList(v));

        // To be able to retrieve list of games from database.
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void onClickShowJoinGamePopupWindow(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.join_game_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // show the popup window at wanted location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        onJoinGamePopupWindowLoadGameList(popupView);
    }

    /**
     * Create buttons for every open games.
     * @param popupView
     */
    public void onJoinGamePopupWindowLoadGameList(View popupView){
        GameDataRequester gameDataRequester = new GameDataRequester(databaseReference);
        LinearLayout openGamesLayout = (LinearLayout) popupView.findViewById(R.id.openGamesLayout);
        final Context context = this;

        Task<DataSnapshot> taskDataSnapshot = gameDataRequester.getGameList();
        taskDataSnapshot.addOnSuccessListener(dataSnapshot -> {
            List<Game> gameNames = gameDataRequester.getCurrentGameList();
            TableLayout gameLayout = new TableLayout(context);

            int id = 0;
            for(Game game : gameNames){
                displayGameInterface(context, gameLayout, openGamesLayout, id, game);
            }
            openGamesLayout.addView(gameLayout);
        });
    }

    private void displayGameInterface(Context context, TableLayout gameLayout, LinearLayout openGamesLayout, int id, Game game){
        // create game layout
        TableRow gameRow = new TableRow(context);
        TableLayout.LayoutParams gameParams = new TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // create join button
        Button button = new Button(context);
        button.setId(id);
        String buttonText = String.format(getResources().getString(R.string.join_game_message));
        button.setText(buttonText);
        gameRow.addView(button);

        // create game name display
        TextView nameView = new TextView(context);
        String nameText = String.format((getResources().getString(R.string.game_name_display)), game.getName());
        nameView.setText(nameText);
        nameView.setPadding(0,0,40,0);
        gameRow.addView(nameView);

        // create player count display
        TextView playerNumberView = new TextView(context);
        String playerNumberText = String.format((getResources().getString(R.string.game_player_number_display)),
                game.getPlayerNumber(),
                game.getMaxPlayerNumber());
        playerNumberView.setText(playerNumberText);
        gameRow.addView(playerNumberView);

        gameLayout.addView(gameRow, gameParams);
    }

    public void onNewGamePopupWindowLoadGameList(View popupView){

    }
}