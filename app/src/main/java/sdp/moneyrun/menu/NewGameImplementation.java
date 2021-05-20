package sdp.moneyrun.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.R;
import sdp.moneyrun.database.GameDatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.game.GameLobbyActivity;
import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.user.User;

public class NewGameImplementation extends MenuImplementation {
    TextView nameGameView;
    TextView maxPlayerNumberView;
    TextView numCoinsView;
    TextView gameRadiusView;
    TextView gameDurationView;

    public NewGameImplementation(Activity activity,
                                 DatabaseReference databaseReference,
                                 User user,
                                 ActivityResultLauncher<String[]> requestPermissionsLauncher,
                                 FusedLocationProviderClient fusedLocationClient) {
        super(activity, databaseReference, user, requestPermissionsLauncher, fusedLocationClient);
    }


    /**
     * Event that occurs when the user wants to add a new game.
     *
     * @param view the current view
     */
    public void onClickShowNewGamePopupWindow(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.new_game_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // show the popup window at wanted location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        LinearLayout newGameLayout = popupView.findViewById(R.id.newGameLayout);
        Button newGameButton = newGameLayout.findViewById(R.id.newGameSubmit);

        newGameButton.setOnClickListener(v -> onSubmitPostNewGame(newGameLayout, popupWindow));
    }

    /**
     * Create a new game.
     *
     * @param newGameLayout the game layout
     */
    public void onSubmitPostNewGame(@NonNull LinearLayout newGameLayout, PopupWindow popupWindow) {
        nameGameView = newGameLayout.findViewById(R.id.nameGameField);
        maxPlayerNumberView = newGameLayout.findViewById(R.id.maxPlayerCountField);
        numCoinsView = newGameLayout.findViewById(R.id.newGameNumCoins);
        gameRadiusView = newGameLayout.findViewById(R.id.newGameRadius);
        gameDurationView = newGameLayout.findViewById(R.id.newGameDuration);
        String gameName = nameGameView.getText().toString().trim();
        String maxPlayerNumberStr = maxPlayerNumberView.getText().toString().trim();
        String numCoinsStr = numCoinsView.getText().toString().trim();
        String gameRadiusStr = gameRadiusView.getText().toString().trim();
        String gameDurationStr = gameDurationView.getText().toString().trim();

        if (!checkNewGameStringParameters(gameName, maxPlayerNumberStr, numCoinsStr, gameRadiusStr, gameDurationStr)) {
            return;
        }
        int maxPlayerNumber = Integer.parseInt(maxPlayerNumberStr);
        int numCoinsNumber = Integer.parseInt(numCoinsStr);
        double gameRadiusNumber = Double.parseDouble(gameRadiusStr);
        double gameDurationNumber = Double.parseDouble(gameDurationStr);

        if (!checkNewGameParametersValues(maxPlayerNumber, numCoinsNumber, gameRadiusNumber, gameDurationNumber)) {
            return ;
        }

        popupWindow.dismiss();

        tryLocation(gameName, maxPlayerNumber, numCoinsNumber, gameRadiusNumber, gameDurationNumber);
    }

    /**
     * Checks if the strings inputted in the UI are good or not
     *
     * @param gameName           name of the game
     * @param maxPlayerNumberStr string inputted in the UI for the  player count
     * @param numCoinsStr        string inputted in the UI for the number of coin
     * @param gameRadiusStr      string inputted in the UI for the radius of th game
     * @param gameDurationStr    string inputted in the UI for the game duration
     * @return true if there is no problem with the strings else return false
     */
    private boolean checkNewGameStringParameters(@NonNull String gameName, @NonNull String maxPlayerNumberStr, @NonNull String numCoinsStr, @NonNull String gameRadiusStr, @NonNull String gameDurationStr) {
        boolean isEmpty = false;
        if (gameName.isEmpty()) {
            nameGameView.setError("This field is required");
            isEmpty = true;
        }

        if (maxPlayerNumberStr.isEmpty()) {
            maxPlayerNumberView.setError("This field is required");
            isEmpty = true;
        }

        if (numCoinsStr.isEmpty()) {
            numCoinsView.setError("This field is required");
            isEmpty = true;
        }
        if (gameRadiusStr.isEmpty()) {
            gameRadiusView.setError("This field is required");
            isEmpty = true;
        }
        if (gameDurationStr.isEmpty()) {
            gameDurationView.setError("This field is required");
            isEmpty = true;
        }
        return !isEmpty;
    }

    /**
     * @param maxPlayerNumber parsed int of the UI string parameter
     * @param numCoins        parsed int of the UI string parameter
     * @param gameRadius      parsed double of the UI string parameter
     * @param gameDuration    parsed double of the UI string parameter
     * @return true if there is no problem with the numbers else return false
     */
    private boolean checkNewGameParametersValues(int maxPlayerNumber, int numCoins, double gameRadius, double gameDuration) {
        boolean outOfBounds = false;
        if (maxPlayerNumber < 1) {
            maxPlayerNumberView.setError("There should be at least one player in a game");
            outOfBounds = true;
        }

        if (numCoins < 1) {
            numCoinsView.setError("There should be at least one coin in a game");
            outOfBounds = true;
        }

        if (gameRadius <= MapActivity.THRESHOLD_DISTANCE) {
            gameRadiusView.setError("The radius of the game should be bigger than 5 meters");
            outOfBounds = true;
        }
        if (gameDuration <= 0) {
            gameDurationView.setError("The game should last for more than 0 minute");
            outOfBounds = true;
        }
        return !outOfBounds;

    }


    /**
     * try getting the player's location to start the game
     */
    @SuppressLint("MissingPermission")
    private void tryLocation(String name, int maxPlayerCount, int numCoins, double gameRadius, double gameDuration) {
        // Grant permissions if necessary
        requestLocationPermissions(requestPermissionsLauncher);
        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
            // Got last known location. In some rare situations this can be null
            // In this case, the game cannot be instantiated
            if (location == null) {
                Log.e("location", "Error getting location");
                return;
            }
            postNewGame(name, maxPlayerCount, numCoins, gameRadius, gameDuration, location);
        });
    }

    public void postNewGame(String name, int maxPlayerCount, int numCoins, double gameRadius, double gameDuration, Location loc){
        // Build new game given fields filled by user
        List<Riddle> riddles = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        Player player = new Player(user.getUserId(), user.getName(), 0);
        Game game = new Game(name, player, maxPlayerCount, riddles, coins, loc, true, numCoins, gameRadius, gameDuration);
        game.setId(user.getUserId());
        // post game to database
        GameDatabaseProxy gdb = new GameDatabaseProxy();
        gdb.putGame(game);
        launchLobbyActivity(game.getId(), player);
    }


    private void launchLobbyActivity(String gameId, Player player) {
        Intent lobbyIntent = new Intent(activity.getApplicationContext(), GameLobbyActivity.class);
        lobbyIntent.putExtra(activity.getString(R.string.join_game_lobby_intent_extra_id), gameId);
        lobbyIntent.putExtra(activity.getString(R.string.join_game_lobby_intent_extra_user), player);
        lobbyIntent.putExtra(activity.getString(R.string.join_game_lobby_intent_extra_type_user), user);
        activity.startActivity(lobbyIntent);
        activity.finish();
    }
}
