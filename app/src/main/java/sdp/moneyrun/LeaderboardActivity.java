package sdp.moneyrun;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {
    //// for more explanation go to https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#attaching-the-adapter-to-a-listview

    private ArrayList<Player> playerList = new ArrayList<>();
    private LeaderboardListAdapter ldbAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        addAdapter();
    }

    private void addAdapter(){
        ldbAdapter = new LeaderboardListAdapter(this,playerList);
        ListView ldbView = (ListView) findViewById(R.id.ldblistView);
        ldbView.setAdapter(ldbAdapter);
    }

    public void addPlayerList(ArrayList<Player> playerList){
        if(playerList == null){
            throw new NullPointerException("Player list is null");
        }
        ldbAdapter.addAll(playerList);
    }

    public void addPlayer(Player player){
        // can't just add a player directly to an adapter
        if(player == null){
            throw new NullPointerException("player is null");
        }
        ArrayList<Player> to_add = new ArrayList<>();
        to_add.add(player);
        addPlayerList(to_add);
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
    }
}