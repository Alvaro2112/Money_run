package sdp.moneyrun;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback {
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private static final float ZOOM = 4;
    private MapView mapView;
    private SymbolManager symbolManager;
    private MapboxMap mapboxMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

            mapView.getMapAsync(this);

    }

    /**
     * @param mapboxMap the map where everything will be done
    *     this overried the OnMapReadyCallback in the implemented interface
   *      We set up the symbol manager here that will allow us to add markers
     */
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {


        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(2));

            GeoJsonOptions geoJsonOptions = new GeoJsonOptions().withTolerance(0.4f);
            symbolManager =  new SymbolManager(mapView, mapboxMap, style, null, geoJsonOptions);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
        });

        this.mapboxMap = mapboxMap;
    }


    public void addMarker(float latitude,float longitude){
        LatLng latLng = new LatLng(latitude,longitude);
        symbolManager.create(new SymbolOptions().withLatLng(latLng));
    }

    public SymbolManager getSymbolManager(){
        return symbolManager;
    }

    public MapboxMap getMapboxMap(){
        return mapboxMap;
    }

    public void moveCameraTo(float latitude, float longitude){
        LatLng latLng = new LatLng(latitude,longitude);
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(10)
                .tilt(20)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}