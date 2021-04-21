package sdp.moneyrun.game;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.database.GameDbData;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
public class GameInstrumentedTest {

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    public Game getGame(){
        String name = "name";
        Player host = new Player(3,"Bob", "Epfl",0,0,0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);

        return new Game(name, host, maxPlayerCount, riddles, coins, location, true);
    }

    public GameDbData getGameData(){
        String name = "name";
        Player host = new Player(3,"Bob", "Epfl",0,0,0);
        List<Player> players = new ArrayList<>();
        players.add(host);
        int maxPlayerCount = 3;
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);

        return new GameDbData(name, host, players, maxPlayerCount, location, true);
    }

    @Test
    public void GameIsAddedToDB(){
        Game g = getGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String id = g.getId();

        //Something went wrong, game might not have been uploaded properly
        if(id.equals("")){
            fail();
        }
        Task<DataSnapshot> dataTask = ref.child("open_games").child(id).get();
        dataTask.addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Game fromDB = Game.getGameFromTaskSnapshot(task);
                assertEquals(g.getName(), fromDB.getName());
                assertEquals(g.getPlayers(), fromDB.getPlayers());
                assertEquals(g.getMaxPlayerCount(), fromDB.getMaxPlayerCount());
                //Soooo Android.Location doesnt have a .equals() method... Have to check manually -_-
                if(g.getStartLocation().getLatitude() != fromDB.getStartLocation().getLatitude() ||
                        g.getStartLocation().getLongitude() != fromDB.getStartLocation().getLongitude()){
                    fail();
                }

            }else{
                fail();
            }
        });
        while(!dataTask.isComplete()){
            System.out.println("false");
        }
    }

    @Test
    public void GameCannotBeAddedTwiceToDB(){
        Game g = getGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String id = g.getId();
        assertEquals(id, g.addToDB());
    }


    /**
     * This tests that the player list is in synchro with the DB
     */
    @Test
    public void GamePlayerListChangesWithDB(){
        Game g = getGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String id = g.getId();

        //Something went wrong, game might not have been uploaded properly
        if(id.equals("")){
            fail();
        }
        List<Player> players = new ArrayList<>();
        players.add(new Player(5, "Ron", "Zurich", 3, 4, 0));
        players.add(new Player(6, "Wisley", "Amsterdam", 3, 4, 0));
        ref.child("open_games").child(id).child("players").setValue(players);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(players, g.getPlayers());
    }

    @Test
    public void getGameDataSnapshotRetrievesGameFromDB(){
        Game g = getGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String id = g.getId();
        //Something went wrong, game might not have been uploaded properly
        if(id.equals("")){
            fail();
        }
        Task<DataSnapshot> dataTaskManually = ref.child("open_games").child(id).get();
        Task<DataSnapshot> dataTaskFunction = Game.getGameDataSnapshot(id);
        dataTaskManually.addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                dataTaskFunction.addOnCompleteListener(task2 -> {
                    if(task.isSuccessful()){
                        String manual = task.getResult().toString();
                        String function = task2.getResult().toString();
                        assertEquals(manual,function);
                    }
                });
            }else{
                fail();
            }
        });

        while(!dataTaskManually.isComplete()){
            System.out.println("false");
        }
        while(!dataTaskFunction.isComplete()){
            System.out.println("false");
        }
    }

    @Test
    public void getGameDataSnapshotFailsOnNullArg(){
        try{
            Game.getGameDataSnapshot(null);
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }

    @Test
    public void getGameFromTaskSnapShotFailsOnNullArg(){
        try{
            Game.getGameFromTaskSnapshot(null);
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }

    @Test
    public void setPlayersFailsOnNullArg(){
        try{
            Game g = getGame();
            g.setPlayers(null);
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }

    @Test
    public void addPlayerFailsOnNullArg(){
        try{
            Game g = getGame();
            g.addPlayer(null);
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }

    @Test
    public void removePlayerFailsOnNullArg(){
        try{
            Game g = getGame();
            g.removePlayer(null);
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }

    @Test
    public void setPlayersFailsOnEmptyList(){
        Game g = getGame();
        try{
            g.setPlayers(new ArrayList<>());
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }

    @Test
    public void setPlayersSetsPlayersLocally(){
        Game g = getGame();
        List<Player> p = new ArrayList<>();
        Player toAdd = new Player(542, "Iron Man", "malibu California", 99, 102, 0);
        Player toAdd2 = new Player(544, "Pepper Pots", "malibu California", 99, 102, 0);
        p.add(toAdd);
        p.add(toAdd2);
        g.setPlayers(p);
        assertEquals(p,g.getPlayers());
    }

    @Test
    public void addPlayerSetsPlayersLocally(){
        Game g = getGame();
        Player host = new Player(3,"Bob", "Epfl",0,0,0);
        Player player = new Player(542, "Iron Man", "malibu California", 99, 102, 0);
        List<Player> players = new ArrayList<>();
        players.add(host);
        players.add(player);
        g.addPlayer(player);
        assertEquals(players, g.getPlayers());
    }

    @Test
    public void removePlayerSetsPlayersLocally(){
        Game g = getGame();
        Player host = new Player(3,"Bob", "Epfl",0,0,0);
        Player player = new Player(542, "Iron Man", "malibu California", 99, 102, 0);
        List<Player> players = new ArrayList<>();
        players.add(host);
        g.addPlayer(player);
        g.removePlayer(player);
        assertEquals(players, g.getPlayers());
    }

    @Test
    public void setPlayersSetsPlayersOnDB(){
        Game g = getGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Player> p = new ArrayList<>();
        Player toAdd = new Player(542, "Iron Man", "malibu California", 99, 102, 0);
        Player toAdd2 = new Player(544, "Pepper Pots", "malibu California", 99, 102, 0);
        p.add(toAdd);
        p.add(toAdd2);
        g.setPlayers(p);
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        assertEquals(p,g.getPlayers());
    }

    @Test
    public void addPlayerSetsPlayersOnDB(){
        Game g = getGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Player host = new Player(3,"Bob", "Epfl",0,0,0);
        Player player = new Player(542, "Iron Man", "malibu California", 99, 102, 0);
        List<Player> players = new ArrayList<>();
        players.add(host);
        players.add(player);
        g.addPlayer(player);
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        assertEquals(players, g.getPlayers());
    }

    @Test
    public void removePlayerSetsPlayersOnDB(){
        Game g = getGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Player host = new Player(3,"Bob", "Epfl",0,0,0);
        Player player = new Player(542, "Iron Man", "malibu California", 99, 102, 0);
        List<Player> players = new ArrayList<>();
        players.add(host);
        g.addPlayer(player);
        g.removePlayer(player);
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        assertEquals(players, g.getPlayers());
    }

    @Test
    public void addGameListenerFailsOnNullArg(){
        try{
            Game g = getGame();
            g.addGameListener(null);
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }

    @Test
    public void addGameListenerDoesntCrash(){
        ValueEventListener v = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        Game g = getGame();
        g.addToDB();
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try{
            g.addGameListener(v);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void getIdReturnsId(){
        Game g = getGame();
        String id = g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(id, g.getId());
    }

    @Test
    public void testStartGameDoesNotCrash() {
        Game game = getGame();
        game.startGame();
        Game.startGame(game);
        assertEquals(1, 1);
    }

    @Test
    public void askPlayerQuestionShouldReturnFalse() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        Game game = getGame();
        assertFalse(game.askPlayer(game.getPlayers().get(0), riddleList.get(0)));
    }


    @Test
    public void getGameDataReturnsGameData(){
        String name = "name";
        Player host = new Player(3,"Bob", "Epfl",0,0,0);
        int maxPlayerCount = 3;
        List<Player> players = new ArrayList<>();
        players.add(host);
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);

        GameDbData gameData = new GameDbData(name, host, players, maxPlayerCount, location, true);

        assertEquals(gameData, getGame().getGameDbData());
    }

    @Test
    public void getGameDbDataWorks(){
        Game game = getGame();
        assertEquals(game.getGameDbData(), getGameData());
    }

    @Test
    public void equalsWorks(){
        Game game1 = getGame();
        Game game2 = getGame();
        assertEquals(game1, game2);
        assertNotEquals(game1, null);
    }

    /*On the matter of testing whether equals actually works, we already test in GameDbData that
    it works, and the Game equals method is just a call to that. Besides to test Database things
    we need the test file to be in the InstrumentedTest folder. To test with Mockito, we need the
    test to be in the Unit test folder. So we cannot do both without heavily modifying the class,
    and as stated, equals is already tested in GameDbData, and this equals is litterally just
    a call to that one*/


}
