package sdp.moneyrun.ui.map;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;

import org.json.JSONObject;

import sdp.moneyrun.R;
import sdp.moneyrun.map.LocationCheckObjectivesCallback;
import sdp.moneyrun.map.TrackedMap;

public class OfflineMapDownloaderActivity extends TrackedMap {

    private boolean isEndNotified = false;
    private ProgressBar progressBar;
    private MapView mapView;
    private OfflineManager offlineManager;
    private float LAT_OFFSET = 0.1f;
    private float LONG_OFFSET = 0.1f;
    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_offline_map_downloader);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);
    }
    /**
     * @param mapboxMap the map where everything will be done
     *                  this overried the OnMapReadyCallback in the implemented interface
     *                  We set up the symbol manager here, it will allow us to add markers and other visual stuff on the map
     *                  Then we setup the location tracking
     */
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        callback = new LocationCheckObjectivesCallback(this);

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            GeoJsonOptions geoJsonOptions = new GeoJsonOptions().withTolerance(0.4f);
            enableLocationComponent(style);

        });
        this.mapboxMap = mapboxMap;
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (offlineManager != null) {
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                @Override
                public void onList(OfflineRegion[] offlineRegions) {
                    if (offlineRegions.length > 1) {
                        // delete the last item in the offlineRegions list which will be yosemite offline map
                        offlineRegions[(offlineRegions.length - 1)].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                            @Override
                            public void onDelete() {
                                Toast.makeText(
                                        OfflineMapDownloaderActivity.this,
                                        getString(R.string.offline_map_deleted),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                            @Override
                            public void onError(String error) {
                            //    Timber.e("On delete error: %s", error);
                            }
                        });
                    }
                }
                @Override
                public void onError(String error) {
                 //   Timber.e("onListError: %s", error);
                }
            });
        }
    }



    // Progress bar methods
    private void startProgress() {
// Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
// Don't notify more than once
        if (isEndNotified) {
            return;
        }
// Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
// Show a toast
        Toast.makeText(OfflineMapDownloaderActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void checkObjectives(Location location) {

    if(! isEndNotified){
        offlineManager = OfflineManager.getInstance(OfflineMapDownloaderActivity.this);

// Create a bounding box for the offline region
        LatLng northeast = new LatLng(location.getLatitude()-LAT_OFFSET, location.getLatitude()-LONG_OFFSET);
        LatLng southwest = new LatLng(location.getLatitude()+LAT_OFFSET, location.getLatitude()+LONG_OFFSET);
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(northeast) // Northeast
                .include(southwest) // Southwest
                .build();

// Define the offline region
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                mapboxMap.getStyle().getUri(),
                latLngBounds,
                10,
                20,
                OfflineMapDownloaderActivity.this.getResources().getDisplayMetrics().density);

// Set the metadata
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, "offline map");
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {
            metadata = null;
        }

// Create the region asynchronously
        if (metadata != null) {
            offlineManager.createOfflineRegion(
                    definition,
                    metadata,
                    new OfflineManager.CreateOfflineRegionCallback() {
                        @Override
                        public void onCreate(OfflineRegion offlineRegion) {
                            offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                            progressBar = findViewById(R.id.progress_bar);
                            startProgress();

                            offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                                @Override
                                public void onStatusChanged(OfflineRegionStatus status) {

                                    double percentage = status.getRequiredResourceCount() >= 0
                                            ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                            0.0;

                                    if (status.isComplete()) {
                                        endProgress(getString(R.string.offline_end_progress_success));
                                    } else if (status.isRequiredResourceCountPrecise()) {
                                        setPercentage((int) Math.round(percentage));
                                    }
                                }

                                @Override
                                public void onError(OfflineRegionError error) {
                                    //     Timber.e("onError reason: %s", error.getReason());
                                    //     Timber.e("onError message: %s", error.getMessage());
                                }

                                @Override
                                public void mapboxTileCountLimitExceeded(long limit) {
                                    //       Timber.e("Mapbox tile count limit exceeded: %s", limit);
                                }
                            });
                        }
                        @Override
                        public void onError(String error) {
                            //  Timber.e("Error: %s", error);
                        }
                    });

        }
    }
    }
}

