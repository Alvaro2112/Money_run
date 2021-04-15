package sdp.moneyrun.map;

import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import sdp.moneyrun.Coin;
import sdp.moneyrun.R;
import sdp.moneyrun.Riddle;
import sdp.moneyrun.RiddlesDatabase;


/*
this map implements all the functionality we will need.
 */
public class MapActivity extends TrackedMap implements OnMapReadyCallback {
    private final String TAG = MapActivity.class.getSimpleName();
    private static final int GAME_TIME = 100;
    private static int chronometerCounter =0;
    private Chronometer chronometer;
    private List<Coin> remainingCoins = new ArrayList<>();
    private List<Coin> collectedCoins = new ArrayList<>();
    private static final double THRESHOLD_DISTANCE = 5.;
    private static final double ZOOM_FOR_FEATURES = 16.;
    private RiddlesDatabase riddleDb;
    private Location currentLocation;
    private  long ASYNC_CALL_TIMEOUT = 10L;

    private final List<String> INAPPROPRIATE_LOCATIONS = Arrays.asList("building", "motorway", "route cantonale");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        createMap(savedInstanceState,R.id.mapView,R.layout.activity_map);
        mapView.getMapAsync(this);
        initChronometer();


        try{
            riddleDb = RiddlesDatabase.createInstance(getApplicationContext());
        }
        catch(RuntimeException e){
            riddleDb = RiddlesDatabase.getInstance();
        }



    }

    /**
     * @param mapboxMap the map where everything will be done
     *     this overried the OnMapReadyCallback in the implemented interface
     *      We set up the symbol manager here, it will allow us to add markers and other visual stuff on the map
     *      Then we setup the location tracking
     */
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        callback = new LocationCheckObjectivesCallback(this);
        mapboxMap.setStyle(Style.MAPBOX_STREETS,style -> {
            GeoJsonOptions geoJsonOptions = new GeoJsonOptions().withTolerance(0.4f);
            symbolManager =  new SymbolManager(mapView, mapboxMap, style, null, geoJsonOptions);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
            enableLocationComponent(style);
                });
        this.mapboxMap = mapboxMap;
    }

    public Chronometer getChronometer(){ return chronometer; }
    public List<Coin> getRemainingCoins(){return remainingCoins;}
    public List<Coin> getCollectedCoins(){return collectedCoins;}
    public Location getCurrentLocation(){return currentLocation;}
    /**
     * The chronometer will countdown from the maximum time of a game to 0
     */
    private void initChronometer(){
        chronometer = (Chronometer) findViewById(R.id.mapChronometer);
        chronometer.start();
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if(chronometerCounter < GAME_TIME){
                    chronometerCounter += 1;
                }
                else{
                }
                chronometer.setFormat("REMAINING TIME "+String.valueOf(GAME_TIME - chronometerCounter));
            }
        });
    }

    public void onButtonShowQuestionPopupWindowClick(View view, Boolean focusable, int layoutId, Riddle riddle) {

        PopupWindow popupWindow = onButtonShowPopupWindowClick(view, focusable, layoutId);
        TextView tv = popupWindow.getContentView().findViewById(R.id.question);
        int correctId = 0;

        //changes the text to the current question
        tv.setText(riddle.getQuestion());

        int[] buttonIds = {R.id.question_choice_1, R.id.question_choice_2, R.id.question_choice_3, R.id.question_choice_4};
        TextView buttonView = tv;

        //Loops to find the ID of the button solution and assigns the text to each button
        for (int i = 0; i < 4; i++){

            buttonView = popupWindow.getContentView().findViewById(buttonIds[i]);
            buttonView.setText(riddle.getPossibleAnswers()[i]);

            if(riddle.getPossibleAnswers()[i].equals(riddle.getAnswer()))
                correctId = buttonIds[i];
        }

        popupWindow.getContentView().findViewById(correctId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    public PopupWindow onButtonShowPopupWindowClick(View view, Boolean focusable, int layoutId) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layoutId, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window at wanted location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupWindow;
    }


    /**
     * @param coin
     * Adds a coins to the list of remaining coins and adds it to the map
     */
    public void addCoin(Coin coin){
        if(coin == null){
            throw new NullPointerException("added coin is null");
        }
        remainingCoins.add(coin);
        symbolManager.create(coin.getSymbolOption());
    }

    /**
     * @param coin
     * removes a coin from the list of remaining coins, adds it to the list of collected coin
     * and removes it from the map
     */
    public void removeCoin(Coin coin){

        if(coin == null){
            throw new NullPointerException("removed coined is null");
        }
        remainingCoins.remove(coin);
        collectedCoins.add(coin);
        LongSparseArray<Symbol> symbols = symbolManager.getAnnotations();
        for(int i = 0; i< symbols.size();++i){
            Symbol symbol = symbols.valueAt(i);
            if(coin.getLatitude() == symbol.getLatLng().getLatitude() && coin.getLongitude() == symbol.getLatLng().getLongitude() ){
                symbolManager.delete(symbol);
            }
        }
    }


    public void moveCameraTo(float latitude, float longitude, double zoom){
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(zoom)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    public void moveCameraWithoutAnimation(double latitude, double longitude, double zoom){
        CountDownLatch moved = new CountDownLatch(1);
        MapboxMap.CancelableCallback callback = new MapboxMap.CancelableCallback() {
            @Override
            public void onCancel() {
                Log.e(TAG, "Camera movement was cancelled");
            }

            @Override
            public void onFinish() {
                moved.countDown();
            }
        };
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(zoom)
                .build();
        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        try {
            moved.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assert(moved.getCount() == 0);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("Moved is now " + moved.getCount());
    }

    /**
     * @param location
     * Used to check if location is near a coin or not
     */
    public void  checkObjectives(Location location){
        currentLocation = location;
        int coinIdx = isNearCoin(location);
        if(coinIdx >= 0){
            removeCoin(remainingCoins.get(coinIdx));
            onButtonShowQuestionPopupWindowClick(this.mapView, true, R.layout.question_popup, riddleDb.getRandomRiddle());
        }
    }


    /**
     * //source : https://stackoverflow.com/questions/8832071/how-can-i-get-the-distance-between-two-point-by-latlng
     * @param lat_a
     * @param lng_a
     * @param lat_b
     * @param lng_b
     * @return  the distance in meters between two coordinates
     */
    public static double distance(double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return distance * meterConversion;
    }


    /**
     * @param location
     * @return the index of the closest coin whose distance is lower than a threshold or -1 if there are none
     */
    public  int isNearCoin(Location location){

        double player_lat = location.getLatitude();
        double player_long = location.getLongitude();

        double min_dist = 10000;
        int min_index = -1;
        for(int i=0; i< remainingCoins.size();++i){
            Coin coin = remainingCoins.get(i);
            double cur_dist = distance(player_lat,player_long,coin.getLatitude(),coin.getLongitude());
            if( cur_dist < THRESHOLD_DISTANCE && cur_dist < min_dist ){
                min_dist = cur_dist;
                min_index = i;
            }
        }
        return min_index;
    }

    public List<Coin> getRandomlyPlacedCoins (int number, int radius, long seed){
        if (number <= 0 || radius <= 0) throw new IllegalArgumentException();
        ArrayList<Coin> coins = new ArrayList<>();
        for(int i = 0; i<number; i++){
            Location loc = null;
            do{
               loc = getRandomLocation(radius, seed);
            }while(!isLocationAppropriate(loc));
            coins.add(new Coin(loc.getLatitude(),loc.getLongitude()));
        }
        return coins;
    }

    public Location getRandomLocation(int radius, long seed){
        if(radius<=0) throw new IllegalArgumentException();
        double lat = currentLocation.getLatitude();
        double lon = currentLocation.getLongitude();
        Random random = new Random(seed);
        double latDifference = random.nextDouble()*radius;
        double lonDifference = random.nextDouble()*radius;
        Location location = new Location("");
        location.setLongitude(lon + lonDifference);
        location.setLatitude(lat + latDifference);
        return location;
    }

    public boolean isLocationAppropriate(Location location){
        List<Feature> features = getFeatureAtLocation(location);
        boolean hasAtLeastAProperty = false;
        String [] relevantFields = new String[] {"type", "class", "name"};
        if(features.size() > 0) {
            for (Feature feature : features) {
                System.out.println("Feature is " + feature);
                if (feature != null && feature.properties() != null){
                    if(!feature.properties().toString().equals("{}")){
                        hasAtLeastAProperty = true;
                    }//If none of the feature has a property field, it's probably a body of water

                    System.out.println(feature.properties().toString());
                    if(!checkIndividualFeature(feature)) return false; //A feature was deemed inappropriate
                }// Advised by MapBox

            }// A location may yield multiple feature and we check that none is inappropriate

            return hasAtLeastAProperty;
        } //If there's no feature at all something is wrong and it is probably not appropriate to put a coin there
        return false;
    }


    private boolean checkIndividualFeature(Feature feature){
        String [] relevantFields = new String[] {"type", "class", "name"};
        for (String field : relevantFields){
            if (feature.properties().has(field) ) {
                String locationType = feature.properties().get(field).toString();
                System.out.println("Finally got there " + locationType.trim());
                if(INAPPROPRIATE_LOCATIONS.contains(locationType.substring(1, locationType.length()-1).toLowerCase())){
                    return false;
                }
            }
        }// An inappropriate characteristics may be in different property fields

        return true;
    }

    private List<Feature> getFeatureAtLocation(Location location){
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        LatLng point = new LatLng(lat,lon);
        //CountDownLatch moved =
        moveCameraWithoutAnimation(lat, lon, ZOOM_FOR_FEATURES);
//        try {
//            moved.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
//
//            assert(moved.getCount() == 0);
//        }catch (InterruptedException e){
//            e.printStackTrace();
//            return null;
//        }
//        System.out.println("Moved is now " + moved.getCount());
        // Convert LatLng coordinates to screen pixel and only query the rendered features.
        //This is because the query feature API function only accepts pixel as an arg
        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);


        System.out.println(mapboxMap.getProjection().fromScreenLocation(pixel));
        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel);
        System.out.println(features.size());
        return features;
    }
}
