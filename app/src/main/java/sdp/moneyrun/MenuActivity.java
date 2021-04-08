package sdp.moneyrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.auth.FirebaseAuth;

import java.io.InputStream;


import sdp.moneyrun.map.MapActivity;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private Button profileButton;
    private Button leaderboardButton;
    private Button joinGame;
    private Button mapButton;
    private String[] result;
    private String[] playerInfo;
    private Player player;
    private int playerId;
    private RiddlesDatabase db;
    protected DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setNavigationViewListener();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mapButton = findViewById(R.id.map_button);
        addJoinGameButtonFunctionality();
        addMapButtonFunctionality();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RiddlesDatabase.reset();
    }


    public void addMapButtonFunctionality(){

        mapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent leaderboardIntent = new Intent(MenuActivity.this, MapActivity.class);
                startActivity(leaderboardIntent);
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.profile_button: {
                onButtonSwitchToUserProfileActivity(item.getActionView());
                break;
            }

            case R.id.leaderboard_button: {
                Intent leaderboardIntent = new Intent(MenuActivity.this, LeaderboardActivity.class);
                startActivity(leaderboardIntent);
                break;
            }

            case R.id.log_out_button: {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void addJoinGameButtonFunctionality(){

        Button joinGame = findViewById(R.id.join_game);

        /**
         * Checks for clicks on the join game button and creates a popup of available games if clicked
         */
        joinGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onButtonShowJoinGamePopupWindowClick(v, true, R.layout.join_game_popup);
            }
        });
    }


    private void setPutExtraArguments(Intent intent){
        intent.putExtra("playerId",playerId);
        intent.putExtra("playerId"+playerId,playerInfo);
    }


    public void onButtonSwitchToUserProfileActivity(View view) {

        Intent playerProfileIntent = new Intent(MenuActivity.this, PlayerProfileActivity.class);
        setPutExtraArguments(playerProfileIntent);
        startActivity(playerProfileIntent);

    }

    public void onButtonShowJoinGamePopupWindowClick(View view, Boolean focusable, int layoutId) {

        onButtonShowPopupWindowClick(view, focusable, layoutId);

    }

    /**
     *
     * @param view Current view before click
     * @param focusable Whether it can be dismissed by clicking outside the popup window
     * @param layoutId Id of the popup layout that will be used
     */
    public PopupWindow onButtonShowPopupWindowClick(View view, Boolean focusable, int layoutId) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layoutId, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window at wanted location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupWindow;
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
