package sdp.moneyrun;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.menu.MenuImplementation;
import sdp.moneyrun.menu.NewGameImplementation;


public class MenuActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {});


    // Distance in meters
    private final float MAX_DISTANCE_TO_JOIN_GAME = 500;

    private Button profileButton;
    private Button leaderboardButton;

    private String[] result;
    private Player player;
    private RiddlesDatabase db;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    // Get player location
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // setup database instance
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Get player location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            db = RiddlesDatabase.createInstance(getApplicationContext());
        } catch (RuntimeException e) {
            db = RiddlesDatabase.getInstance();
        }

        // Every buttons, elements on the activity
        profileButton = findViewById(R.id.go_to_profile_button);
        leaderboardButton = findViewById(R.id.menu_leaderboardButton);


        Button joinGame = findViewById(R.id.join_game);
        joinGame.setOnClickListener(v -> onClickShowJoinGamePopupWindow(v, true, R.layout.join_game_popup));

        Button newGame = findViewById(R.id.new_game);
        newGame.setOnClickListener(v -> NewGameImplementation.onClickShowNewGamePopupWindow(v, this, databaseReference, requestPermissionsLauncher, fusedLocationClient));

        addAskQuestionButtonFunctionality();
        addLogOutButtonFunctionality();

        profileButton.setOnClickListener(this::onButtonSwitchToUserProfileActivity);

        leaderboardButton.setOnClickListener(v -> {
            Intent leaderboardIntent = new Intent(MenuActivity.this, LeaderboardActivity.class);
            startActivity(leaderboardIntent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RiddlesDatabase.reset();
    }

    public void addLogOutButtonFunctionality() {

        Button logOut = findViewById(R.id.log_out_button);

        /**
         * Checks for clicks on the join game button and creates a popup of available games if clicked
         */
        logOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }

    public void addAskQuestionButtonFunctionality() {

        Button askQuestion = findViewById(R.id.ask_question);
        askQuestion.setOnClickListener(v -> onButtonShowQuestionPopupWindowClick(v, true, R.layout.question_popup, db.getRandomRiddle()));
    }

    public void onButtonSwitchToUserProfileActivity(View view) {

        Intent playerProfileIntent = new Intent(MenuActivity.this, PlayerProfileActivity.class);
        int playerId = getIntent().getIntExtra("playerId", 0);
        String[] playerInfo = getIntent().getStringArrayExtra("playerId" + playerId);
        playerProfileIntent.putExtra("playerId", playerId);
        playerProfileIntent.putExtra("playerId" + playerId, playerInfo);
        startActivity(playerProfileIntent);
    }

    public void onButtonShowQuestionPopupWindowClick(View view, Boolean focusable, int layoutId, Riddle riddle) {

        PopupWindow popupWindow = onButtonShowPopupWindowClick(view, focusable, layoutId);
        TextView tv = popupWindow.getContentView().findViewById(R.id.question);
        int correctId = 0;

        //changes the text to the current question
        tv.setText(riddle.getQuestion());

        int[] buttonIds = {R.id.question_choice_1, R.id.question_choice_2, R.id.question_choice_3, R.id.question_choice_4};
        TextView buttonView = tv;

        //Loops to find the ID of the button solution and assigns the text to each button
        for (int i = 0; i < 4; i++) {

            buttonView = popupWindow.getContentView().findViewById(buttonIds[i]);
            buttonView.setText(riddle.getPossibleAnswers()[i]);

            if (riddle.getPossibleAnswers()[i].equals(riddle.getAnswer()))
                correctId = buttonIds[i];
        }

        popupWindow.getContentView().findViewById(correctId).setOnClickListener(v -> popupWindow.dismiss());
    }

    /**
     *
     * @param view Current view before click
     * @param focusable Whether it can be dismissed by clicking outside the popup window
     * @param layoutId Id of the popup layout that will be used
     */
    public PopupWindow onButtonShowPopupWindowClick(View view, Boolean focusable, int layoutId) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layoutId, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window at wanted location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupWindow;
    }

    /**
     * Event that occurs when the user wants to see the list of current games.
     *
     * @param view the current view
     */
    public void onClickShowJoinGamePopupWindow(View view, boolean focusable, int layoutId) {
        // Show popup
        PopupWindow popupWindows = onButtonShowPopupWindowClick(view, focusable, layoutId);
        // Load game list
        onJoinGamePopupWindowLoadGameList(popupWindows.getContentView());
    }

    /**
     * Displays every current games.
     * @param popupView
     */
    public void onJoinGamePopupWindowLoadGameList(View popupView) {
        LinearLayout openGamesLayout = (LinearLayout) popupView.findViewById(R.id.openGamesLayout);

        List<GameRepresentation> gameRepresentations = new ArrayList<>();
        Task<DataSnapshot> taskDataSnapshot = getTaskGameRepresentations(gameRepresentations);
        taskDataSnapshot.addOnSuccessListener(dataSnapshot -> {
            TableLayout gameLayout = new TableLayout(this);

            int buttonId = 0;
            for (GameRepresentation gameRepresentation : gameRepresentations) {
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
    public Task<DataSnapshot> getTaskGameRepresentations(List<GameRepresentation> gameRepresentations) {

        return databaseReference
                .child(getString(R.string.database_open_games))
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        gameRepresentations.clear();

                        DataSnapshot result = task.getResult();
                        if (result == null) {
                            return;
                        }

                        for (DataSnapshot dataSnapshot : result.getChildren()) {
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
    private GameRepresentation defineGameFromDatabase(DataSnapshot dataSnapshot) {
        String gameId = dataSnapshot.child(getString(R.string.database_open_games_game_id)).getValue(String.class);
        String name = dataSnapshot.child(getString(R.string.database_open_games_name)).getValue(String.class);
        Integer playerCountInteger = dataSnapshot.child(getString(R.string.database_open_games_player_count)).getValue(Integer.class);
        int playerCount = 0;
        if (playerCountInteger != null) {
            playerCount = playerCountInteger;
        }
        Integer maxPlayerCountInteger = dataSnapshot.child(getString(R.string.database_open_games_max_player_count)).getValue(Integer.class);
        int maxPlayerCount = 0;
        if (maxPlayerCountInteger != null) {
            maxPlayerCount = maxPlayerCountInteger;
        }
        LocationRepresentation startLocation = dataSnapshot.child(getString(R.string.database_open_games_start_location)).getValue(LocationRepresentation.class);

        return new GameRepresentation(gameId, name, playerCount, maxPlayerCount, startLocation);
    }

    /**
     * Displays a game.
     *
     * @param gameLayout            the game layout
     * @param buttonId              the id of the join button
     * @param gameRepresentation    the representation of the game to display
     */
    private void displayGameInterface(TableLayout gameLayout, int buttonId, GameRepresentation gameRepresentation) {
        // create game layout
        TableRow gameRow = new TableRow(this);
        TableLayout.LayoutParams gameParams = new TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        Button button = new Button(this);
        createJoinButton(button, buttonId, gameRepresentation);

        gameRow.addView(button);

        // create game name display
        createGameNameInfoDisplay(gameRepresentation, gameRow);

        // create player count display
        createPlayerCountNameInfoDisplay(gameRepresentation, gameRow);

        gameLayout.addView(gameRow, gameParams);
    }

    @SuppressLint("MissingPermission")
    public void createJoinButton(Button button, int buttonId, GameRepresentation gameRepresentation){
        // create join button
        button.setId(buttonId);
        button.setText(getString(R.string.join_game_message));
        button.setOnClickListener(v -> joinLobbyFromJoinButton(v, gameRepresentation));

        // Modify button if the game is full
        if (gameRepresentation.getPlayerCount() >= gameRepresentation.getMaxPlayerCount()) {
            button.setEnabled(false);
            button.setText(getString(R.string.join_game_full_message));
        }

        // Modify button if game is too far
        // Grant permissions if necessary
        MenuImplementation.requestLocationPermissions(this, requestPermissionsLauncher);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null
                    // In this case, the game cannot be instanciated
                    if (location == null) {
                        Log.e("location", "Error getting location");
                    }
                    LocationRepresentation locationRep = new LocationRepresentation(location.getLatitude(), location.getLongitude());

                    double distance = gameRepresentation.getStartLocation().distanceTo(locationRep);

                    if (distance > MAX_DISTANCE_TO_JOIN_GAME) {
                        button.setEnabled(false);
                        button.setText(getString(R.string.join_game_too_far_message));
                    }
                });
    }

    public void createGameNameInfoDisplay(GameRepresentation gameRepresentation, TableRow gameRow){
        TextView nameView = new TextView(this);
        String nameText = String.format((getResources().getString(R.string.game_name_display)), gameRepresentation.getName());
        nameView.setText(nameText);
        nameView.setPadding(0, 0, 40, 0);
        gameRow.addView(nameView);
    }

    public void createPlayerCountNameInfoDisplay(GameRepresentation gameRepresentation, TableRow gameRow){
        TextView playerNumberView = new TextView(this);
        String playerNumberText = String.format((getResources().getString(R.string.game_player_number_display)),
                gameRepresentation.getPlayerCount(),
                gameRepresentation.getMaxPlayerCount());
        playerNumberView.setText(playerNumberText);
        gameRow.addView(playerNumberView);
    }

    public void joinLobbyFromJoinButton(View v, GameRepresentation gameRepresentation){
        Intent lobbyIntent = new Intent(getApplicationContext(), GameLobbyActivity.class);
        // Pass the game id to the lobby activity
        lobbyIntent.putExtra("currentGameId", gameRepresentation.getGameId());

        startActivity(lobbyIntent);
    }
}