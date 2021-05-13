package sdp.moneyrun.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sdp.moneyrun.game.Game;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.game.GameLobbyActivity;
import sdp.moneyrun.game.GameRepresentation;
import sdp.moneyrun.map.LocationRepresentation;
import sdp.moneyrun.R;
import sdp.moneyrun.user.User;

public class JoinGameImplementation extends MenuImplementation{

    // Distance in meters
    private final static float MAX_DISTANCE_TO_JOIN_GAME = 1000;
    private final boolean focusable;
    private final int layoutId;
    private final User currentUser;
    private static final String TAG = JoinGameImplementation.class.getSimpleName();

    private int buttonId;

    public JoinGameImplementation(Activity activity,
                                  DatabaseReference databaseReference,
                                  User user,
                                  ActivityResultLauncher<String[]> requestPermissionsLauncher,
                                  FusedLocationProviderClient fusedLocationClient,
                                  boolean focusable,
                                  int layoutId){
        super(activity, databaseReference, user, requestPermissionsLauncher, fusedLocationClient);
        if(user == null){
            throw new IllegalArgumentException("user is null");
        }
        this.focusable = focusable;
        this.layoutId = layoutId;
        this.currentUser = user;
    }

    /**
     * Event that occurs when the user wants to see the list of current games.
     *
     * @param view the current view
     */
    public void onClickShowJoinGamePopupWindow(View view) {
        activity.getIntent().putExtra("number_of_results", -10);
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
        LinearLayout openGamesLayout = popupView.findViewById(R.id.openGamesLayout);

        Button filterButton = popupView.findViewById(R.id.join_game_button_filter);

        filterButton.setOnClickListener(v -> {
            EditText filterEditText = popupView.findViewById(R.id.join_game_text_filter);
            openGamesLayout.removeAllViews();

            String filterText = filterEditText.getText().toString().trim().toLowerCase(Locale.getDefault());
            loadGameListGivenFilter(popupView, openGamesLayout, filterText);
        });

        // First load the game list without any filter.
        loadGameListGivenFilter(popupView, openGamesLayout, null);
    }

    @SuppressLint("MissingPermission")
    private void loadGameListGivenFilter(View popupView, LinearLayout openGamesLayout, String filterText){
        List<GameRepresentation> gameRepresentations = new ArrayList<>();
        Task<DataSnapshot> taskDataSnapshot = getTaskGameRepresentations(gameRepresentations);
        taskDataSnapshot.addOnSuccessListener(dataSnapshot -> {
            TableLayout gameLayout = new TableLayout(activity);

            // Grant permissions if necessary
            requestLocationPermissions(requestPermissionsLauncher);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        // Got last known location. In some rare situations this can be null
                        // In this case, define a default location (0, 0)
                        LocationRepresentation locationRep;

                        if (location == null) {
                            locationRep = new LocationRepresentation(0, 0);
                        }else{
                            locationRep = new LocationRepresentation(location.getLatitude(), location.getLongitude());
                        }

                        buttonId = 0;
                        for (GameRepresentation gameRepresentation : gameRepresentations) {
                            // Do not show games that do not match filter
                            String lowerName = gameRepresentation.getName().toLowerCase(Locale.getDefault());
                            // Do not show far away game
                            double distance = gameRepresentation.getStartLocation().distanceTo(locationRep);

                            if((filterText == null || lowerName.contains(filterText)) && distance <= MAX_DISTANCE_TO_JOIN_GAME){
                                displayGameInterface(gameLayout, buttonId, gameRepresentation, filterText);
                                buttonId++;
                            }
                        }
                        // Put extra to give the number of elements found
                        activity.getIntent().putExtra("number_of_results", buttonId);

                        openGamesLayout.addView(gameLayout);
                    });
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
                .child(activity.getString(R.string.database_game))
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
        String gameId = dataSnapshot.getKey();
        Boolean isVisible = dataSnapshot.child(activity.getString(R.string.database_game_is_visible)).getValue(Boolean.class);
        String name = dataSnapshot.child(activity.getString(R.string.database_game_name)).getValue(String.class);
        int playerCount = (int) dataSnapshot.child(activity.getString(R.string.database_game_players)).getChildrenCount();
        Integer maxPlayerCountInteger = dataSnapshot.child(activity.getString(R.string.database_game_max_player_count)).getValue(Integer.class);
        int maxPlayerCount = 0;
        if (maxPlayerCountInteger != null) {
            maxPlayerCount = maxPlayerCountInteger;
        }
        LocationRepresentation startLocation = dataSnapshot.child(activity.getString(R.string.database_game_start_location)).getValue(LocationRepresentation.class);

        if(isVisible == null || !isVisible || gameId == null || name == null || startLocation == null){
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
    private void displayGameInterface(TableLayout gameLayout,
                                      int buttonId,
                                      GameRepresentation gameRepresentation,
                                      String filterText) {
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

    private void createJoinButton(Button button, int buttonId, GameRepresentation gameRepresentation){
        // create join button
        button.setId(buttonId);
        button.setText(activity.getString(R.string.join_game_message));

        button.setOnClickListener(v -> joinLobbyFromJoinButton(gameRepresentation));

        //modify button if the game is full or if a space frees up
        addFullGameListener(button, gameRepresentation);
    }

    private void addFullGameListener(Button button, GameRepresentation gameRepresentation) {
        // Modify button if the game is full
        databaseReference.child(activity.getString(R.string.database_game))
                .child(gameRepresentation.getGameId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newPlayerCount = (int) snapshot.child(activity.getString(R.string.database_open_games_players)).getChildrenCount();
                if(newPlayerCount >= gameRepresentation.getMaxPlayerCount()){
                    button.setEnabled(false);
                    button.setText(activity.getResources().getString(R.string.join_game_full_message));
                }else{
                    button.setEnabled(true);
                    button.setText(activity.getResources().getString(R.string.join_game_message));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting new Player Count from DB");
                //FailSafe defaults. If we couldnt get the data, let's be safe and close the game
                button.setEnabled(false);
                button.setText(activity.getResources().getString(R.string.join_game_full_message));
            }
        });
    }

    private void createGameNameInfoDisplay(GameRepresentation gameRepresentation, TableRow gameRow){
        TextView nameView = new TextView(activity);
        String nameText = String.format((activity.getResources().getString(R.string.game_name_display)), gameRepresentation.getName());
        nameView.setText(nameText);
        nameView.setPadding(0, 0, 40, 0);
        nameView.setId(nameText.hashCode() + buttonId);
        gameRow.addView(nameView);
    }

    private void createPlayerCountNameInfoDisplay(GameRepresentation gameRepresentation, TableRow gameRow){
        TextView playerNumberView = new TextView(activity);
        String playerNumberText = String.format((activity.getResources().getString(R.string.game_player_number_display)),
                gameRepresentation.getPlayerCount(),
                gameRepresentation.getMaxPlayerCount());
        playerNumberView.setText(playerNumberText);

        //makes the playerCount dynamic so that it changes when people join and leave lobbies
        databaseReference.child(activity.getString(R.string.database_game))
                .child(gameRepresentation.getGameId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int newPlayerCount = (int) snapshot.child(activity.getString(R.string.database_open_games_players)).getChildrenCount();
                        String playerNumberText = String.format((activity.getResources().getString(R.string.game_player_number_display)),
                                newPlayerCount,
                                gameRepresentation.getMaxPlayerCount());
                        playerNumberView.setText(playerNumberText);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting new Player Count from DB");
                    }
                });

        gameRow.addView(playerNumberView);
    }

    private void joinLobbyFromJoinButton(GameRepresentation gameRepresentation){
        DatabaseReference gamePlayers = databaseReference.child(activity.getString(R.string.database_games)).child(gameRepresentation.getGameId()).child(activity.getString(R.string.database_open_games_players));
        final Player newPlayer = new Player(currentUser.getUserId(), currentUser.getName(), 0);
         gamePlayers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Player> players = snapshot.getValue(new GenericTypeIndicator<List<Player>>(){});
                players.add(newPlayer);
                gamePlayers.setValue(players);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error adding a player who joined the Game to the DB \n"+ error.getMessage());
            }

        });

        Intent lobbyIntent = new Intent(activity.getApplicationContext(), GameLobbyActivity.class);
        // Pass the game id to the lobby activity
        if(newPlayer == null){
            throw new IllegalArgumentException();
        }
        lobbyIntent.putExtra(activity.getString(R.string.join_game_lobby_intent_extra_id), gameRepresentation.getGameId())
                .putExtra(activity.getString(R.string.join_game_lobby_intent_extra_user), newPlayer)
                .putExtra(activity.getString(R.string.join_game_lobby_intent_extra_type_user), currentUser);
        activity.startActivity(lobbyIntent);
    }


}
