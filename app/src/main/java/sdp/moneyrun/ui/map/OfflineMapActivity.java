package sdp.moneyrun.ui.map;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;

import sdp.moneyrun.R;
import sdp.moneyrun.map.BaseMap;

public class OfflineMapActivity extends BaseMap {
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private final int MIN_ZOOM = 9;

    private boolean hasFoundMap = false;
    private Button exitButton;
    private OfflineManager offlineManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        createMap(savedInstanceState, R.id.mapView_offline, R.layout.activity_offline_map);
        mapView.getMapAsync(this::onMapReady);
        exitButton = findViewById(R.id.close_map_offline);
        addExitButton();
    }

    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            offlineManager = OfflineManager.getInstance(OfflineMapActivity.this);
            getDownloadedRegion();
        });
    }

    private void addExitButton() {
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean getHasFoundMap(){return hasFoundMap;}


    // We only allow one downloaded map
    private void getDownloadedRegion(){
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {

            @Override
            public void onList(OfflineRegion[] offlineRegions) {
                if (offlineRegions == null || offlineRegions.length == 0) {
                    hasFoundMap = false;
                    Toast.makeText(getApplicationContext(), getString(R.string.no_offline_regions), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), getString(R.string.found_offline_regions), Toast.LENGTH_SHORT).show();

                hasFoundMap = true;
                // Create new camera position
                int regionSelected = 0;
                LatLngBounds bounds = (offlineRegions[regionSelected].getDefinition()).getBounds();
                double regionZoom = (offlineRegions[regionSelected].getDefinition()).getMinZoom();

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(bounds.getCenter())
                        .zoom(regionZoom)
                        .build();
                Toast.makeText(getApplicationContext(), bounds.toString(), Toast.LENGTH_SHORT).show();

                // Move camera to new position
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            @Override
            public void onError(String error) {
                Toast.makeText(OfflineMapActivity.this, getString(R.string.no_offline_regions), Toast.LENGTH_SHORT).show();
            }
        });
    }


}