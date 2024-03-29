package sdp.moneyrun.ui.map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import org.json.JSONObject;

import java.util.Objects;

import sdp.moneyrun.R;
import sdp.moneyrun.map.LocationCheckObjectivesCallback;
import sdp.moneyrun.map.TrackedMap;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

@SuppressWarnings("FieldCanBeLocal")
public class OfflineMapDownloaderActivity extends TrackedMap {

    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private final float LAT_OFFSET = 0.1f;
    private final float LONG_OFFSET = 0.1f;
    private final int MAX_ZOOM = 15;
    private final int MIN_ZOOM = 9;
    private boolean isEndNotified = false;
    private boolean hasStartedDownload = false;
    private ProgressBar progressBar;
    private MapView mapView;
    private OfflineManager offlineManager;
    private Button exitButton;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        createMap(savedInstanceState, R.id.mapView_downloader, R.layout.activity_offline_map_downloader);
        setContentView(R.layout.activity_offline_map_downloader);

        user = (User) getIntent().getSerializableExtra("user");
        mapView = findViewById(R.id.mapView_downloader);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);

        exitButton = findViewById(R.id.downloader_exit);
        addExitButton();

    }

    private void addExitButton() {
        exitButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(OfflineMapDownloaderActivity.this, MenuActivity.class);
            mainIntent.putExtra("user", user);
            startActivity(mainIntent);
            finish();
        });
    }


    /**
     * @param mapboxMap the map where everything will be done
     *                  this overrides the OnMapReadyCallback in the implemented interface
     *                  We set up the symbol manager here, it will allow us to add markers and other visual stuff on the map
     *                  Then we setup the location tracking
     */
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        callback = new LocationCheckObjectivesCallback(this);

        mapboxMap.setStyle(Style.MAPBOX_STREETS, this::enableLocationComponent);
        this.mapboxMap = mapboxMap;
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (offlineManager != null) {
            deleteOlderMaps();
        }
    }

    public boolean getIsEndNotified() {
        return isEndNotified;
    }

    public boolean getHasStartedDownload() {
        return hasStartedDownload;
    }

    // Progress bar methods
    private void startProgress() {
        // Start and show the progress bar
        isEndNotified = false;
        hasStartedDownload = true;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
        if (isEndNotified) {
            return;
        }
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
        Context context = getApplicationContext();

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        deleteOlderMaps();
    }

    /**
     * @param location the center of the downloaded map
     *                 The map will be downloaded when the location provider updates the location so that we download the map where the user is.
     */
    @Override
    public void checkObjectives(@NonNull Location location) {

        if (isEndNotified || hasStartedDownload)
            return;

        offlineManager = OfflineManager.getInstance(OfflineMapDownloaderActivity.this);

        // Create a bounding box for the offline region
        LatLng northeast = new LatLng(location.getLatitude() + LAT_OFFSET, location.getLongitude() + LONG_OFFSET);
        LatLng southwest = new LatLng(location.getLatitude() - LAT_OFFSET, location.getLongitude() - LONG_OFFSET);

        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(northeast) // Northeast
                .include(southwest) // Southwest
                .build();

        // Define the offline region
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                mapboxMap.getStyle().getUri(),
                latLngBounds,
                MIN_ZOOM,
                MAX_ZOOM,
                OfflineMapDownloaderActivity.this.getResources().getDisplayMetrics().density);

        // Set the metadata
        byte[] metadata = setMetaData();

        // Create the region asynchronously
        if (metadata != null)
            createOfflineRegion(metadata, definition);

    }

    public void createOfflineRegion(@NonNull byte[] metadata, @NonNull OfflineTilePyramidRegionDefinition definition) {
        offlineManager.createOfflineRegion(
                definition,
                metadata,
                new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(@NonNull OfflineRegion offlineRegion) {
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                        progressBar = findViewById(R.id.progress_bar_map_downloader);
                        startProgress();
                        setOfflineRegionObserver(offlineRegion);


                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(OfflineMapDownloaderActivity.this.getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setOfflineRegionObserver(@NonNull OfflineRegion offlineRegion) {
        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
            @Override
            public void onStatusChanged(@NonNull OfflineRegionStatus status) {
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
            public void onError(@NonNull OfflineRegionError error) {
                Toast.makeText(OfflineMapDownloaderActivity.this.getApplicationContext(), error.getReason(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit) {
                Toast.makeText(OfflineMapDownloaderActivity.this.getApplicationContext(), getString(R.string.tile_limit_exceeded), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Nullable
    public byte[] setMetaData() {
        byte[] metadata;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, getString(R.string.offline_map_name));
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {
            metadata = null;
        }

        return metadata;
    }


    /**
     * Deletes all offline regions except the last from the device
     */
    private void deleteOlderMaps() {
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(@NonNull OfflineRegion[] offlineRegions) {

                if (offlineRegions.length > 1) {
                    // delete the last item in the offlineRegions list which will be yosemite offline map
                    for (int i = 0; i < offlineRegions.length - 1; ++i) {
                        Toast.makeText(OfflineMapDownloaderActivity.this.getApplicationContext(), offlineRegions[i].getDefinition().getBounds().toString(), Toast.LENGTH_SHORT).show();

                        offlineRegions[i].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
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
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

}

