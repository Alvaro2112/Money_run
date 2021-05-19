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
import java.util.HashSet;
import java.util.List;

import sdp.moneyrun.R;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.menu.MainLeaderboardListAdapter;
import sdp.moneyrun.user.User;

@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal"})
public class MainLeaderboardActivity extends AppCompatActivity {

    private final int NUM_PLAYERS_LEADERBOARD = 10;

    private ArrayList<User> userList = new ArrayList<>();
    private MainLeaderboardListAdapter ldbAdapter;
    private User user;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void bestToWorstUser(@NonNull List<User> users) {
        users.sort((o1, o2) -> Integer.compare(o2.getMaxScoreInGame(), o1.getMaxScoreInGame()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_leaderboard);

        user = (User) getIntent().getSerializableExtra("user");
        System.out.println("ME"+user.getUserId());
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
            }
        });
    }

    /**
     * @param userList: users to be added to the leaderboard
     *                  Adds users to leaderboard
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addUserList(@Nullable List<User> userList) {
        if (userList == null) {
            throw new NullPointerException("user list should not be null.");
        }

        ldbAdapter.addAll(userList);
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < ldbAdapter.getCount(); ++i)
            users.add(ldbAdapter.getItem(i));
        ldbAdapter.clear();
        bestToWorstUser(users);
        ldbAdapter.addAll(users);
    }

    /**
     * @param user: user to be added to the leaderboard
     *              Adds user to leaderboard
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addUser(@Nullable User user) {
        if (user == null) {
            throw new IllegalArgumentException("user should not be null.");
        }

        List<User> to_add = new ArrayList<>();
        to_add.add(user);
        addUserList(to_add);
    }
}
