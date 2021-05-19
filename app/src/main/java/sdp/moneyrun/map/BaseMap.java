package sdp.moneyrun.map;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;

import org.jetbrains.annotations.NotNull;


/*
Base class for a mapboxmap, implements base mapboxmap functions
 */
public abstract class BaseMap extends AppCompatActivity {

    protected MapView mapView;
    protected MapboxMap mapboxMap;
    @Nullable
    protected SymbolManager symbolManager;

    protected void createMap(Bundle savedInstanceState, int mapViewID, int contentViewID) {
        setContentView(contentViewID);
        mapView = findViewById(mapViewID);
        mapView.onCreate(savedInstanceState);
    }

    public MapboxMap getMapboxMap() {
        return mapboxMap;
    }

    @Nullable
    public SymbolManager getSymbolManager() {
        return symbolManager;
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
    protected void onSaveInstanceState(@NotNull Bundle outState) {
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
