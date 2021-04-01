package sdp.moneyrun;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

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

    private Button joinGame;
    private Button newGame;
    private String[] result;
    private Player player;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    // Get player location
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setup database instance
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Get player location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
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
                    }
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
}