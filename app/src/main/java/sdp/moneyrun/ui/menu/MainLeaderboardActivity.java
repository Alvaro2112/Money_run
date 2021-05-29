package sdp.moneyrun.ui.menu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.menu.MainLeaderboardListAdapter;
import sdp.moneyrun.user.User;

@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal"})
public class MainLeaderboardActivity extends AppCompatActivity {

    public static final int NUM_PLAYERS_LEADERBOARD = 10;

    private final String TAG = MainLeaderboardActivity.class.getSimpleName();
    @NonNull
    private ArrayList<User> userList = new ArrayList<>();
    private MainLeaderboardListAdapter ldbAdapter;
    private User user;
    private Button backToMenu;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main_leaderboard);

        user = (User) getIntent().getSerializableExtra("user");
        addAdapter();
        addUsersToLeaderboard(NUM_PLAYERS_LEADERBOARD);
        DatabaseProxy.addOfflineListener(this, TAG);
        backToMenu = findViewById(R.id.back_from_main_leaderboard_button);

        backToMenu.setOnClickListener(v -> {
            Intent menuIntent = new Intent(MainLeaderboardActivity.this, MenuActivity.class);
            menuIntent.putExtra("user", user);
            startActivity(menuIntent);
            finish();
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseProxy.removeOfflineListener();
    }

    /**
     * @return the leaderboard adapter instance
     */
    public MainLeaderboardListAdapter getLdbAdapter() {
        return ldbAdapter;
    }

    /**
     * @return the user list
     */
    @NonNull
    public ArrayList<User> getUserList() {
        return userList;
    }

    public int getMaxUserNumber() {
        return NUM_PLAYERS_LEADERBOARD;
    }

    private void addAdapter() {
        // The adapter lets us add item to a ListView easily.
        ldbAdapter = new MainLeaderboardListAdapter(this, userList, user);
        Helpers.addAdapter(ldbAdapter, this, R.id.ldblistView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addUsersToLeaderboard(int n) {
        UserDatabaseProxy dp = new UserDatabaseProxy();
        dp.getLeaderboardUsers(n).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                DataSnapshot result = task.getResult();
                if (result == null) {
                    return;
                }
                HashSet<User> userToShow = new HashSet<>();
                for (DataSnapshot dataSnapshot : result.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getUserId() != null) {
                            userToShow.add(user);
                        }
                    }
                }
                userList = new ArrayList<>(userToShow);
                addUserList(userList);
                // Always add yourself at the end
                if(!userList.contains(user)){
                    addUser(user);
                }
            }
        });
    }

    /**
     * @param userList: users to be added to the leaderboard
     *                  Adds users to leaderboard
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addUserList(@Nullable ArrayList<User> userList) {
        Helpers.addObjectListToAdapter(userList, ldbAdapter);

    }

    /**
     * @param user: user to be added to the leaderboard
     *              Adds user to leaderboard
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addUser(@Nullable User user) {
        // can't just add a user directly to an adapter, we need to put it in a list first.
        if (user == null) {
            throw new IllegalArgumentException("user should not be null.");
        }
        ArrayList<User> to_add = new ArrayList<>(Collections.singletonList(user));
        addUserList(to_add);
    }

    @Override
    public void onBackPressed() {
    }
}
