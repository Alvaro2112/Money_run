package sdp.moneyrun.ui.map;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.RiddlesDatabase;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.CoinGenerationHelper;
import sdp.moneyrun.map.LocationCheckObjectivesCallback;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.map.TrackedMap;


/*
this map implements all the functionality we will need.
 */
public class MapActivity extends TrackedMap implements OnMapReadyCallback {
    public static final double THRESHOLD_DISTANCE = 5.;
    private static final int GAME_TIME = 10000;
    private static final double ZOOM_FOR_FEATURES = 15.;
    private static int chronometerCounter = 0;
    private final String TAG = MapActivity.class.getSimpleName();
    private final List<Coin> remainingCoins = new ArrayList<>();
    private final List<Coin> collectedCoins = new ArrayList<>();
    private final List<Coin> disabledLocalCoins = new ArrayList<>();
    private final long ASYNC_CALL_TIMEOUT = 10L;
    private Chronometer chronometer;
    private RiddlesDatabase riddleDb;
    private Location currentLocation;
    private int playerId;
    private TextView currentScoreView;
    private int currentScore = 0;
    private Button exitButton;
    private Button questionButton;

    /**
     * //source : https://stackoverflow.com/questions/8832071/how-can-i-get-the-distance-between-two-point-by-latlng
     *
     * @param lat_a
     * @param lng_a
     * @param lat_b
     * @param lng_b
     * @return the distance in meters between two coordinates
     */
    public static double distance(double lat_a, double lng_a, double lat_b, double lng_b) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return distance * meterConversion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playerId = getIntent().getIntExtra("playerId", 0);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        createMap(savedInstanceState, R.id.mapView, R.layout.activity_map);
        mapView.getMapAsync(this);
        initChronometer();

        try {
            riddleDb = RiddlesDatabase.createInstance(getApplicationContext());
        } catch (RuntimeException e) {
            riddleDb = RiddlesDatabase.getInstance();
        }

        currentScoreView = findViewById(R.id.map_score_view);
        String default_score = getString(R.string.map_score_text, 0);
        currentScoreView.setText(default_score);
        exitButton = findViewById(R.id.close_map);
        questionButton = findViewById(R.id.new_question);
        addExitButton();
        addQuestionButton();
    }

    /**
     * Add the functionality of leaving the map Activity
     */
    private void addExitButton() {
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Add functionality to the question button so that we can see random questions popup
     */
    private void addQuestionButton() {
        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonShowQuestionPopupWindowClick(mapView, true, R.layout.question_popup, riddleDb.getRandomRiddle(), null);

            }
        });
    }

    /**
     * @param mapboxMap the map where everything will be done
     *                  this overried the OnMapReadyCallback in the implemented interface
     *                  We set up the symbol manager here, it will allow us to add markers and other visual stuff on the map
     *                  Then we setup the location tracking
     */
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        callback = new LocationCheckObjectivesCallback(this);

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

            GeoJsonOptions geoJsonOptions = new GeoJsonOptions().withTolerance(0.4f);
            symbolManager = new SymbolManager(mapView, mapboxMap, style, null, geoJsonOptions);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
            enableLocationComponent(style);

        });

        this.mapboxMap = mapboxMap;
    }

    public Chronometer getChronometer() {
        return chronometer;
    }

    public List<Coin> getRemainingCoins() {
        return remainingCoins;
    }

    public List<Coin> getCollectedCoins() {
        return collectedCoins;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public int getPlayerId() {
        return playerId;
    }

    /**
     * The chronometer will countdown from the maximum time of a game to 0
     */
    private void initChronometer() {

        chronometer = findViewById(R.id.mapChronometer);
        chronometer.start();

        chronometer.setOnChronometerTickListener(chronometer -> {
            if (chronometerCounter < GAME_TIME) {
                chronometerCounter += 1;
            } else {
                Game.endGame(collectedCoins, playerId, MapActivity.this);
            }

            chronometer.setFormat("REMAINING TIME " + (GAME_TIME - chronometerCounter));

        });
    }


    /**
     * Creates the popup where the question and the possible answers will be shown to the user
     *
     * @param view      The view on top of which the popup will be shown
     * @param focusable whether the popup is focused
     * @param layoutId  the layoutId of the popup
     * @param riddle    The riddle that will be asked
     * @param coin      the coin that triggered a riddle
     */
    public void onButtonShowQuestionPopupWindowClick(View view, Boolean focusable, int layoutId, Riddle riddle, Coin coin) {

        PopupWindow popupWindow = Helpers.onButtonShowPopupWindowClick(this, view, focusable, layoutId);
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

            //Found the id of the solution button
            if (riddle.getPossibleAnswers()[i].equals(riddle.getAnswer())) {
                correctId = buttonIds[i];
                correctAnswerListener(popupWindow, correctId, coin, riddle);
            } else {
                wrongAnswerListener(popupWindow, buttonIds[i], coin, riddle);
            }
        }

    }

    public void closePopupListener(PopupWindow popupWindow, int Id) {
        popupWindow.getContentView().findViewById(Id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * Listener on the wrong possible answers of the riddle
     *
     * @param popupWindow the current popup where the question is displayed
     * @param btnId       the id of one of the incorrect answer buttons
     * @param coin        the coin that triggered the question
     * @param riddle      the riddle that is being displayed
     */
    public void wrongAnswerListener(PopupWindow popupWindow, int btnId, Coin coin, Riddle riddle) {

        popupWindow.getContentView().findViewById(btnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
                PopupWindow wrongAnswerPopupWindow = Helpers.onButtonShowPopupWindowClick(MapActivity.this, mapView, true, R.layout.wrong_answer_popup);
                TextView tv = wrongAnswerPopupWindow.getContentView().findViewById(R.id.question);
                tv.setText(riddle.getQuestion());

                tv = wrongAnswerPopupWindow.getContentView().findViewById(R.id.incorrect_answer_text);
                tv.setText("You are incorrect!\n The answer was " + "'" + riddle.getAnswer() + "'");
                tv.setTextColor(Color.RED);


                closePopupListener(wrongAnswerPopupWindow, R.id.continue_run);

                if (coin != null) {
                    disabledLocalCoins.add(coin);
                    removeCoin(coin, true);
                }
            }
        });
    }


    /**
     * Listener on the correct answer of the riddle
     *
     * @param popupWindow the current popup where the question is displayed
     * @param btnId       the id of the correct answer button
     * @param coin        the coin that triggered the question
     * @param riddle      the riddle that is being displayed
     */
    public void correctAnswerListener(PopupWindow popupWindow, int btnId, Coin coin, Riddle riddle) {

        popupWindow.getContentView().findViewById(btnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
                PopupWindow correctAnswerPopupWindow = Helpers.onButtonShowPopupWindowClick(MapActivity.this, mapView, true, R.layout.correct_answer_popup);
                TextView tv = correctAnswerPopupWindow.getContentView().findViewById(R.id.question);
                tv.setText(riddle.getQuestion());

                tv = correctAnswerPopupWindow.getContentView().findViewById(R.id.incorrect_answer_text);
                tv.setTextColor(Color.GREEN);

                closePopupListener(correctAnswerPopupWindow, R.id.collect_coin);

                if (coin != null)
                    removeCoin(coin, false);
                //get points

            }
        });
    }


    /**
     * @param coin Adds a coins to the list of remaining coins and adds it to the map
     */
    public void addCoin(Coin coin) {
        if (coin == null) {
            throw new NullPointerException("added coin is null");
        }
        remainingCoins.add(coin);
        symbolManager.create(new SymbolOptions().withLatLng(new LatLng(coin.getLatitude(), coin.getLongitude())));
    }


    /**
     * @param coin removes a coin from the list of remaining coins, adds it to the list of collected coin
     *             and removes it from the map
     */
    public void removeCoin(Coin coin, Boolean collected) {

        if (coin == null) {
            throw new NullPointerException("removed coined is null");
        }

        if (collected) {
            remainingCoins.remove(coin);
            collectedCoins.add(coin);
            currentScore += coin.getValue();
            String default_score = getString(R.string.map_score_text, currentScore);
            currentScoreView.setText(default_score);
        } else {
            remainingCoins.remove(coin);
        }


        LongSparseArray<Symbol> symbols = symbolManager.getAnnotations();
        for (int i = 0; i < symbols.size(); ++i) {
            Symbol symbol = symbols.valueAt(i);
            if (coin.getLatitude() == symbol.getLatLng().getLatitude() && coin.getLongitude() == symbol.getLatLng().getLongitude()) {
                symbolManager.delete(symbol);
            }
        }
    }


    /**
     * @param location Used to check if location is near a coin or not
     */
    public void checkObjectives(Location location) {
        currentLocation = location;
        Coin coin = isNearCoin(location);
        if (coin != null) {
            //What to do when a player gets to a coin, we need the logic here of what coins the player is allowed to take
        }
    }

    /**
     * @param location
     * @return the index of the closest coin whose distance is lower than a threshold or -1 if there are none
     */
    public Coin isNearCoin(Location location) {

        double player_lat = location.getLatitude();
        double player_long = location.getLongitude();

        double min_dist = Integer.MAX_VALUE;
        double curr_dist = Integer.MAX_VALUE;
        Coin min_coin = null;
        Coin curr_coin;

        for (int i = 0; i < remainingCoins.size(); ++i) {

            curr_coin = remainingCoins.get(i);

            curr_dist = distance(player_lat, player_long, curr_coin.getLatitude(), curr_coin.getLongitude());

            if (curr_dist < THRESHOLD_DISTANCE && curr_dist < min_dist) {
                min_dist = curr_dist;
                min_coin = curr_coin;
            }
        }

        return min_coin;
    }

    public void placeRandomCoins(int number, int radius) {
        if (number <= 0 || radius <= 0) throw new IllegalArgumentException();
        for (int i = 0; i < number; i++) {
            Location loc = null;
            do {
                loc = CoinGenerationHelper.getRandomLocation(currentLocation, radius);
            } while (!isLocationAppropriate(loc));
            remainingCoins.add(new Coin(loc.getLatitude(), loc.getLongitude(), 0));
        }
    }


}