package sdp.moneyrun.ui.menu.friendlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.menu.friendlist.AddFriendListListAdapter;
import sdp.moneyrun.user.User;

public class AddFriendListActivity extends AppCompatActivity {

    private final String TAG = AddFriendListActivity.class.getSimpleName();
    @Nullable
    private List<User> resultList = new ArrayList<>();
    @Nullable
    private AddFriendListListAdapter ldbAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_add_friend_list);

        user = (User) getIntent().getSerializableExtra("user");
        UserDatabaseProxy db = new UserDatabaseProxy();
        db.updatedFriendListFromDatabase(user);

        addAdapter();
        Button searchButton = findViewById(R.id.friend_add_list_search_button);
        searchButton.setOnClickListener(v -> searchButtonFunctionality());
        Button goButton = findViewById(R.id.friend_add_list_button_back);
        goButton.setOnClickListener(v -> goBackButtonFunctionality());
        DatabaseProxy.addOfflineListener(this, TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseProxy.removeOfflineListener();
    }

    /**
     * Link list adapter to the activity
     */
    private void addAdapter() {
        // The adapter lets us add item to a ListView easily.
        ldbAdapter = new AddFriendListListAdapter(this, resultList, user);
        Helpers.addAdapter(ldbAdapter, this, R.id.friend_add_list_view);
    }

    /**
     * Functionality for the search friend button
     */
    private void searchButtonFunctionality() {
        EditText editTextFilter = findViewById(R.id.friend_add_list_filter);
        String textFilter = editTextFilter
                .getText()
                .toString()
                .trim()
                .toLowerCase(Locale.getDefault());

        if (textFilter.length() <= 1) {
            editTextFilter.setError("The filter should be more precise.");
            ldbAdapter.clear();
            return;
        }

        UserDatabaseProxy db = new UserDatabaseProxy();
        db.getUsersTask().addOnCompleteListener(task -> {
            resultList = db.getUserListFromTaskFromSimilarName(task, textFilter);
            if (resultList != null) {
                addUserList(resultList);
            }
        });
    }

    /**
     * Functionality for the go back to friend list button
     */
    private void goBackButtonFunctionality() {
        Intent intent = new Intent(AddFriendListActivity.this, FriendListActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
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

    @Override
    public void onBackPressed() {
    }

}
