package sdp.moneyrun.map;

import android.location.Location;
import android.os.Bundle;
import android.widget.Chronometer;

import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.Coin;
import sdp.moneyrun.R;

/*
this map implements all the functionality we will need.
 */
public class MapActivity extends TrackedMap implements OnMapReadyCallback {
    private static final int GAME_TIME = 100;
    private static int chronometerCounter =0;
    private Chronometer chronometer;
    private List<Coin> remainingCoins = new ArrayList<>();
    private List<Coin> caughtCoins = new ArrayList<>();
    private static final double THRESHOLD_DISTANCE = 1.;
    private Location currentLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        createMap(savedInstanceState,R.id.mapView,R.layout.activity_map);
        mapView.getMapAsync(this);
        initChronometer();
    }

    /**
     * @param mapboxMap the map where everything will be done
     *     this overried the OnMapReadyCallback in the implemented interface
     *      We set up the symbol manager here, it will allow us to add markers and other visual stuff on the map
     *                    *       Then we setup the location tracking
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
    public List<Coin> getCaughtCoins(){return caughtCoins;}
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
                chronometer.setFormat("REMAINING TIME"+String.valueOf(GAME_TIME - chronometerCounter));
            }
        });
    }


    public void addMarker(float latitude,float longitude){
        LatLng latLng = new LatLng(latitude,longitude);
        symbolManager.create(new SymbolOptions().withLatLng(latLng));
    }

    public void moveCameraTo(float latitude, float longitude){
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    public void  checkObjectives(Location location){
        currentLocation = location;
        int coinIdx = isNearCoin(location);
        if(coinIdx >= 0){
            Coin caught = remainingCoins.remove(coinIdx);
            caughtCoins.add(caught);
            // TODO :
            //  call the riddle
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



}