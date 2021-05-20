package sdp.moneyrun.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.menu.FriendListListAdapter;
import sdp.moneyrun.user.User;

@SuppressWarnings("FieldMayBeFinal")
public class FriendListActivity extends AppCompatActivity {

    @NonNull
    private ArrayList<User> friendList = new ArrayList<>();
    private FriendListListAdapter ldbAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        user = (User) getIntent().getSerializableExtra("user");
        UserDatabaseProxy db = new UserDatabaseProxy();
        db.updatedFriendListFromDatabase(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showFriendList();
            }
        });

        addAdapter();
        Button searchButton = findViewById(R.id.friend_list_search_button);
        searchButton.setOnClickListener(v -> friendButtonFunctionality());
    }

    /**
     * Link list adapter to the activity
     */
    private void addAdapter() {
        // The adapter lets us add item to a ListView easily.
        ldbAdapter = new FriendListListAdapter(this, friendList, user);
        Helpers.addAdapter(ldbAdapter, this, R.id.friend_list_view);
    }

    /**
     * Functionality to link this activity to the add friend activity
     */
    private void friendButtonFunctionality() {
        Intent intent = new Intent(FriendListActivity.this, AddFriendListActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    /**
     * Display the friend list
     */
    private void showFriendList() {
        List<String> friendListId = user.getFriendIdList();

        ldbAdapter.clear();

        UserDatabaseProxy db = new UserDatabaseProxy();
        for (String userId : friendListId) {
            db.getUserTask(userId).addOnCompleteListener(task -> {
                User requestedUser = db.getUserFromTask(task);
                if (requestedUser == null) {
                    return;
                }

                addUserToList(requestedUser);
            });
        }
    }

    /**
     * Add a user to the list adapter
     *
     * @param user the user to add
     */
    public void addUserToList(@Nullable User user) {
        if (user == null) {
            throw new IllegalArgumentException("user should not be null.");
        }
        List<User> userList = new ArrayList<>();
        userList.add(user);

        ldbAdapter.addAll(userList);
    }

    /**
     * Add a user list to the list adapter
     *
     * @param userList the user list to add
     */
    public void addUserList(@Nullable List<User> userList) {
        if (userList == null) {
            throw new NullPointerException("user list should not be null.");
        }

        ldbAdapter.clear();
        ldbAdapter.addAll(userList);
    }
}
