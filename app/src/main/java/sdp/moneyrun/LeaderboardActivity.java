package sdp.moneyrun;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {
    //// for more explanation go to https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#attaching-the-adapter-to-a-listview

    private ArrayList<Player> playerList = new ArrayList<>();
    private LeaderboardListAdapter ldbAdapter;
    private Player user;
    private DatabaseProxy db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        user = (Player) getIntent().getSerializableExtra("user");

        addAdapter();
//        setUserPlayer();
        setMainPlayer(null);
        //TODO
        // Put addPlayer with local cache
        setDummyPlayers();
    }

    /**
     *
     * @return the leaderboard adapter instance
     */
    public LeaderboardListAdapter getLdbAdapter(){
        return ldbAdapter;
    }


    private void addAdapter(){
        // The adapter lets us add item to a ListView easily.
        ldbAdapter = new LeaderboardListAdapter(this,playerList);
        ListView ldbView = (ListView) findViewById(R.id.ldblistView);
        ldbView.setAdapter(ldbAdapter);
    }
    /**
     *
     * @param playerList: players to be added to the leaderboard
     *  Adds players to leaderboard
     */
    public void addPlayerList(ArrayList<Player> playerList){
        if(playerList == null){
            throw new NullPointerException("Player list is null");
        }
        ldbAdapter.addAll(playerList);
    }

    /**
     *
     * @param player: player to be added to the leaderboard
     *  Adds player to leaderboard
     */
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

    /**
     *
     *  Initializes the player object private instance
     */
    public void setMainPlayer(Player player){
        if(user == null && player != null){
            addPlayer(player);
        }
    }
    /**
    * This function will set up players in the leaderboard once we know their player ids and names
    * it will set up dummy players before that so that we have a leaderboard nonetheless every time a player joins
    * the game( up to 6 players for now), on data change listeners will be attached to these players here so that
    * once real players join the leaderboard updates accordingly
    * */
    public void setDummyPlayers(){
        DatabaseProxy databaseProxy = new DatabaseProxy();
        Player dummy1 = new Player(1000000);
        dummy1.setName("Dummy Player 1");
        dummy1.setAddress("Here");
        dummy1.setScore(1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        addPlayer(dummy1);
        attachListenerToPlayer(dummy1,databaseProxy);
        for(int i = 2; i< 6;++i){
            Player dummy = new Player(i*1000000);
            dummy.setName("Dummy Player "+ i);
            dummy.setAddress("Here");
            dummy.setScore(i);
            addPlayer(dummy);
        }
    }/**
        @param  dummy1: dummy representation of a player that will later evolve into a player that was in a game
        @param databaseProxy: the proxy database that we use to access Firebase
        Attaches a lister to a player so that once real players join the game the dummy player will represent
        an actual person with all the player object attributes associated with it
     */
    private void attachListenerToPlayer(Player dummy1, DatabaseProxy databaseProxy){
        databaseProxy.addPlayerListener(dummy1, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player update = snapshot.getValue(Player.class);
                System.out.println(snapshot.getValue(Player.class)+ "Getting snapshot on data change in leaderboard class");
                if(update != null)
                    dummy1.setName(update.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /**
        @return: returns the player object representing the person that wants to access the leaderboard
     */
    public Player getUserPlayer() {
        return user;
    }
}