package sdp.moneyrun;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.permissions.PermissionsRequester;

public class MenuActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {
    private final String OPEN_GAMES = "open_games";
    private final String OPEN_GAMES_GAME_ID = "gameId";
    private final String OPEN_GAMES_NAME = "name";
    private final String OPEN_GAMES_PLAYER_COUNT = "playerCount";
    private final String OPEN_GAMES_MAX_PLAYER_COUNT = "maxPlayerCount";
    private final String GAME_START_LOCATION = "startLocation";

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

        try{
            db = RiddlesDatabase.createInstance(getApplicationContext());
        }
        catch(RuntimeException e){
            db = RiddlesDatabase.getInstance();
        }

        // Every buttons, elements on the activity
        profileButton = findViewById(R.id.go_to_profile_button);
        leaderboardButton = findViewById(R.id.menu_leaderboardButton);

        addJoinGameButtonFunctionality();
        addNewGameButtonFunctionality();
        addAskQuestionButtonFunctionality();
        addLogOutButtonFunctionality();
        linkProfileButton(profileButton);
        linkLeaderboardButton(leaderboardButton);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RiddlesDatabase.reset();
    }

    public void addJoinGameButtonFunctionality(){

        Button joinGame = findViewById(R.id.join_game);
        joinGame.setOnClickListener(v -> onClickShowJoinGamePopupWindow(v, true, R.layout.join_game_popup));
    }

    public void addNewGameButtonFunctionality(){

        Button newGame = findViewById(R.id.new_game);
        newGame.setOnClickListener(this::onClickShowNewGamePopupWindow);
    }

    public void addLogOutButtonFunctionality(){

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

    public void addAskQuestionButtonFunctionality(){

        Button askQuestion = findViewById(R.id.ask_question);
        askQuestion.setOnClickListener(v -> onButtonShowQuestionPopupWindowClick(v, true, R.layout.question_popup, db.getRandomRiddle()));
    }

    private void linkProfileButton(Button button){
        button.setOnClickListener(v -> onButtonSwitchToUserProfileActivity(v));
    }

    
    private void linkLeaderboardButton(Button button){
        button.setOnClickListener(v -> {
            Intent leaderboardIntent = new Intent(MenuActivity.this, LeaderboardActivity.class);
            startActivity(leaderboardIntent);
        });
    }

    public void onButtonSwitchToUserProfileActivity(View view) {

        Intent playerProfileIntent = new Intent(MenuActivity.this, PlayerProfileActivity.class);
        int playerId = getIntent().getIntExtra("playerId",0);
        String[] playerInfo = getIntent().getStringArrayExtra("playerId"+playerId);
        playerProfileIntent.putExtra("playerId",playerId);
        playerProfileIntent.putExtra("playerId"+playerId,playerInfo);
        startActivity(playerProfileIntent);

    }

    public void onButtonShowJoinGamePopupWindowClick(View view, Boolean focusable, int layoutId) {

        onButtonShowPopupWindowClick(view, focusable, layoutId);

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
        for (int i = 0; i < 4; i++){

            buttonView = popupWindow.getContentView().findViewById(buttonIds[i]);
            buttonView.setText(riddle.getPossibleAnswers()[i]);

            if(riddle.getPossibleAnswers()[i].equals(riddle.getAnswer()))
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
                .child(OPEN_GAMES)
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
        String gameId = dataSnapshot.child(OPEN_GAMES_GAME_ID).getValue(String.class);
        String name = dataSnapshot.child(OPEN_GAMES_NAME).getValue(String.class);
        Integer playerCountInteger = dataSnapshot.child(OPEN_GAMES_PLAYER_COUNT).getValue(Integer.class);
        int playerCount = 0;
        if (playerCountInteger != null) {
            playerCount = playerCountInteger;
        }
        Integer maxPlayerCountInteger = dataSnapshot.child(OPEN_GAMES_MAX_PLAYER_COUNT).getValue(Integer.class);
        int maxPlayerCount = 0;
        if (maxPlayerCountInteger != null) {
            maxPlayerCount = maxPlayerCountInteger;
        }
        LocationRepresentation startLocation = dataSnapshot.child(GAME_START_LOCATION).getValue(LocationRepresentation.class);

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

        // create join button
        Button button = new Button(this);
        button.setId(buttonId);
        button.setText(getString(R.string.join_game_message));
        button.setOnClickListener(v -> joinLobbyFromJoinButton(v, gameRepresentation));

        // Modify button if the game is full
        if(gameRepresentation.getPlayerCount() >= gameRepresentation.getMaxPlayerCount()){
            button.setEnabled(false);
            button.setText(getString(R.string.join_game_full_message));
        }

        // Modify button if game is too far
        // Grant permissions if necessary
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissions();
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null
                        // In this case, the game cannot be instanciated
                        if (location == null) {
                            Log.e("location", "Error getting location");
                        }
                        LocationRepresentation locationRep = new LocationRepresentation(location.getLatitude(), location.getLongitude());

                        double distance = gameRepresentation.getStartLocation().distanceTo(locationRep);

                        if(distance > MAX_DISTANCE_TO_JOIN_GAME){
                            button.setEnabled(false);
                            button.setText(getString(R.string.join_game_too_far_message));
                        }
                    }
                });

        gameRow.addView(button);

        // create game name display
        TextView nameView = new TextView(this);
        String nameText = String.format((getResources().getString(R.string.game_name_display)), gameRepresentation.getName());
        nameView.setText(nameText);
        nameView.setPadding(0, 0, 40, 0);
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
    public void onSubmitPostNewGame(LinearLayout newGameLayout) {
        TextView nameGameView = newGameLayout.findViewById(R.id.nameGameText);
        TextView maxPlayerNumberView = newGameLayout.findViewById(R.id.maxPlayerNumber);
        String gameName = nameGameView.getText().toString().trim();
        String maxPlayerNumberStr = maxPlayerNumberView.getText().toString().trim();
        if (gameName.isEmpty()) {
            nameGameView.setError("This field is required");
            return;
        }
        if (maxPlayerNumberStr.isEmpty()) {
            maxPlayerNumberView.setError("This field is required");
            return;
        }

        int maxPlayerNumber = Integer.parseInt(maxPlayerNumberStr);

        if (maxPlayerNumber < 1) {
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
    public void postNewGame(String name, int maxPlayerCount) {
        DatabaseReference gameReference = databaseReference.child(OPEN_GAMES).push();
        DatabaseReference startLocationReference = databaseReference.child(OPEN_GAMES).child(gameReference.getKey()).child(GAME_START_LOCATION);

        // Grant permissions if necessary
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissions();
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null
                    // In this case, the game cannot be instanciated
                    if (location == null) {
                        Log.e("location", "Error getting location");
                    }

                    // Build new game given fields filled by user
                    String gameId = gameReference.getKey();
                    List<Player> players = new ArrayList<>();
                    List<Riddle> riddles = new ArrayList<>();

                    Game game = new Game(gameId, name, players, maxPlayerCount, riddles, location);

                    // post game to database
                    gameReference.setValue(game);

                    // Post location to database
                    LocationRepresentation locationRep = new LocationRepresentation(location.getLatitude(), location.getLongitude());
                    startLocationReference.setValue(locationRep);
                });
    }

    public void requestLocationPermissions(){
        ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
            for (String permission : map.keySet()) {

                Boolean isGranted = map.get(permission);
                isGranted = isGranted != null ? isGranted : false;

                if (isGranted) {
                    System.out.println("Permission" + permission + " granted.");
                } else {
                    System.out.println("Permission" + permission + " denied.");
                }
            }
        });

        String coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
        String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;

        PermissionsRequester locationPermissionsRequester = new PermissionsRequester(
                this,
                requestPermissionsLauncher,
                getString(R.string.user_location_permission_explanation),
                false,
                coarseLocation,
                fineLocation);
        locationPermissionsRequester.requestPermission();
    }

    public void joinLobbyFromJoinButton(View v, GameRepresentation gameRepresentation){
        Intent lobbyIntent = new Intent(getApplicationContext(), GameLobbyActivity.class);
        // Pass the game id to the lobby activity
        lobbyIntent.putExtra("currentGameId", gameRepresentation.getGameId());

        startActivity(lobbyIntent);
    }
}