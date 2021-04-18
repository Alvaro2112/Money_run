package sdp.moneyrun.map;

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

import sdp.moneyrun.Coin;
import sdp.moneyrun.Game;
import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.Riddle;
import sdp.moneyrun.RiddlesDatabase;


/*
this map implements all the functionality we will need.
 */
public class MapActivity extends TrackedMap implements OnMapReadyCallback {
    private static final int GAME_TIME = 10000;
    private static final double THRESHOLD_DISTANCE = 5.;
    private static int chronometerCounter = 0;
    private final List<Coin> remainingCoins = new ArrayList<>();
    private final List<Coin> collectedCoins = new ArrayList<>();
    private Chronometer chronometer;
    private RiddlesDatabase riddleDb;
    private Location currentLocation;
    private int playerId;

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
                onButtonShowQuestionPopupWindowClick(mapView, true, R.layout.question_popup, riddleDb.getRandomRiddle());
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
                chronometer.setFormat("REMAINING TIME "+String.valueOf(GAME_TIME - chronometerCounter));

        });
    }

    public void onButtonShowQuestionPopupWindowClick(View view, Boolean focusable, int layoutId, Riddle riddle) {

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

            if (riddle.getPossibleAnswers()[i].equals(riddle.getAnswer()))
                correctId = buttonIds[i];
        }

        popupWindow.getContentView().findViewById(correctId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
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
    public void removeCoin(Coin coin) {

        if (coin == null) {
            throw new NullPointerException("removed coined is null");
        }
        remainingCoins.remove(coin);
        collectedCoins.add(coin);
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
        int coinIdx = isNearCoin(location);
        if (coinIdx >= 0) {
            removeCoin(remainingCoins.get(coinIdx));
            onButtonShowQuestionPopupWindowClick(this.mapView, true, R.layout.question_popup, riddleDb.getRandomRiddle());
        }
    }

    /**
     * @param location
     * @return the index of the closest coin whose distance is lower than a threshold or -1 if there are none
     */
    public int isNearCoin(Location location) {

        double player_lat = location.getLatitude();
        double player_long = location.getLongitude();

        double min_dist = 10000;
        int min_index = -1;
        for (int i = 0; i < remainingCoins.size(); ++i) {
            Coin coin = remainingCoins.get(i);
            double cur_dist = distance(player_lat, player_long, coin.getLatitude(), coin.getLongitude());
            if (cur_dist < THRESHOLD_DISTANCE && cur_dist < min_dist) {
                min_dist = cur_dist;
                min_index = i;
            }
        }
        return min_index;
    }
}
