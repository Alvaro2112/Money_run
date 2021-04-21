package sdp.moneyrun.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.ui.game.GameLobbyActivity;
import sdp.moneyrun.game.GameRepresentation;
import sdp.moneyrun.map.LocationRepresentation;
import sdp.moneyrun.R;

public class JoinGameImplementation extends MenuImplementation{

    // Distance in meters
    private final static float MAX_DISTANCE_TO_JOIN_GAME = 500;

    private final boolean focusable;
    private final int layoutId;

    public JoinGameImplementation(Activity activity,
                                  DatabaseReference databaseReference,
                                  ActivityResultLauncher<String[]> requestPermissionsLauncher,
                                  FusedLocationProviderClient fusedLocationClient,
                                  boolean focusable,
                                  int layoutId){
        super(activity, databaseReference, requestPermissionsLauncher, fusedLocationClient);
        this.focusable = focusable;
        this.layoutId = layoutId;
    }

    /**
     * Event that occurs when the user wants to see the list of current games.
     *
     * @param view the current view
     */
    public void onClickShowJoinGamePopupWindow(View view) {


        // Show popup
        PopupWindow popupWindows = onButtonShowPopupWindowClick(view, focusable, layoutId);
        // Load game list
        onJoinGamePopupWindowLoadGameList(popupWindows.getContentView());
    }

    /**
     * Displays every current games.
     * @param popupView
     */
    private void onJoinGamePopupWindowLoadGameList(View popupView) {
        LinearLayout openGamesLayout = (LinearLayout) popupView.findViewById(R.id.openGamesLayout);

        List<GameRepresentation> gameRepresentations = new ArrayList<>();
        Task<DataSnapshot> taskDataSnapshot = getTaskGameRepresentations(gameRepresentations);
        taskDataSnapshot.addOnSuccessListener(dataSnapshot -> {
            TableLayout gameLayout = new TableLayout(activity);

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
    private Task<DataSnapshot> getTaskGameRepresentations(List<GameRepresentation> gameRepresentations) {

        return databaseReference
                .child(activity.getString(R.string.database_open_games))
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
                            if(gameRepresentation != null){
                                gameRepresentations.add(gameRepresentation);
                            }
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
        Boolean isVisible = dataSnapshot.child(activity.getString(R.string.database_open_games_is_visible)).getValue(Boolean.class);
        String gameId = dataSnapshot.child(activity.getString(R.string.database_open_games_game_id)).getValue(String.class);
        String name = dataSnapshot.child(activity.getString(R.string.database_open_games_name)).getValue(String.class);
        Integer playerCountInteger = dataSnapshot.child(activity.getString(R.string.database_open_games_player_count)).getValue(Integer.class);
        int playerCount = 0;
        if (playerCountInteger != null) {
            playerCount = playerCountInteger;
        }
        Integer maxPlayerCountInteger = dataSnapshot.child(activity.getString(R.string.database_open_games_max_player_count)).getValue(Integer.class);
        int maxPlayerCount = 0;
        if (maxPlayerCountInteger != null) {
            maxPlayerCount = maxPlayerCountInteger;
        }
        LocationRepresentation startLocation = dataSnapshot.child(activity.getString(R.string.database_open_games_start_location)).getValue(LocationRepresentation.class);

        if(isVisible == null || !isVisible ||gameId == null || name == null || startLocation == null){
            return null;
        }

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
        TableRow gameRow = new TableRow(activity);
        TableLayout.LayoutParams gameParams = new TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        Button button = new Button(activity);
        createJoinButton(button, buttonId, gameRepresentation);

        gameRow.addView(button);

        // create game name display
        createGameNameInfoDisplay(gameRepresentation, gameRow);

        // create player count display
        createPlayerCountNameInfoDisplay(gameRepresentation, gameRow);

        gameLayout.addView(gameRow, gameParams);
    }

    @SuppressLint("MissingPermission")
    private void createJoinButton(Button button, int buttonId, GameRepresentation gameRepresentation){
        // create join button
        button.setId(buttonId);
        button.setText(activity.getString(R.string.join_game_message));
        button.setOnClickListener(v -> joinLobbyFromJoinButton(gameRepresentation));

        // Modify button if the game is full
        if (gameRepresentation.getPlayerCount() >= gameRepresentation.getMaxPlayerCount()) {
            button.setEnabled(false);
            button.setText(activity.getString(R.string.join_game_full_message));
        }

        // Modify button if game is too far
        // Grant permissions if necessary
        requestLocationPermissions(requestPermissionsLauncher);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, location -> {
                    // Got last known location. In some rare situations this can be null
                    // In this case, the game cannot be instanciated
                    if (location == null) {
                        Log.e("location", "Error getting location");
                    }
                    LocationRepresentation locationRep = new LocationRepresentation(location.getLatitude(), location.getLongitude());

                    double distance = gameRepresentation.getStartLocation().distanceTo(locationRep);

                    if (distance > MAX_DISTANCE_TO_JOIN_GAME) {
                        button.setEnabled(false);
                        button.setText(activity.getString(R.string.join_game_too_far_message));
                    }
                });
    }

    private void createGameNameInfoDisplay(GameRepresentation gameRepresentation, TableRow gameRow){
        TextView nameView = new TextView(activity);
        String nameText = String.format((activity.getResources().getString(R.string.game_name_display)), gameRepresentation.getName());
        nameView.setText(nameText);
        nameView.setPadding(0, 0, 40, 0);
        gameRow.addView(nameView);
    }

    private void createPlayerCountNameInfoDisplay(GameRepresentation gameRepresentation, TableRow gameRow){
        TextView playerNumberView = new TextView(activity);
        String playerNumberText = String.format((activity.getResources().getString(R.string.game_player_number_display)),
                gameRepresentation.getPlayerCount(),
                gameRepresentation.getMaxPlayerCount());
        playerNumberView.setText(playerNumberText);
        gameRow.addView(playerNumberView);
    }

    private void joinLobbyFromJoinButton(GameRepresentation gameRepresentation){
        Intent lobbyIntent = new Intent(activity.getApplicationContext(), GameLobbyActivity.class);
        // Pass the game id to the lobby activity
        lobbyIntent.putExtra("currentGameId", gameRepresentation.getGameId());

        activity.startActivity(lobbyIntent);
    }


}
