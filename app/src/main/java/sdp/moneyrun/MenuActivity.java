package sdp.moneyrun;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
    private final String OPEN_GAMES = "open_games";
    private final String OPEN_GAMES_GAME_ID = "gameId";
    private final String OPEN_GAMES_NAME = "name";
    private final String OPEN_GAMES_PLAYER_COUNT = "playerCount";
    private final String OPEN_GAMES_MAX_PLAYER_COUNT = "maxPlayerCount";

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
        // setup database instance
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        setContentView(R.layout.activity_menu);

        // add event to Profile button
        profileButton = findViewById(R.id.go_to_profile_button);
        profileButton.setOnClickListener(v -> {
            Intent playerProfileIntent = new Intent(MenuActivity.this, PlayerProfileActivity.class);
            playerProfileIntent.putExtra("profile", result);
            startActivity(playerProfileIntent);
        });

        // add event to Join game button
        joinGame = findViewById(R.id.join_game);
        joinGame.setOnClickListener(this::onClickShowJoinGamePopupWindow);

        // add event to New game button
        newGame = findViewById(R.id.new_game);
        newGame.setOnClickListener(this::onClickShowNewGamePopupWindow);
    }

    /**
     * Event that occurs when the user wants to see the list of current games.
     *
     * @param view the current view
     */
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
     * Displays every current games.
     * @param popupView
     */
    public void onJoinGamePopupWindowLoadGameList(View popupView){
        LinearLayout openGamesLayout = (LinearLayout) popupView.findViewById(R.id.openGamesLayout);

        List<GameRepresentation> gameRepresentations = new ArrayList<>();
        Task<DataSnapshot> taskDataSnapshot = getTaskGameRepresentations(gameRepresentations);
        taskDataSnapshot.addOnSuccessListener(dataSnapshot -> {
            TableLayout gameLayout = new TableLayout(this);

            int buttonId = 0;
            for(GameRepresentation gameRepresentation : gameRepresentations){
                displayGameInterface(gameLayout, buttonId, gameRepresentation);
                buttonId++;
            }
            openGamesLayout.addView(gameLayout);
        });
    }

    /**
     * Read the current games from database.
     *
     * @param gameRepresentations a list of the game representations.
     * @return
     */
    public Task<DataSnapshot> getTaskGameRepresentations(List<GameRepresentation> gameRepresentations){

        return databaseReference
                .child(OPEN_GAMES)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }else{
                        gameRepresentations.clear();

                        DataSnapshot result = task.getResult();
                        if(result == null){
                            return;
                        }

                        for(DataSnapshot dataSnapshot : result.getChildren()){
                            GameRepresentation gameRepresentation = defineGameFromDatabase(dataSnapshot);
                            gameRepresentations.add(gameRepresentation);
                        }
                    }
                });
    }

    /**
     * Get a representation of a game from the database.
     *
     * @param dataSnapshot the game snapshot
     * @return
     */
    private GameRepresentation defineGameFromDatabase(DataSnapshot dataSnapshot){
        String gameId = dataSnapshot.child(OPEN_GAMES_GAME_ID).getValue(String.class);
        String name = dataSnapshot.child(OPEN_GAMES_NAME).getValue(String.class);
        Integer playerCountInteger = dataSnapshot.child(OPEN_GAMES_PLAYER_COUNT).getValue(Integer.class);
        int playerCount = 0;
        if(playerCountInteger != null){
            playerCount = playerCountInteger;
        }
        Integer maxPlayerCountInteger = dataSnapshot.child(OPEN_GAMES_MAX_PLAYER_COUNT).getValue(Integer.class);
        int maxPlayerCount = 0;
        if(maxPlayerCountInteger != null){
            maxPlayerCount = maxPlayerCountInteger;
        }

        return new GameRepresentation(gameId, name, playerCount, maxPlayerCount);
    }

    /**
     * Displays a game.
     *
     * @param gameLayout            the game layout
     * @param buttonId              the id of the join button
     * @param gameRepresentation    the representation of the game to display
     */
    private void displayGameInterface(TableLayout gameLayout, int buttonId, GameRepresentation gameRepresentation){
        // create game layout
        TableRow gameRow = new TableRow(this);
        TableLayout.LayoutParams gameParams = new TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // create join button
        Button button = new Button(this);
        button.setId(buttonId);
        String buttonText = String.format(getResources().getString(R.string.join_game_message));
        button.setText(buttonText);
        button.setEnabled(gameRepresentation.getPlayerCount() < gameRepresentation.getMaxPlayerCount());
        gameRow.addView(button);

        // create game name display
        TextView nameView = new TextView(this);
        String nameText = String.format((getResources().getString(R.string.game_name_display)), gameRepresentation.getName());
        nameView.setText(nameText);
        nameView.setPadding(0,0,40,0);
        gameRow.addView(nameView);

        // create player count display
        TextView playerNumberView = new TextView(this);
        String playerNumberText = String.format((getResources().getString(R.string.game_player_number_display)),
                gameRepresentation.getPlayerCount(),
                gameRepresentation.getMaxPlayerCount());
        playerNumberView.setText(playerNumberText);
        gameRow.addView(playerNumberView);

        gameLayout.addView(gameRow, gameParams);
    }

    /**
     * Event that occurs when the user wants to add a new game.
     *
     * @param view the current view
     */
    public void onClickShowNewGamePopupWindow(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.new_game_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // show the popup window at wanted location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        LinearLayout newGameLayout = (LinearLayout) popupView.findViewById(R.id.newGameLayout);
        Button newGameButton = newGameLayout.findViewById(R.id.newGameSubmit);

        newGameButton.setOnClickListener(v -> onSubmitPostNewGame(newGameLayout));
    }

    /**
     * Create a new game.
     *
     * @param newGameLayout the game layout
     */
    public void onSubmitPostNewGame(LinearLayout newGameLayout){
        TextView nameGameView = newGameLayout.findViewById(R.id.nameGameText);
        TextView maxPlayerNumberView = newGameLayout.findViewById(R.id.maxPlayerNumber);
        String gameName = nameGameView.getText().toString().trim();
        String maxPlayerNumberStr = maxPlayerNumberView.getText().toString().trim();
        if(gameName.isEmpty()){
            nameGameView.setError("This field is required");
            return;
        }
        if(maxPlayerNumberStr.isEmpty()) {
            maxPlayerNumberView.setError("This field is required");
            return;
        }

        int maxPlayerNumber = Integer.parseInt(maxPlayerNumberStr);

        if(maxPlayerNumber < 1){
            maxPlayerNumberView.setError("There should be at least one player in a game");
            return;
        }

        postNewGame(gameName, maxPlayerNumber);
    }

    /**
     * Post a new game.
     *
     * @param name              the game name
     * @param maxPlayerCount    the maximum number of players in the game
     * @return the game
     */
    public Game postNewGame(String name, int maxPlayerCount){
        DatabaseReference gameReference = databaseReference.child(OPEN_GAMES).push();

        String gameId = gameReference.getKey();
        List<Player> players = new ArrayList<>();
        List<Riddle> riddles = new ArrayList<>();
        Location startLocation = new Location("");
        startLocation.getLatitude();
        startLocation.getLongitude();

        Game game = new Game(gameId, name, players, maxPlayerCount, riddles, startLocation);

        gameReference.setValue(game);

        return game;
    }
}