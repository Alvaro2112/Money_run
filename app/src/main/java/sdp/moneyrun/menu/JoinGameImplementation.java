package sdp.moneyrun.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.LocationManager;
import android.media.MediaPlayer;
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
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.game.GameRepresentation;
import sdp.moneyrun.location.AndroidLocationService;
import sdp.moneyrun.location.LocationRepresentation;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

public class JoinGameImplementation extends MenuImplementation {

    public static final String TAG_GAME_PREFIX = "game";
    // Distance in meters
    private static final String TAG = JoinGameImplementation.class.getSimpleName();
    private final boolean focusable;
    private final int layoutId;
    @Nullable
    private final User currentUser;
    private final String LOCATION_MODE = LocationManager.GPS_PROVIDER;
    private int buttonId;

    public JoinGameImplementation(Activity activity,
                                  DatabaseReference databaseReference,
                                  @Nullable User user,
                                  ActivityResultLauncher<String[]> requestPermissionsLauncher,
                                  AndroidLocationService locationService,
                                  boolean focusable,
                                  int layoutId) {
        super(activity, databaseReference, user, requestPermissionsLauncher, locationService);
        if (user == null) {
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
        MediaPlayer.create(activity.getApplicationContext(), R.raw.button_press).start();
        // Show popup
        PopupWindow popupWindow = onButtonShowPopupWindowClick(view, focusable, layoutId);
        // Load game list
        onJoinGamePopupWindowLoadGameList(popupWindow);
    }

    /**
     * Displays every current games.
     *
     * @param popupWindow
     */
    private void onJoinGamePopupWindowLoadGameList(@NonNull PopupWindow popupWindow) {
        LinearLayout openGamesLayout = popupWindow.getContentView().findViewById(R.id.openGamesLayout);

        Button filterButton = popupWindow.getContentView().findViewById(R.id.join_game_button_filter);

        filterButton.setOnClickListener(v -> {
            EditText filterEditText = popupWindow.getContentView().findViewById(R.id.join_game_text_filter);
            openGamesLayout.removeAllViews();

            String filterText = filterEditText.getText().toString().trim().toLowerCase(Locale.getDefault());
            loadGameListGivenFilter(popupWindow, openGamesLayout, filterText);
        });

        // First load the game list without any filter.
        loadGameListGivenFilter(popupWindow, openGamesLayout, null);
    }

    private void loadGameListGivenFilter(@NonNull PopupWindow popupWindow, @NonNull LinearLayout openGamesLayout, @Nullable String filterText) {
        List<GameRepresentation> gameRepresentations = new ArrayList<>();
        Task<DataSnapshot> taskDataSnapshot = getTaskGameRepresentations(gameRepresentations);
        taskDataSnapshot.addOnSuccessListener(dataSnapshot -> {
            TableLayout gameLayout = new TableLayout(activity);

            buttonId = 0;
            for (GameRepresentation gameRepresentation : gameRepresentations) {
                String lowerName = gameRepresentation.getName().toLowerCase(Locale.getDefault());

                // Get user location and compute distance
                AndroidLocationService locationService = ((MenuActivity) activity).getLocationService();
                LocationRepresentation location = locationService.getCurrentLocation();
                if (location == null || gameRepresentation.getStartLocation() == null) {
                    return;
                }

                double distance = location.distanceTo(gameRepresentation.getStartLocation());
                if ((filterText == null || lowerName.contains(filterText)) && distance <= MAX_DISTANCE_TO_JOIN_GAME) {
                    displayGameInterface(popupWindow, gameLayout, buttonId, gameRepresentation);
                    buttonId++;
                }
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
    @NonNull
    private Task<DataSnapshot> getTaskGameRepresentations(@NonNull List<GameRepresentation> gameRepresentations) {

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
                            if (gameRepresentation != null) {
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

    @Nullable
    private GameRepresentation defineGameFromDatabase(@NonNull DataSnapshot dataSnapshot) {
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

        if (isVisible == null || !isVisible || gameId == null || name == null || startLocation == null) {
            return null;
        }

        return new GameRepresentation(gameId, name, playerCount, maxPlayerCount, startLocation);
    }

    /**
     * Displays a game.
     *
     * @param gameLayout         the game layout
     * @param buttonId           the id of the join button
     * @param gameRepresentation the representation of the game to display
     */
    private void displayGameInterface(@NonNull PopupWindow popupWindow, @NonNull TableLayout gameLayout,
                                      int buttonId,
                                      @NonNull GameRepresentation gameRepresentation) {
        // create game layout
        TableRow gameRow = new TableRow(activity);
        TableLayout.LayoutParams gameParams = new TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        Button button = new Button(activity);
        createJoinButton(popupWindow, button, buttonId, gameRepresentation);

        gameRow.addView(button);

        // create game name display
        createGameNameInfoDisplay(gameRepresentation, gameRow);

        // create player count display
        createPlayerCountNameInfoDisplay(gameRepresentation, gameRow);

        gameLayout.addView(gameRow, gameParams);
        // Define view tag
        gameLayout.setTag(TAG_GAME_PREFIX + gameRepresentation.getGameId());
    }

    @SuppressLint("MissingPermission")
    private void createJoinButton(@NonNull PopupWindow popupWindow, @NonNull Button button, int buttonId, @NonNull GameRepresentation gameRepresentation) {
        // create join button
        button.setId(buttonId);
        button.setText(activity.getString(R.string.join_game_message));

        button.setOnClickListener(v -> {
            Helpers.joinLobbyFromJoinButton(gameRepresentation, databaseReference, activity, currentUser, LOCATION_MODE);
            popupWindow.dismiss();
        });

        //modify button if the game is full or if a space frees up
        addFullGameListener(button, gameRepresentation);
        // Modify button if game is too far
        // Grant permissions if necessary
        requestLocationPermissions(requestPermissionsLauncher);
    }

    private void addFullGameListener(@NonNull Button button, @NonNull GameRepresentation gameRepresentation) {
        // Modify button if the game is full
        databaseReference.child(activity.getString(R.string.database_game))
                .child(gameRepresentation.getGameId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newPlayerCount = (int) snapshot.child(activity.getString(R.string.database_open_games_players)).getChildrenCount();
                if (newPlayerCount >= gameRepresentation.getMaxPlayerCount()) {
                    button.setEnabled(false);
                    button.setText(activity.getResources().getString(R.string.join_game_full_message));
                } else {
                    button.setEnabled(true);
                    button.setText(activity.getResources().getString(R.string.join_game_message));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting new Player Count from DB");
                //FailSafe defaults. If we couldn't get the data, let's be safe and close the game
                button.setEnabled(false);
                button.setText(activity.getResources().getString(R.string.join_game_full_message));
            }
        });
    }

    private void createGameNameInfoDisplay(@NonNull GameRepresentation gameRepresentation, @NonNull TableRow gameRow) {
        TextView nameView = new TextView(activity);
        String nameText = String.format((activity.getResources().getString(R.string.game_name_display)), gameRepresentation.getName());
        nameView.setText(nameText);
        nameView.setPadding(0, 0, 40, 0);
        gameRow.addView(nameView);
    }

    private void createPlayerCountNameInfoDisplay(@NonNull GameRepresentation gameRepresentation, @NonNull TableRow gameRow) {
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
}
