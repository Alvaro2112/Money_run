package sdp.moneyrun.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.collection.LongSparseArray;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.database.game.GameDatabaseProxy;
import sdp.moneyrun.database.riddle.Riddle;
import sdp.moneyrun.database.riddle.RiddlesDatabase;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.CoinGenerationHelper;
import sdp.moneyrun.map.LocationCheckObjectivesCallback;
import sdp.moneyrun.map.MapPlayerListAdapter;
import sdp.moneyrun.map.TrackedMap;
import sdp.moneyrun.player.LocalPlayer;
import sdp.moneyrun.player.Player;


/*
this map implements all the functionality we will need.
 */
@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal"})
public class MapActivity extends TrackedMap implements OnMapReadyCallback {
    public static final int THRESHOLD_DISTANCE = 7;
    public static final float DISTANCE_CHANGE_BEFORE_UPDATE = (float) 2;
    private static final long MINIMUM_TIME_BEFORE_UPDATE = 2000;
    private static int chronometerCounter;
    private final String TAG = MapActivity.class.getSimpleName();
    private final String COIN_ID = "COIN";
    private final float ICON_SIZE = 1.5f;
    private final int DISTANCE_BETWEEN_COINS = THRESHOLD_DISTANCE;
    private final int COIN_PLACEMENT_ATTEMPT_LIMIT = 50;
    private final double scalingFactor = 5000.0;
    private final int MapboxScale = 8;
    private final int numberOfSecondsInAMinute = 60;
    public int coinsToPlace;
    private Chronometer chronometer;
    @Nullable
    private RiddlesDatabase riddleDb;
    private Location currentLocation;
    private Player player;
    private GameDatabaseProxy proxyG;
    private TextView currentScoreView;
    private Button exitButton;
    private Button leaderboardButton;
    private LocalPlayer localPlayer;
    @Nullable
    private Game game;
    private String gameId;
    private boolean addedCoins = false;
    private boolean host = false;
    private MapPlayerListAdapter ldbListAdapter;
    private boolean hasEnded;
    private CircleManager circleManager;
    private double game_radius;
    private int game_time;
    @Nullable
    private Location game_center;
    private float circleRadius;
    private double shrinkingFactor = 0.99;
    private ArrayList<Coin> seenCoins;
    private ValueEventListener isEndedListener;
    private String locationMode;
    private boolean isAnswering;
    private boolean hasFoundMap;
    private OfflineManager offlineManager;
    private boolean startLocationIsSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        getExtras();
        initializeVariables();

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        createMap(savedInstanceState, R.id.mapView, R.layout.activity_map);
        mapView.getMapAsync(this);

        getViews();

        String default_score = getString(R.string.map_score_text, 0);
        currentScoreView.setText(default_score);

        addExitButton();
        addLeaderboardButton();

        mapView.addOnDidFinishRenderingMapListener(fully -> {
            if (gameId != null)
                initializeGame(gameId);
        });

        DatabaseProxy.addOfflineListener(this, TAG);

        if (locationMode != null)
            initLocationManager(locationMode);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseProxy.removeOfflineListener();
    }

    /**
     * Initializes all the logic to keep the location updated
     *
     * @param locationMode the location mode that will be used ("gps" for gps location, "network" for network location and null for the default MapBox location (i.e. getLastLocation))
     */
    public void initLocationManager(String locationMode) {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        @NonNull
        LocationListener locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (getMapboxMap() != null) {
                    getMapboxMap().getLocationComponent().forceLocationUpdate(location);
                    checkObjectives(location);
                }
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

        };
        locationManager.requestLocationUpdates(locationMode, MINIMUM_TIME_BEFORE_UPDATE, DISTANCE_CHANGE_BEFORE_UPDATE, locationListenerGPS);


    }


    /**
     * get all the extras necessary to run this activity
     */
    public void getExtras() {
        player = (Player) getIntent().getSerializableExtra("player");
        gameId = getIntent().getStringExtra("currentGameId");
        if (gameId == null) {
            gameId = getIntent().getStringExtra("gameId");
        }
        host = getIntent().getBooleanExtra("host", false);
        locationMode = getIntent().getStringExtra("locationMode");

    }

    /**
     * Initialize all the variables to start this activity
     */
    public void initializeVariables() {
        seenCoins = new ArrayList<>();
        proxyG = new GameDatabaseProxy();
        localPlayer = new LocalPlayer();
        hasEnded = false;
        isAnswering = false;
        hasFoundMap = false;
        try {
            riddleDb = RiddlesDatabase.createInstance(getApplicationContext());
        } catch (RuntimeException e) {
            riddleDb = RiddlesDatabase.getInstance();
        }
    }

    /**
     * Find all the views present in this activity
     */
    public void getViews() {
        currentScoreView = findViewById(R.id.map_score_view);
        chronometer = findViewById(R.id.mapChronometer);
        exitButton = findViewById(R.id.close_map);
        leaderboardButton = findViewById(R.id.in_game_scores_button);
    }

    /**
     * @param gameId The game ID to fetch the game from the DB
     *               Place the coins if user is host and coins have not been placed yet
     *               It then finds the game and put the coins in it, the database is then updated
     *               finishes by adding a listener for the coins
     */
    public void initializeGame(String gameId) {
        proxyG.getGameDataSnapshot(gameId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                game = proxyG.getGameFromTaskSnapshot(task);
                coinsToPlace = game.getNumCoins();
                game_radius = game.getRadius();
                circleRadius = (float) game_radius * MapboxScale;
                game_time = (int) Math.floor(game.getDuration() * numberOfSecondsInAMinute);
                setGameCenter();
                initChronometer();

                if (!addedCoins && host) {
                    placeRandomCoins(coinsToPlace, game_radius, THRESHOLD_DISTANCE);
                    addedCoins = true;
                }
                addCoinsListener();
                if (host) {
                    game.setCoins(localPlayer.getLocallyAvailableCoins(), true);
                    proxyG.updateGameInDatabase(game, null);
                } else {
                    localPlayer.setLocallyAvailableCoins((ArrayList<Coin>) game.getCoins());
                }
                initCircleAndSetEndListener();
            } else {
                Log.e(TAG, task.getException().getMessage());
            }
        });
    }

    /**
     * Setting Game Center depending on the host
     */
    private void setGameCenter() {
        game_center = getCurrentLocation();
        if (host)
            game.setStartLocation(game_center, false);
    }

    /**
     * Initializes circle and end game listener
     */
    private void initCircleAndSetEndListener() {
        initCircle();
        if (!host)
            listenEnded();
    }

    /**
     * Adds a coin listener to database proxy,
     * used to update the coin in the current game for the player map
     */
    private void addCoinsListener() {

        proxyG.addCoinListener(game, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<Coin>> t = new GenericTypeIndicator<List<Coin>>() {
                };

                List<Coin> newCoins = snapshot.getValue(t);

                if (newCoins == null) {
                    return;
                }

                localPlayer.syncAvailableCoinsFromDb(new ArrayList<>(newCoins));

                symbolManager.deleteAll();

                for (Coin coin : localPlayer.getLocallyAvailableCoins()) {
                    addCoin(coin, false);
                }
                if (!host && !startLocationIsSet) {
                    game_center = game.getStartLocation();
                    startLocationIsSet = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(Game.class.getSimpleName(), error.getMessage());
            }

        });

    }


    /**
     * Adds functionality to the leaderboard button
     */
    private void addLeaderboardButton() {
        leaderboardButton.setOnClickListener(v -> onButtonShowLeaderboard(mapView, true, R.layout.in_game_scores));
    }


    /**
     * Add the functionality of leaving the map Activity
     */
    private void addExitButton() {
        exitButton.setOnClickListener(v -> {
                    if (!hasEnded) {
                        endGame();
                    }
                }
        );
    }

    /**
     * Add functionality to the question button so that we can see random questions popup
     *
     * @param mapboxMap the map where everything will be done
     *                  this overrides the OnMapReadyCallback in the implemented interface
     *                  We set up the symbol manager here, it will allow us to add markers and other visual stuff on the map
     *                  Then we setup the location tracking
     */
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        callback = new LocationCheckObjectivesCallback(this, locationMode == null);

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            offlineManager = OfflineManager.getInstance(MapActivity.this);
            getDownloadedRegion();

            GeoJsonOptions geoJsonOptions = new GeoJsonOptions().withTolerance(0.4f);
            symbolManager = new SymbolManager(mapView, mapboxMap, style, null, geoJsonOptions);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
            style.addImage(COIN_ID, BitmapUtils.getBitmapFromDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.coin_image)), false);
            enableLocationComponent(style);

            circleManager = new CircleManager(mapView, mapboxMap, style);

        });

        this.mapboxMap = mapboxMap;
    }

    public Chronometer getChronometer() {
        return chronometer;
    }

    public int getChronometerCounter() {
        return chronometerCounter;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    public double getGameRadius() {
        return game_radius;
    }

    public double getGameDuration() {
        return game_time;
    }

    @Nullable
    public Location getGameCenter() {
        return game_center;
    }

    public boolean getHasFoundMap() {
        return hasFoundMap;
    }


    @Nullable
    public String getPlayerId() {
        return player.getPlayerId();
    }

    public MapPlayerListAdapter getLdbListAdapter() {
        return ldbListAdapter;
    }

    /**
     * The chronometer will countdown from the maximum time of a game to 0
     */
    private void initChronometer() {

        setupChronometer();
        chronometer.setOnChronometerTickListener(chronometer -> {
            if (chronometerCounter < game_time) {
                chronometerCounter += 1;
                circleRadius -= shrinkingFactor;
                if (chronometerCounter % 2 == 0)
                    initCircle();
            } else {
                if (!hasEnded) {
                    if (host)
                        game.setEnded(true, false);
                    endGame();
                }
            }
            chronometer.setFormat("REMAINING TIME " + (game_time - chronometerCounter));
        });
    }

    /**
     * Helper method to set the chronometer functionality
     */
    private void setupChronometer() {
        chronometer.start();
        chronometerCounter = 0;
        chronometer.setFormat("REMAINING TIME " + (game_time - chronometerCounter));
        shrinkingFactor = (circleRadius) / (2 * game_time);
        long current = System.currentTimeMillis() / 1000;
        if (host) {
            game.setStartTime(current, false);
        } else {
            long start = game.getStartTime();
            int loadingTimeInSeconds = 6;
            chronometerCounter = (int) (current - start + loadingTimeInSeconds);
        }
    }


    public void endGame() {
        hasEnded = true;
        Game.endGame(localPlayer.getCollectedCoins().size(), localPlayer.getScore(), player.getPlayerId(), game.getPlayers(), MapActivity.this, false);
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
    public void onButtonShowQuestionPopupWindowClick(View view, Boolean focusable, int layoutId, @NonNull Riddle riddle, @Nullable Coin coin) {

        PopupWindow popupWindow = Helpers.onButtonShowPopupWindowClick(this, view, focusable, layoutId);
        popupWindow.setOnDismissListener(() -> {
            if (coin != null) {
                removeCoin(coin, false);
            }
        });
        TextView tv = popupWindow.getContentView().findViewById(R.id.question);
        int correctId;

        //changes the text to the current question
        tv.setText(riddle.getQuestion());

        int[] buttonIds = {R.id.question_choice_1, R.id.question_choice_2, R.id.question_choice_3, R.id.question_choice_4};
        TextView buttonView;

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

    /**
     * Function used when clicking the leaderboard button,
     * fetches the game from the database and shows the name and score of the player in descending order
     *
     * @param view      The view on top of which the popup will be shown
     * @param focusable whether the popup is focused
     * @param layoutId  the layoutId of the popup
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onButtonShowLeaderboard(View view, Boolean focusable, int layoutId) {
        PopupWindow popupWindow = Helpers.onButtonShowPopupWindowClick(this, view, focusable, layoutId);

        proxyG.getGameDataSnapshot(gameId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                game = proxyG.getGameFromTaskSnapshot(task);
                ArrayList<Player> playerList = new ArrayList<>(game.getPlayers());
                playerList.sort((o1, o2) -> Integer.compare(o2.getScore(), o1.getScore()));

                ldbListAdapter = new MapPlayerListAdapter(this, new ArrayList<>());
                ListView leaderboard = popupWindow.getContentView().findViewById(R.id.in_game_scores_listview);
                ldbListAdapter.addAll(playerList);
                leaderboard.setAdapter(ldbListAdapter);
            } else {
                Log.e(TAG, task.getException().getMessage());
            }
        });

    }

    public void closePopupListener(@NonNull PopupWindow popupWindow, int Id) {
        popupWindow.getContentView().findViewById(Id).setOnClickListener(v -> popupWindow.dismiss());
    }

    /**
     * Listener on the wrong possible answers of the riddle
     *
     * @param popupWindow the current popup where the question is displayed
     * @param btnId       the id of one of the incorrect answer buttons
     * @param coin        the coin that triggered the question
     * @param riddle      the riddle that is being displayed
     */
    public void wrongAnswerListener(@NonNull PopupWindow popupWindow, int btnId, @Nullable Coin coin, @NonNull Riddle riddle) {

        popupWindow.getContentView().findViewById(btnId).setOnClickListener(v -> {
            MediaPlayer.create(this, R.raw.wrong_choice).start();
            popupWindow.dismiss();
            PopupWindow wrongAnswerPopupWindow = Helpers.onButtonShowPopupWindowClick(MapActivity.this, mapView, true, R.layout.wrong_answer_popup);
            TextView tv = wrongAnswerPopupWindow.getContentView().findViewById(R.id.question);
            tv.setText(riddle.getQuestion());

            tv = wrongAnswerPopupWindow.getContentView().findViewById(R.id.incorrect_answer_text);
            tv.setText(String.format("%s: '%s'", getString(R.string.incorrect_answer_message), riddle.getAnswer()));
            tv.setTextColor(Color.RED);


            closePopupListener(wrongAnswerPopupWindow, R.id.continue_run);

            if (coin != null) {
                removeCoin(coin, false);
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
    public void correctAnswerListener(@NonNull PopupWindow popupWindow, int btnId, @Nullable Coin coin, @NonNull Riddle riddle) {

        popupWindow.getContentView().findViewById(btnId).setOnClickListener(v -> {
            MediaPlayer.create(this, R.raw.correct_choice).start();
            popupWindow.dismiss();
            PopupWindow correctAnswerPopupWindow = Helpers.onButtonShowPopupWindowClick(MapActivity.this, mapView, true, R.layout.correct_answer_popup);
            TextView tv = correctAnswerPopupWindow.getContentView().findViewById(R.id.question);
            tv.setText(riddle.getQuestion());

            tv = correctAnswerPopupWindow.getContentView().findViewById(R.id.incorrect_answer_text);
            tv.setTextColor(Color.GREEN);

            closePopupListener(correctAnswerPopupWindow, R.id.collect_coin);
            if (coin != null)
                removeCoin(coin, true);
        });
    }


    /**
     * @param coin Adds a coins to the list of remaining coins and adds it to the map
     */
    public void addCoin(@Nullable Coin coin, boolean addLocal) {
        if (coin == null) {
            throw new NullPointerException("added coin cannot be null");
        }

        if (addLocal) {
            localPlayer.addLocallyAvailableCoin(coin);
        }
        symbolManager.create(new SymbolOptions().withLatLng(new LatLng(coin.getLatitude(), coin.getLongitude())).withIconImage(COIN_ID).withIconSize(ICON_SIZE));
    }


    /**
     * @param coin removes a coin from the list of remaining coins, adds it to the list of collected coin
     *             and removes it from the map
     */
    public void removeCoin(@Nullable Coin coin, Boolean collected) {
        if (coin == null) {
            throw new NullPointerException("removed coined is null");
        }

        localPlayer.updateCoins(coin, collected);

        if (collected) {
            String default_score = getString(R.string.map_score_text, localPlayer.getScore());
            currentScoreView.setText(default_score);
            List<Player> temp_players = game.getPlayers();
            temp_players.remove(player);
            player.setScore(localPlayer.getScore(), true);
            temp_players.add(player);
            game.setPlayers(temp_players, true);
            game.setCoins(localPlayer.toSendToDb(), true);
            proxyG.updateGameInDatabase(game, null);
        }

        deleteCoinFromMap(coin);
        boolean check = checkIfLegalPosition(coin, circleRadius, game_center.getLatitude(), game_center.getLongitude());
        if (check)
            Game.endGame(localPlayer.getCollectedCoins().size(), localPlayer.getScore(), player.getPlayerId(), game.getPlayers(), MapActivity.this, true);

    }

    /**
     * Removes the coin from the symbol manager
     *
     * @param coin Coin to delete from the Symbolmanager
     */
    public void deleteCoinFromMap(@NonNull Coin coin) {
        LongSparseArray<Symbol> symbols = symbolManager.getAnnotations();

        for (int i = 0; i < symbols.size(); ++i) {
            Symbol symbol = symbols.valueAt(i);
            if (coin.getLatitude() == symbol.getLatLng().getLatitude() && coin.getLongitude() == symbol.getLatLng().getLongitude()) {
                symbolManager.delete(symbol);
            }
        }
    }

    /**
     * @param number    number of coins to add
     * @param maxRadius max distance of a coin from the center
     * @param minRadius min distance of a coin from the center
     */
    public void placeRandomCoins(int number, double maxRadius, double minRadius) {
        if (number < 0 || maxRadius <= 0 || minRadius <= 0)
            throw new IllegalArgumentException("Number of coins to place is less than 0, number of coin is  " + number);
        if (minRadius >= maxRadius)
            throw new IllegalArgumentException("Min radius cannot be bigger or equal than max Radius ");
        int addedCoins = 0;
        for (int i = 0; i < number; i++) {
            Location loc = generateSingleLocation(maxRadius, minRadius);
            if (loc != null) {
                addCoin(new Coin(loc.getLatitude(), loc.getLongitude(), CoinGenerationHelper.coinValue(loc, getCurrentLocation())), true);
                addedCoins++;
            }
        }
        if (addedCoins < number) {
            Toast.makeText(this, "Only " + addedCoins + " could be added within the specified radius", Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    private Location generateSingleLocation(double maxRadius, double minRadius) {
        List<Coin> coins = localPlayer.getLocallyAvailableCoins();
        Location loc;
        int tries = 0;
        do {
            loc = CoinGenerationHelper.getRandomLocation(getCurrentLocation(), maxRadius, minRadius);
            tries++;
        } while (CoinGenerationHelper.minDistWithExistingCoins(loc, coins) < DISTANCE_BETWEEN_COINS && tries < COIN_PLACEMENT_ATTEMPT_LIMIT);
        if (tries == COIN_PLACEMENT_ATTEMPT_LIMIT) {
            loc = null;
        } // if no successful loc was found we return null
        return loc;
    }

    /**
     * @param location Used to check if location is near a coin or not
     */
    @Override
    public void checkObjectives(@NonNull Location location) {
        currentLocation = location;
        Coin coin = nearestCoin(location, localPlayer.getLocallyAvailableCoins(), THRESHOLD_DISTANCE);

        if (coin != null && !seenCoins.contains(coin) && !isAnswering) {
            isAnswering = true;
            seenCoins.add(coin);
            try {
                onButtonShowQuestionPopupWindowClick(mapView, true, R.layout.question_popup, riddleDb.getRandomRiddle(), coin);
            } catch (WindowManager.BadTokenException e) {
                seenCoins.remove(coin);
            }
            isAnswering = false;
        }
    }

    /**
     * Draws a circle of a predefined radius that corresponds to the radius inside which coins get generated
     * and it shrinks by a shrinking factor that is also predefined
     */
    public void initCircle() {
        circleManager.deleteAll();
        circleHelper(game_center, circleRadius / scalingFactor);
    }


    private void circleHelper(@NonNull Location centerCoordinates, double radiusInKilometers) {
        int numberOfSides = 256;
        int halfCircleDegrees = 180;
        double scaleLongitudeToKilometers = 111.319;
        double scaleLatitudeToKilometers = 110.574;
        CircleOptions circleOptions = new CircleOptions();
        double distanceX = radiusInKilometers / (scaleLongitudeToKilometers * Math.cos(centerCoordinates.getLatitude() * Math.PI / halfCircleDegrees));
        double distanceY = radiusInKilometers / scaleLatitudeToKilometers;
        double slice = (2 * Math.PI) / numberOfSides;
        double theta;
        double x;
        double y;
        LatLng position;
        for (int i = 0; i < numberOfSides; ++i) {
            theta = i * slice;
            x = distanceX * Math.cos(theta);
            y = distanceY * Math.sin(theta);
            position = new LatLng(centerCoordinates.getLatitude() + y,
                    centerCoordinates.getLongitude() + x);
            circleManager.create(circleOptions.withCircleRadius(3f).withLatLng(position).withCircleColor(ColorUtils.colorToRgbaString(getResources().getColor(R.color.colorPrimary))));
        }
    }


    /**
     * Check if there is a downloaded map and show a toast if there is one or not
     */
    private void getDownloadedRegion() {
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {

            @Override
            public void onList(@Nullable OfflineRegion[] offlineRegions) {
                if (offlineRegions == null || offlineRegions.length == 0) {
                    hasFoundMap = false;
                    Toast.makeText(getApplicationContext(), getString(R.string.no_offline_regions), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), getString(R.string.found_offline_regions), Toast.LENGTH_SHORT).show();

                hasFoundMap = true;
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MapActivity.this, getString(R.string.no_offline_regions), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public CircleManager getCircleManager() {
        return circleManager;
    }

    /**
     * Checks if the coin the player wants to pick up is allowed
     *
     * @param coin     coin that the player wants to pick up
     * @param radius   the current radius of the game
     * @param center_x the x coordinate of the center of the game
     * @param center_y the y coordinate of the center of the game
     * @return true if the coin is inside the game circle
     */
    public boolean checkIfLegalPosition(@NonNull Coin coin, double radius, double center_x, double center_y) {
        // calculates distance between the current coin and the game center
        double distance = Math.sqrt(Math.pow(coin.getLatitude() - center_x, 2) + Math.pow(coin.getLongitude() - center_y, 2));
        return (distance > radius);
    }


    /**
     * Sets up the listener for the end of the game, if host time reached 0 everyone should not be able to continue playing
     */
    public void listenEnded() {
        isEndedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.child("ended").getValue() != null && (boolean) snapshot.child("ended").getValue()) {
                    Game.endGame(localPlayer.getCollectedCoins().size(), localPlayer.getScore(), player.getPlayerId(), game.getPlayers(), MapActivity.this, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        };
        proxyG.addGameListener(game, isEndedListener);
    }


    @Override
    public void onBackPressed() {
    }
}
