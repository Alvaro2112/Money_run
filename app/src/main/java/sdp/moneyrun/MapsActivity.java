package sdp.moneyrun;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int DEFAULT_ZOOM = 15;
    private CameraPosition cameraPosition;
    private static final String TAG = MapsActivity.class.getSimpleName();

    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean hasPermission;
    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }


    public MarkerOptions addMarker(LatLng pos, String title){
        if(pos == null){
            throw new NullPointerException("pos is null");
        }
        if(title == null){
            throw new NullPointerException("title is null");
        }
        MarkerOptions marker = new MarkerOptions().position(pos).title(title);
        this.mMap.addMarker(marker);

        return marker;

    }

    public LatLng latLngFromLocation(Location loc){
        if(loc == null){
            throw new NullPointerException("loc is null"):
        }
        return new LatLng(loc.getLatitude(),loc.getLongitude());
    }

    private void setDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (hasPermission) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {

                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                moveCameraTo(latLngFromLocation(lastKnownLocation));
                            }
                        } else {
                            Log.d(TAG, "Couldn't get the location.");
                            Log.e( TAG,"Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void moveCameraTo(LatLng pos){
        if(pos == null){
            throw new NullPointerException("pos is null");
        }
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,DEFAULT_ZOOM));
    }

    public  GoogleMap getMap(){
        return this.mMap;
    }
    public Location getLastKnownLocation(){
        return this.lastKnownLocation;
    }

    private void validatePermission(){
        //TODO
        // change this so that it uses the permission in the login
        hasPermission = true;
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * @return
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        try {
            if (hasPermission) {
                this.mMap.setMyLocationEnabled(true);
                this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                this.mMap.setMyLocationEnabled(false);
                this.mMap.getUiSettings().setMyLocationButtonEnabled(false);

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

        setDeviceLocation();
    }

}