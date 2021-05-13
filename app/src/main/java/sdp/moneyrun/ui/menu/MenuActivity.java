package sdp.moneyrun.ui.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;

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

import java.util.concurrent.Semaphore;

import sdp.moneyrun.R;
import sdp.moneyrun.database.RiddlesDatabase;
import sdp.moneyrun.menu.JoinGameImplementation;
import sdp.moneyrun.menu.NewGameImplementation;
import sdp.moneyrun.permissions.PermissionsRequester;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.authentication.LoginActivity;
import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.map.OfflineMapActivity;
import sdp.moneyrun.ui.map.OfflineMapDownloaderActivity;
import sdp.moneyrun.ui.player.UserProfileActivity;
import sdp.moneyrun.ui.authentication.LoginActivity;
import sdp.moneyrun.user.User;


public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
    });

    private RiddlesDatabase db;
    private Button mapButton;
    protected DrawerLayout mDrawerLayout;
    private final Semaphore available = new Semaphore(1, true);
    private int numberOfAsyncTasks;
    private int tasksFinished;
    private Player currentPlayer;
    private int tasksFInished;
    private User user;

    DatabaseReference databaseReference;
    FusedLocationProviderClient fusedLocationClient;

    private final String coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        setNavigationViewListener();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mapButton = findViewById(R.id.map_button);
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

    public void addDownloadButton(){
        Button download = findViewById(R.id.download_map);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonSwitchToActivity(OfflineMapDownloaderActivity.class,false);
            }});
    }

    public void addOfflineMapButton(){
        Button offline_map = findViewById(R.id.offline_map_menu);
        offline_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonSwitchToActivity(OfflineMapActivity.class,false);
            }});
    }


    public void runFunctionalities(){
        //Setting the current player object
        user = (User) getIntent().getSerializableExtra("user");
        if(user == null){
            throw new IllegalStateException("the Intent that launched MenuActivity has null \"user\" value");
        }
        boolean guestPlayer = getIntent().getBooleanExtra("guestPlayer",false);
        setGuestPlayerFields(guestPlayer);

        boolean isLocationMocked = getIntent().getBooleanExtra("isLocationMocked", false);

        JoinGameImplementation joinGameImplementation = new JoinGameImplementation(this,
                databaseReference,
                user,
                requestPermissionsLauncher,
                fusedLocationClient,
                true,
                R.layout.join_game_popup,
                isLocationMocked);

        NewGameImplementation newGameImplementation = new NewGameImplementation(this,
                databaseReference,
                user,
                requestPermissionsLauncher,
                fusedLocationClient);

        // Functionalities
        mapButton = findViewById(R.id.map_button);
        addMapButtonFunctionality();

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
    
    public void StartMapActivity(){
        Intent mainIntent = new Intent(MenuActivity.this, MapActivity.class);
        if(user != null){
            mainIntent.putExtra("playerId", user.getUserId());
        }
        MenuActivity.this.startActivity(mainIntent);
        MenuActivity.this.finish();
        available.release();
    }

    public void addMapButtonFunctionality(){

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Example of how the Async tasks should be implemented
                numberOfAsyncTasks = 2; //number of async tasks
                tasksFinished = 0;

                setContentView(R.layout.splash_screen);

                Runnable x = new Runnable() {
                    public void run() {
                        synchronized (this) {
                            try {
                                wait(5000);
                            } catch (InterruptedException e) {
                            }
                        }
                        try {
                            available.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(tasksFinished == numberOfAsyncTasks - 1){
                            StartMapActivity();
                        } else {
                            tasksFinished += 1;
                        }

                        available.release();
                    }
                };

                Runnable y = new Runnable() {
                    public void run() {
                        synchronized (this) {
                            try {
                                wait(2000);
                            } catch (InterruptedException e) {
                            }
                        }
                        try {
                            available.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(tasksFinished == numberOfAsyncTasks - 1){
                            StartMapActivity();
                        } else {
                            tasksFinished += 1;
                        }

                        available.release();
                    }
                };

                Thread thread = new Thread(x);
                Thread thread1 = new Thread(y);

                thread.start();
                thread1.start();
            }

        });
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

    private void setPutExtraArguments(Intent intent){
        intent.putExtra("user", user);
    }

    public void onButtonSwitchToActivity(Class activityClass, boolean shouldFinish){
        Intent switchActivity = new Intent(MenuActivity.this, activityClass);
        setPutExtraArguments(switchActivity);
        startActivity(switchActivity);
        if(shouldFinish){
            finish();
        }
    }

    public void setGuestPlayerFields(boolean guest){
        if(guest){
            Button joinGame = findViewById(R.id.join_game);
            joinGame.setEnabled(false);
        }
    }


    //TODO: fix it somehow: task is never completed and thus cannot get player from database
    //To come back too later
//    public void setPlayerObject(){
//        playerId = getIntent().getIntExtra("playerId",0);
//        playerInfo = getIntent().getStringArrayExtra("playerId"+playerId);
//        DatabaseProxy db = new DatabaseProxy();
//        if(db != null) {
//            Task<DataSnapshot> t = db.getPlayerTask(playerId);
////            player = db.getPlayerFromTask(t);
//            t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if(task.isSuccessful()){
//                        player = db.getPlayerFromTask(t);
//                    }
//                }
//            });
////           while(!t.isComplete()){
////               System.out.println("Task is not ready yet");
////           }
//            System.out.println("PLayer should be set by now");
//        }
//        //TODO: put player in the database with playerId as primary key
//    }
}
