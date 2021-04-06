package sdp.moneyrun.map;

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

import sdp.moneyrun.R;

public class MapActivity extends TrackedMap implements OnMapReadyCallback {
    protected static final int GAME_TIME = 100;
    protected static int chronometerCounter =0;
    private SymbolManager symbolManager;

    private Chronometer chronometer;
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

        callback = new LocationChangeListeningActivityLocationCallback(this);
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
    public SymbolManager getSymbolManager(){return symbolManager;}
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




}