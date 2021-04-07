package sdp.moneyrun.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.GameLobbyActivity;
import sdp.moneyrun.GameRepresentation;
import sdp.moneyrun.LocationRepresentation;
import sdp.moneyrun.R;

public class JoinGameImplementation {

    // Distance in meters
    private final static float MAX_DISTANCE_TO_JOIN_GAME = 500;

    /**
     * Event that occurs when the user wants to see the list of current games.
     *
     * @param view the current view
     */
    public static void onClickShowJoinGamePopupWindow(View view,
                                                      Activity activity,
                                                      DatabaseReference databaseReference,
                                                      boolean focusable,
                                                      int layoutId,
                                                      ActivityResultLauncher<String[]> requestPermissionsLauncher,
                                                      FusedLocationProviderClient fusedLocationClient) {
        // Show popup
        PopupWindow popupWindows = MenuImplementation.onButtonShowPopupWindowClick(view, activity, focusable, layoutId);
        // Load game list
        onJoinGamePopupWindowLoadGameList(popupWindows.getContentView(), activity, databaseReference, requestPermissionsLauncher, fusedLocationClient);
    }

    /**
     * Displays every current games.
     * @param popupView
     */
    private static void onJoinGamePopupWindowLoadGameList(View popupView,
                                                         Activity activity,
                                                         DatabaseReference databaseReference,
                                                          ActivityResultLauncher<String[]> requestPermissionsLauncher,
                                                          FusedLocationProviderClient fusedLocationClient) {
        LinearLayout openGamesLayout = (LinearLayout) popupView.findViewById(R.id.openGamesLayout);

        List<GameRepresentation> gameRepresentations = new ArrayList<>();
        Task<DataSnapshot> taskDataSnapshot = getTaskGameRepresentations(gameRepresentations, activity, databaseReference);
        taskDataSnapshot.addOnSuccessListener(dataSnapshot -> {
            TableLayout gameLayout = new TableLayout(activity);

            int buttonId = 0;
            for (GameRepresentation gameRepresentation : gameRepresentations) {
                displayGameInterface(activity, gameLayout, buttonId, gameRepresentation, requestPermissionsLauncher, fusedLocationClient);
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
    private static Task<DataSnapshot> getTaskGameRepresentations(List<GameRepresentation> gameRepresentations,
                                                                Activity activity,
                                                                DatabaseReference databaseReference) {

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
                            GameRepresentation gameRepresentation = defineGameFromDatabase(dataSnapshot, activity);
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
    private static GameRepresentation defineGameFromDatabase(DataSnapshot dataSnapshot, Activity activity) {
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

        return new GameRepresentation(gameId, name, playerCount, maxPlayerCount, startLocation);
    }

    /**
     * Displays a game.
     *
     * @param gameLayout            the game layout
     * @param buttonId              the id of the join button
     * @param gameRepresentation    the representation of the game to display
     */
    private static void displayGameInterface(Activity activity,
                                             TableLayout gameLayout,
                                             int buttonId,
                                             GameRepresentation gameRepresentation,
                                             ActivityResultLauncher<String[]> requestPermissionsLauncher,
                                             FusedLocationProviderClient fusedLocationClient) {
        // create game layout
        TableRow gameRow = new TableRow(activity);
        TableLayout.LayoutParams gameParams = new TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        Button button = new Button(activity);
        createJoinButton(activity, button, buttonId, gameRepresentation, requestPermissionsLauncher, fusedLocationClient);

        gameRow.addView(button);

        // create game name display
        createGameNameInfoDisplay(activity, gameRepresentation, gameRow);

        // create player count display
        createPlayerCountNameInfoDisplay(activity, gameRepresentation, gameRow);

        gameLayout.addView(gameRow, gameParams);
    }

    @SuppressLint("MissingPermission")
    private static void createJoinButton(Activity activity,
                                         Button button,
                                         int buttonId,
                                         GameRepresentation gameRepresentation,
                                         ActivityResultLauncher<String[]> requestPermissionsLauncher,
                                         FusedLocationProviderClient fusedLocationClient){
        // create join button
        button.setId(buttonId);
        button.setText(activity.getString(R.string.join_game_message));
        button.setOnClickListener(v -> joinLobbyFromJoinButton(activity, gameRepresentation));

        // Modify button if the game is full
        if (gameRepresentation.getPlayerCount() >= gameRepresentation.getMaxPlayerCount()) {
            button.setEnabled(false);
            button.setText(activity.getString(R.string.join_game_full_message));
        }

        // Modify button if game is too far
        // Grant permissions if necessary
        MenuImplementation.requestLocationPermissions((AppCompatActivity) activity, requestPermissionsLauncher);

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

    private static void createGameNameInfoDisplay(Activity activity,
                                                  GameRepresentation gameRepresentation,
                                                  TableRow gameRow){
        TextView nameView = new TextView(activity);
        String nameText = String.format((activity.getResources().getString(R.string.game_name_display)), gameRepresentation.getName());
        nameView.setText(nameText);
        nameView.setPadding(0, 0, 40, 0);
        gameRow.addView(nameView);
    }

    private static void createPlayerCountNameInfoDisplay(Activity activity,
                                                         GameRepresentation gameRepresentation,
                                                         TableRow gameRow){
        TextView playerNumberView = new TextView(activity);
        String playerNumberText = String.format((activity.getResources().getString(R.string.game_player_number_display)),
                gameRepresentation.getPlayerCount(),
                gameRepresentation.getMaxPlayerCount());
        playerNumberView.setText(playerNumberText);
        gameRow.addView(playerNumberView);
    }

    private static void joinLobbyFromJoinButton(Activity activity,
                                                GameRepresentation gameRepresentation){
        Intent lobbyIntent = new Intent(activity.getApplicationContext(), GameLobbyActivity.class);
        // Pass the game id to the lobby activity
        lobbyIntent.putExtra("currentGameId", gameRepresentation.getGameId());

        activity.startActivity(lobbyIntent);
    }
}
