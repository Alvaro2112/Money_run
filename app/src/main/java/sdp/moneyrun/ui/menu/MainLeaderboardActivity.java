package sdp.moneyrun.ui.menu;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.menu.MainLeaderboardListAdapter;
import sdp.moneyrun.user.User;

@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal"})
public class MainLeaderboardActivity extends AppCompatActivity {

    private final int NUM_PLAYERS_LEADERBOARD = 10;

    @NonNull
    private ArrayList<User> userList = new ArrayList<>();
    private MainLeaderboardListAdapter ldbAdapter;
    private User user;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_leaderboard);

        user = (User) getIntent().getSerializableExtra("user");

        addAdapter();
        addUsersToLeaderboard(NUM_PLAYERS_LEADERBOARD);
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
        ListView ldbView = findViewById(R.id.ldblistView);
        ldbView.setAdapter(ldbAdapter);
        ldbAdapter.clear();
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

                for (DataSnapshot dataSnapshot : result.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        addUser(user);
                    }
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
}
