package sdp.moneyrun.ui.menu.friendlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.location.AndroidLocationService;
import sdp.moneyrun.menu.friendlist.FriendListListAdapter;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal", "FieldCanBeLocal"})
public class FriendListActivity extends AppCompatActivity {

    private final String TAG = FriendListActivity.class.getSimpleName();
    @Nullable
    private AndroidLocationService locationService;
    @NonNull
    private ArrayList<User> friendList = new ArrayList<>();
    @Nullable
    private FriendListListAdapter ldbAdapter;
    private User user;
    private Button goBackToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Objects.requireNonNull(getSupportActionBar()).hide();
        locationService = AndroidLocationService.buildFromContextAndProvider(this, "");

        user = (User) getIntent().getSerializableExtra("user");
        goBackToMenu = findViewById(R.id.back_from_friend_list_button);

        goBackToMenu.setOnClickListener(v -> {
            Intent menuIntent = new Intent(FriendListActivity.this, MenuActivity.class);
            menuIntent.putExtra("user", user);
            startActivity(menuIntent);
            finish();
        });

        UserDatabaseProxy db = new UserDatabaseProxy();
        Task<DataSnapshot> taskUpdatedUser = db.updatedFriendListFromDatabase(user);

        if (taskUpdatedUser == null)
            return;

        taskUpdatedUser.addOnCompleteListener(task -> {
            if (task.isSuccessful())
                showFriendList();
        });

        addAdapter();
        Button searchButton = findViewById(R.id.friend_list_search_button);
        searchButton.setOnClickListener(v -> friendButtonFunctionality());
        DatabaseProxy.addOfflineListener(this, TAG);


    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseProxy.removeOfflineListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseProxy.addOfflineListener(this, TAG);
    }

    protected void onStop() {
        super.onStop();
        DatabaseProxy.removeOfflineListener();
    }

    /**
     * Link list adapter to the activity
     */
    private void addAdapter() {
        // The adapter lets us add item to a ListView easily.
        ldbAdapter = new FriendListListAdapter(this, friendList, user, locationService);
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
     * @return the location service.
     */
    @Nullable
    public AndroidLocationService getLocationService() {
        return locationService;
    }

    /**
     * Sets the location service.
     */
    public void setLocationService(@NonNull AndroidLocationService locationService) {
        this.locationService = locationService;
        // Update friend list
        showFriendList();
    }

    @Override
    public void onBackPressed() {
    }
}
