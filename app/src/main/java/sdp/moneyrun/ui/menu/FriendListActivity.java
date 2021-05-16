package sdp.moneyrun.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import sdp.moneyrun.R;
import sdp.moneyrun.menu.FriendListListAdapter;
import sdp.moneyrun.user.User;

public class FriendListActivity extends AppCompatActivity {

    private ArrayList<User> friendList = new ArrayList<>();
    private FriendListListAdapter ldbAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        user = (User) getIntent().getSerializableExtra("user");

        addAdapter();
        friendButtonFunctionality();
        getFriendList();
    }

    private void addAdapter(){
        // The adapter lets us add item to a ListView easily.
        ldbAdapter = new FriendListListAdapter(this, friendList, user);
        ListView ldbView = findViewById(R.id.friend_list_view);
        ldbView.setAdapter(ldbAdapter);
        ldbAdapter.clear();
    }

    private void friendButtonFunctionality(){
        Button button = findViewById(R.id.friend_list_search_button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(FriendListActivity.this, AddFriendListActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }

    private void getFriendList(){
        // TODO
    }
}
