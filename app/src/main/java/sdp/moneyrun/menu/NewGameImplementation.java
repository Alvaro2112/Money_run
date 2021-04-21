package sdp.moneyrun.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.Coin;
import sdp.moneyrun.Game;
import sdp.moneyrun.LocationRepresentation;
import sdp.moneyrun.Player;
import sdp.moneyrun.R;
import sdp.moneyrun.Riddle;

public class NewGameImplementation extends MenuImplementation {
    public NewGameImplementation(Activity activity,
                                 DatabaseReference databaseReference,
                                 ActivityResultLauncher<String[]> requestPermissionsLauncher,
                                 FusedLocationProviderClient fusedLocationClient){
        super(activity, databaseReference, requestPermissionsLauncher, fusedLocationClient);
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
        View popupView = inflater.inflate(R.layout.new_game_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // show the popup window at wanted location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        LinearLayout newGameLayout = popupView.findViewById(R.id.newGameLayout);
        Button newGameButton = newGameLayout.findViewById(R.id.newGameSubmit);

        newGameButton.setOnClickListener(v -> onSubmitPostNewGame(newGameLayout));
    }

    /**
     * Create a new game.
     *
     * @param newGameLayout the game layout
     */
    public void onSubmitPostNewGame(LinearLayout newGameLayout) {
        TextView nameGameView = newGameLayout.findViewById(R.id.nameGameField);
        TextView maxPlayerNumberView = newGameLayout.findViewById(R.id.maxPlayerCountField);
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
    @SuppressLint("MissingPermission")
    public void postNewGame(String name, int maxPlayerCount) {
        DatabaseReference gameReference = databaseReference.child(activity.getString(R.string.database_open_games)).push();
        DatabaseReference startLocationReference = databaseReference
                .child(activity.getString(R.string.database_open_games))
                .child(gameReference.getKey()).child(activity.getString(R.string.database_open_games_start_location));

        // Grant permissions if necessary
        requestLocationPermissions(requestPermissionsLauncher);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, location -> {
                    // Got last known location. In some rare situations this can be null
                    // In this case, the game cannot be instanciated
                    if (location == null) {
                        Log.e("location", "Error getting location");
                    }

                    // Build new game given fields filled by user
                    String gameId = gameReference.getKey();
                    List<Player> players = new ArrayList<>();
                    List<Riddle> riddles = new ArrayList<>();
                    List<Coin> coins = new ArrayList<>();

                    Game game = new Game(name, players, maxPlayerCount, riddles, coins, location);

                    // post game to database
                    gameReference.setValue(game);

                    // Post location to database
                    LocationRepresentation locationRep = new LocationRepresentation(location.getLatitude(), location.getLongitude());
                    startLocationReference.setValue(locationRep);
                });
    }
}
