package sdp.moneyrun.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.R;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.menu.AddFriendListListAdapter;
import sdp.moneyrun.menu.FriendListListAdapter;
import sdp.moneyrun.user.User;

public class AddFriendListActivity extends AppCompatActivity {

    private List<User> resultList = new ArrayList<>();
    private AddFriendListListAdapter ldbAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_list);

        user = (User) getIntent().getSerializableExtra("user");
        UserDatabaseProxy db = new UserDatabaseProxy();
        db.updatedFriendListFromDatabase(user);

        addAdapter();
        searchButtonFunctionality();
        goBackButtonFunctionality();
    }

    /**
     * Link list adapter to the activity
     */
    private void addAdapter(){
        // The adapter lets us add item to a ListView easily.
        ldbAdapter = new AddFriendListListAdapter(this, resultList, user);
        ListView ldbView = findViewById(R.id.friend_add_list_view);
        ldbView.setAdapter(ldbAdapter);
        ldbAdapter.clear();
    }

    /**
     * Functionality for the search friend button
     */
    private void searchButtonFunctionality(){
        Button button = findViewById(R.id.friend_add_list_search_button);
        button.setOnClickListener(v -> {
            EditText editTextFilter = findViewById(R.id.friend_add_list_filter);
            String textFilter = editTextFilter.getText().toString();

            if(textFilter.length() <= 1){
                editTextFilter.setError("The filter should be more precise.");
                ldbAdapter.clear();
                return;
            }

            UserDatabaseProxy db = new UserDatabaseProxy();
            db.getUsersTask().addOnCompleteListener(task -> {
                resultList = db.getUserListFromTaskFromSimilarName(task, textFilter);
                if(resultList != null){
                    addUserList(resultList);
                }
            });
        });
    }

    /**
     * Functionality for the go back to friend list button
     */
    private void goBackButtonFunctionality(){
        Button button = findViewById(R.id.friend_add_list_button_back);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(AddFriendListActivity.this, FriendListActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Add a user list to the list adapter
     * @param userList the user list to add
     */
    public void addUserList(List<User> userList){
        if(userList == null){
            throw new NullPointerException("user list should not be null.");
        }

        ldbAdapter.clear();
        ldbAdapter.addAll(userList);
    }

}