package sdp.moneyrun.ui.map;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.GameDatabaseProxy;
import sdp.moneyrun.database.RiddlesDatabase;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.CoinGenerationHelper;
import sdp.moneyrun.map.LocationCheckObjectivesCallback;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.map.TrackedMap;
import sdp.moneyrun.player.LocalPlayer;
import sdp.moneyrun.player.Player;


/*
this map implements all the functionality we will need.
 */
public class MapActivity extends TrackedMap implements OnMapReadyCallback {
    public static final double THRESHOLD_DISTANCE = 5;
    private static final int GAME_TIME = 10000;
    private static final double ZOOM_FOR_FEATURES = 15.;
    private static int chronometerCounter = 0;
    private final String TAG = MapActivity.class.getSimpleName();
    private final long ASYNC_CALL_TIMEOUT = 10L;
    private final String COIN_ID = "COIN";
    private final float ICON_SIZE = 1.5f;
    private Chronometer chronometer;
    private RiddlesDatabase riddleDb;
    private Location currentLocation;
    private SymbolLayer symbolLayer;
    private Player player;
    private GameDatabaseProxy proxyG;
    private TextView currentScoreView;
    private Button exitButton;
    private Button questionButton;
    private LocalPlayer localPlayer;
    private Game game;
    private String gameId;
    private boolean addedCoins = false;
    public static int COINS_TO_PLACE = 2;
    private boolean host = false;
    private final String DATABASE_COIN = "coins";
    private boolean useDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        player = (Player) getIntent().getSerializableExtra("player");
        gameId = getIntent().getStringExtra("currentGameId");
        if(gameId == null){
            gameId = getIntent().getStringExtra("gameId");
        }
        host = getIntent().getBooleanExtra("host", false);
        useDB = getIntent().getBooleanExtra("useDB", false);

        proxyG = new GameDatabaseProxy();

        localPlayer = new LocalPlayer();
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        createMap(savedInstanceState, R.id.mapView, R.layout.activity_map);
        mapView.getMapAsync(this);
        initChronometer();

        try {
            riddleDb = RiddlesDatabase.createInstance(getApplicationContext());
        } catch (RuntimeException e) {
            riddleDb = RiddlesDatabase.getInstance();
        }

        String default_score = getString(R.string.map_score_text, 0);
        currentScoreView = findViewById(R.id.map_score_view);
        currentScoreView.setText(default_score);

        exitButton = findViewById(R.id.close_map);
        questionButton = findViewById(R.id.new_question);

        addExitButton();
        addQuestionButton();
        if(useDB){
            mapView.addOnDidFinishRenderingMapListener(new MapView.OnDidFinishRenderingMapListener() {
                @Override
                public void onDidFinishRenderingMap(boolean fully) {
                    if(gameId != null){
                        initializeGame(gameId);
                    }
                }
            });
        }
    }

    /**
     * @param gameId The game ID to fetch the game from the DB
     *    Place the coins if user is host and coins have not been placed yet
     *     It then finds the game and put the coins in it, the databse is then updated
     *    finishes by adding a listener for the coins
     */
    public void initializeGame(String gameId){

        if(!addedCoins && host){
            placeRandomCoins(COINS_TO_PLACE, 6);
            addedCoins = true;
        }
        proxyG.getGameDataSnapshot(gameId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                game = proxyG.getGameFromTaskSnapshot(task);

                if(player.equals(game.getHost())){
                    game.setCoins(localPlayer.getLocallyAvailableCoins(), false);
                }

                addCoinsListener();
            } else {
                Log.e(TAG, task.getException().getMessage());
            }
        });
    }

    private  void addCoinsListener(){

        proxyG.addCoinListener(game,new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<Coin>> t = new GenericTypeIndicator<List<Coin>>(){};
                List<Coin> newCoins = snapshot.getValue(t);
                if(newCoins == null){
                    return;
                }
                localPlayer.syncAvailableCoinsFromDb(new ArrayList<>(newCoins));

                symbolManager.deleteAll();
                for(Coin coin : newCoins){
                    addCoin(coin,false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(Game.class.getSimpleName(), error.getMessage());
            }

        });

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
            style.addImage(COIN_ID, BitmapUtils.getBitmapFromDrawable(getApplicationContext().getDrawable(R.drawable.coin_image)), true);
            enableLocationComponent(style);

        });

        this.mapboxMap = mapboxMap;

    }

    public Chronometer getChronometer() {
        return chronometer;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    public String getPlayerId() {
        return player.getPlayerId();
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
                Game.endGame(localPlayer.getCollectedCoins().size(), localPlayer.getScore(), player.getPlayerId(), MapActivity.this);
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
                    removeCoin(coin, false);
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
                    removeCoin(coin, true);
            }
        });
    }


    /**
     * @param coin Adds a coins to the list of remaining coins and adds it to the map
     */
    public void addCoin(Coin coin,boolean addLocal) {
        if (coin == null) {
            throw new NullPointerException("added coin cannot be null");
        }

        if(addLocal){
            localPlayer.addLocallyAvailableCoin(coin);
        }
        symbolManager.create(new SymbolOptions().withLatLng(new LatLng(coin.getLatitude(), coin.getLongitude())).withIconImage(COIN_ID).withIconSize(ICON_SIZE));
    }


    /**
     * @param coin removes a coin from the list of remaining coins, adds it to the list of collected coin
     *             and removes it from the map
     */
    public void removeCoin(Coin coin, Boolean collected) {

        if (coin == null) {
            throw new NullPointerException("removed coined is null");
        }

        localPlayer.updateCoins(coin, collected);

        if (collected) {
            String default_score = getString(R.string.map_score_text, localPlayer.getScore());
            currentScoreView.setText(default_score);
            //TODO: Inform database
            if(useDB) {
                  game.setCoins(localPlayer.toSendToDb(),true);
                  proxyG.updateGameInDatabase(game,null);

            }
        }

        LongSparseArray<Symbol> symbols = symbolManager.getAnnotations();

        for (int i = 0; i < symbols.size(); ++i) {
            Symbol symbol = symbols.valueAt(i);
            if (coin.getLatitude() == symbol.getLatLng().getLatitude() && coin.getLongitude() == symbol.getLatLng().getLongitude()) {
                symbolManager.delete(symbol);
            }
        }


    }

    public void placeRandomCoins(int number, int radius) {
        if (number <= 0 || radius <= 0) throw new IllegalArgumentException();
        for (int i = 0; i < number; i++) {
            Location coinLoc = null;
            coinLoc = CoinGenerationHelper.getRandomLocation(getCurrentLocation(), radius);
            addCoin(new Coin(coinLoc.getLatitude(), coinLoc.getLongitude(), CoinGenerationHelper.coinValue(coinLoc,getCurrentLocation())),true);
           // localPlayer.addLocallyAvailableCoin(new Coin(loc.getLatitude(), loc.getLongitude(), 1));
        }
    }

    /**
     * @param location Used to check if location is near a coin or not
     */
    @Override
    public void checkObjectives(Location location) {
        currentLocation = location;
        Coin coin = nearestCoin(location, localPlayer.getLocallyAvailableCoins(), THRESHOLD_DISTANCE);
        if (coin != null) {
            onButtonShowQuestionPopupWindowClick(mapView, true, R.layout.question_popup, riddleDb.getRandomRiddle(), coin);
        }

    }


}
