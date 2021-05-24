package sdp.moneyrun.ui.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.database.RiddlesDatabase;
import sdp.moneyrun.location.LocationRepresentation;
import sdp.moneyrun.menu.JoinGameImplementation;
import sdp.moneyrun.menu.NewGameImplementation;
import sdp.moneyrun.ui.authentication.LoginActivity;
import sdp.moneyrun.ui.map.OfflineMapActivity;
import sdp.moneyrun.ui.map.OfflineMapDownloaderActivity;
import sdp.moneyrun.ui.player.UserProfileActivity;
import sdp.moneyrun.user.User;
import sdp.moneyrun.weather.AddressGeocoder;
import sdp.moneyrun.weather.OpenWeatherMap;
import sdp.moneyrun.weather.WeatherForecast;
import sdp.moneyrun.weather.WeatherReport;


@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal"})
public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //In meters
    public static final float DISTANCE_CHANGE_BEFORE_UPDATE = (float) 100.0;
    private static final long MINIMUM_TIME_BEFORE_UPDATE = 10000;
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    map -> {
                    });


    protected DrawerLayout mDrawerLayout;
    private User user;

    private OpenWeatherMap openWeatherMap;
    private AddressGeocoder addressGeocoder;
    private WeatherForecast currentForecast;
    private LocationRepresentation currentLocation;
    DatabaseReference databaseReference;
    FusedLocationProviderClient fusedLocationClient;

    private final String TAG = MenuActivity.class.getSimpleName();


    @NonNull
    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            loadWeather(location);
            if(currentForecast != null)
                setWeatherFieldsToday(currentForecast.getWeatherReport(WeatherForecast.Day.TODAY));

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        setNavigationViewListener();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // setup database instance
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Get player location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        runFunctionalities();
        addDownloadButton();
        addOfflineMapButton();

        DatabaseProxy.addOfflineListener(MenuActivity.this, TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseProxy.removeOfflineListener();
    }
    @Override
    protected void onResume() {
        super.onResume();
        DatabaseProxy.addOfflineListener(MenuActivity.this, TAG);
    }

    protected void onStop(){
        super.onStop();
        DatabaseProxy.removeOfflineListener();
    }

    public void addDownloadButton() {
        Button download = findViewById(R.id.download_map);
        download.setOnClickListener(v -> onButtonSwitchToActivity(OfflineMapDownloaderActivity.class, false));
    }

    public void addOfflineMapButton() {
        Button offline_map = findViewById(R.id.offline_map_menu);
        offline_map.setOnClickListener(v -> onButtonSwitchToActivity(OfflineMapActivity.class, false));
    }

    public void runFunctionalities() {
        //Setting the current player object
        user = (User) getIntent().getSerializableExtra("user");
        if (user == null) {
            throw new IllegalStateException("the Intent that launched MenuActivity has null \"user\" value");
        }
        boolean guestPlayer = getIntent().getBooleanExtra("guestPlayer", false);
        setGuestPlayerFields(guestPlayer);

        JoinGameImplementation joinGameImplementation = new JoinGameImplementation(this,
                databaseReference,
                user,
                requestPermissionsLauncher,
                fusedLocationClient,
                true,
                R.layout.join_game_popup);

        NewGameImplementation newGameImplementation = new NewGameImplementation(this,
                databaseReference,
                user,
                requestPermissionsLauncher,
                fusedLocationClient);


        runWeather();

        Button joinGame = findViewById(R.id.join_game);
        joinGame.setOnClickListener(joinGameImplementation::onClickShowJoinGamePopupWindow);

        Button newGame = findViewById(R.id.new_game);
        newGame.setOnClickListener(newGameImplementation::onClickShowNewGamePopupWindow);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RiddlesDatabase.reset();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        MediaPlayer.create(this, R.raw.button_press).start();
        switch (item.getItemId()) {

            case R.id.profile_button: {
                onButtonSwitchToActivity(UserProfileActivity.class, false);
                break;
            }

            case R.id.friend_list_button: {
                onButtonSwitchToActivity(FriendListActivity.class, false);
                break;
            }
            
            case R.id.main_leaderboard_button: {
                onButtonSwitchToActivity(MainLeaderboardActivity.class, false);
                break;
            }

            case R.id.log_out_button: {
                FirebaseAuth.getInstance().signOut();
                onButtonSwitchToActivity(LoginActivity.class, true);
                break;
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setPutExtraArguments(@NonNull Intent intent) {
        intent.putExtra("user", user);
    }

    public void onButtonSwitchToActivity(Class activityClass, boolean shouldFinish) {
        Intent switchActivity = new Intent(MenuActivity.this, activityClass);
        setPutExtraArguments(switchActivity);
        startActivity(switchActivity);
        if (shouldFinish) {
            finish();
        }
    }

    public void setGuestPlayerFields(boolean guest) {
        if (guest) {
            Button joinGame = findViewById(R.id.join_game);
            joinGame.setEnabled(false);
        }
    }

    private void runWeather() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        currentLocation = new LocationRepresentation(0, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BEFORE_UPDATE, DISTANCE_CHANGE_BEFORE_UPDATE, locationListenerGPS);
        } catch (Exception e) {
            System.out.println("Your device does not have network capabilities");
        }
        openWeatherMap = OpenWeatherMap.build();
        addressGeocoder = AddressGeocoder.fromContext(this);
    }


    public void loadWeather(@NonNull android.location.Location location) {
        try {
            LocationRepresentation loc;
            loc = new LocationRepresentation(location.getLatitude(), location.getLongitude());
            this.currentLocation = loc;
            this.currentForecast = openWeatherMap.getForecast(loc);

        } catch (IOException e) {
            Log.e("WeatherActivity", "Error when retrieving forecast.", e);
        }
    }

    public WeatherForecast getCurrentForecast() {
        return currentForecast;
    }

    public LocationRepresentation getCurrentLocation() {
        return currentLocation;
    }

    public void setWeatherFieldsToday(@NonNull WeatherReport report) {
        TextView weatherTypeText = findViewById(R.id.weather_type);
        TextView weatherTempText = findViewById(R.id.weather_temp_average);
        ImageView weatherIconView = findViewById(R.id.weather_icon);

        String url = "https://openweathermap.org/img/wn/" + report.getWeatherIcon() + "@4x.png";
        Picasso obj = Picasso.get();
        obj.setLoggingEnabled(true);
        obj.load(url).fit().into(weatherIconView);
        weatherIconView.setContentDescription(report.getWeatherType());
        weatherTempText.setText(String.format("%s C", report.getAverageTemperature()));
        weatherTypeText.setText(report.getWeatherType());
    }

    @Override
    public void onBackPressed() {
        // disable the back button from menu since the user should not be able to log in again once logged in properly
        return;
    }
}
