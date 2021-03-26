package sdp.moneyrun;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {
    //// for more explanation go to https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#attaching-the-adapter-to-a-listview

    private ArrayList<Player> playerList = new ArrayList<>();
    private LeaderboardListAdapter ldbAdapter;
    private Player userPlayer;
    private DatabaseProxy db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        addAdapter();
//        setUserPlayer();
        setMainPlayer();
        //TODO
        // Put addPlayer with local cache
    }

    public LeaderboardListAdapter getLdbAdapter(){
        return ldbAdapter;
    }


    private void addAdapter(){
        // The adapter lets us add item to a ListView easily.
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
        // can't just add a player directly to an adapter, need to put it in a list
        if(player == null){
            throw new IllegalArgumentException("player is null");
        }
        ArrayList<Player> to_add = new ArrayList<>();
        to_add.add(player);
        addPlayerList(to_add);
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
    }

    public void setMainPlayer(){
        int playerId = getIntent().getIntExtra("playerId",0);
        if(playerId != 0) {
            String[] playerInfo = getIntent().getStringArrayExtra("playerId" + playerId);
            userPlayer = new Player(playerId);
            userPlayer.setName(playerInfo[0]);
            userPlayer.setAddress(playerInfo[1]);
            userPlayer.setScore(0);
            addPlayer(userPlayer);
        }
    }

//    public void setUserPlayer2(){
//        int playerId = getIntent().getIntExtra("playerId",0);
//        db = new DatabaseProxy();
//        Task<DataSnapshot> playerTask = db.getPlayerTask(playerId);
//        playerTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful())
//                    userPlayer = db.getPlayerFromTask(playerTask);
//                else
//                    return;//TODO: return error message
//            }
//        });
//        while(!playerTask.isComplete()){System.out.println("Not");}
//        addPlayer(userPlayer);
//    }
//    public void setUserPlayer(){
//        int playerId = getIntent().getIntExtra("playerId",0);
//        Player player = new Player(playerId);
//        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("players").child(""+playerId);
//        dbReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Player player2 = (Player) snapshot.getValue(Player.class);
//                player.setName(player2.getName());
//                player.setAddress(player2.getAddress());
//                player.setScore(0);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        addPlayer(player);
//    }
}