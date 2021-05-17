package sdp.moneyrun.ui.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Semaphore;

import sdp.moneyrun.R;
import sdp.moneyrun.database.RiddlesDatabase;
import sdp.moneyrun.map.LocationRepresentation;
import sdp.moneyrun.menu.JoinGameImplementation;
import sdp.moneyrun.menu.NewGameImplementation;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.authentication.LoginActivity;
import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.map.OfflineMapActivity;
import sdp.moneyrun.ui.map.OfflineMapDownloaderActivity;
import sdp.moneyrun.ui.player.UserProfileActivity;
import sdp.moneyrun.ui.authentication.LoginActivity;
import sdp.moneyrun.user.User;
import sdp.moneyrun.weather.Address;
import sdp.moneyrun.weather.AddressGeocoder;
import sdp.moneyrun.weather.OpenWeatherMap;
import sdp.moneyrun.weather.WeatherForecast;
import sdp.moneyrun.weather.WeatherReport;


public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
    });

    public static final float DISTANCE_CHANGE_BEFORE_UPDATE = (float) 0.00001;
    private static final long MINIMUM_TIME_BEFORE_UPDATE = 10000;

    private RiddlesDatabase db;
    protected DrawerLayout mDrawerLayout;
    private final Semaphore available = new Semaphore(1, true);
    private int numberOfAsyncTasks;
    private int tasksFinished;
    private Player currentPlayer;
    private int tasksFInished;
    private User user;

    private OpenWeatherMap openWeatherMap;
    private AddressGeocoder addressGeocoder;
    private WeatherForecast currentForecast;
    private LocationRepresentation currentLocation;




    DatabaseReference databaseReference;
    FusedLocationProviderClient fusedLocationClient;


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
        String toDeleteId = getIntent().getStringExtra("deleteGame");

        // Get player location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        runFunctionalities();
        addDownloadButton();
        addOfflineMapButton();
    }

    public void addDownloadButton() {
        Button download = findViewById(R.id.download_map);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonSwitchToActivity(OfflineMapDownloaderActivity.class, false);
            }
        });
    }

    public void addOfflineMapButton() {
        Button offline_map = findViewById(R.id.offline_map_menu);
        offline_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonSwitchToActivity(OfflineMapActivity.class, false);
            }
        });
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

        switch (item.getItemId()) {

            case R.id.profile_button: {
                onButtonSwitchToActivity(UserProfileActivity.class, false);
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

    private void setPutExtraArguments(Intent intent) {
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

    LocationListener locationListenerGPS = location -> {
        loadWeather(location);
        setWeatherFieldsToday(currentForecast.getWeatherReport(WeatherForecast.Day.TODAY));
    };

    private void runWeather() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        currentLocation = new LocationRepresentation(0,0);

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

    public void loadWeather(android.location.Location location) {
        try {
            LocationRepresentation loc;
            loc = new LocationRepresentation(location.getLatitude(), location.getLongitude());
            this.currentLocation = loc;
            this.currentForecast = openWeatherMap.getForecast(loc);

            android.location.Address addr = addressGeocoder.getAddress(loc);
            Address address;
            if (addr != null) {
                address = addressGeocoder.convertToAddress(addr);
            }

        } catch (IOException e) {
            Log.e("WeatherActivity", "Error when retrieving forecast.", e);
        }
    }

    public WeatherForecast getCurrentForecast(){
        return currentForecast;
    }
    public LocationRepresentation getCurrentLocation(){
        return currentLocation;
    }

    private void setWeatherFieldsToday(WeatherReport report){
        String weatherIconURL = "http://openweathermap.org/img/wn/"+report.getWeatherIcon()+"@2x.png";
        Log.d(MenuActivity.class.getSimpleName(), "THE ICON IS : "+report.getWeatherIcon());
        TextView weatherTypeText =findViewById(R.id.weather_type);
        TextView weatherTempText =findViewById(R.id.weather_temp_average);
        weatherTempText.setText(Double.toString(report.getAverageTemperature())+" C");
        weatherTypeText.setText(report.getWeatherType());
    }
}
