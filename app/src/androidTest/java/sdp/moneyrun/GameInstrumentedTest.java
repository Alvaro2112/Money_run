package sdp.moneyrun;

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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
public class GameInstrumentedTest {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    Game getTestGame() {
        String name = "TestGame";
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "James", "Lausanne", 3, 4));
        players.add(new Player(2, "Potter", "Nyon", 3, 4));
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("?", "a", "b", "c", "d", "e"));
        int maxPlayers = 4;

        Location targetLocation = new Location("");//provider name is unnecessary
        targetLocation.setLatitude(10);
        targetLocation.setLongitude(20);
        return new Game(name, players, maxPlayers, targetLocation);
    }



    @Test
    public void GameIsAddedToDB(){
        Game g = getTestGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String id = g.getGameId();

        //Something went wrong, game might not have been uploaded properly
        if(id.equals("")){
            fail();
        }
        Task<DataSnapshot> dataTask = ref.child("open_games").child(id).get();
        dataTask.addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Game fromDB = Game.getGameFromTaskSnapshot(task);
                assertEquals(g.getGameDbData().getName(), fromDB.getGameDbData().getName());
                assertEquals(g.getGameDbData().getPlayers(), fromDB.getGameDbData().getPlayers());
                assertEquals(g.getGameDbData().getMaxPlayerNumber(), fromDB.getGameDbData().getMaxPlayerNumber());
                //Soooo Android.Location doesnt have a .equals() method... Have to check manually -_-
                if(g.getGameDbData().getStartLocation().getLatitude() != fromDB.getGameDbData().getStartLocation().getLatitude() ||
                        g.getGameDbData().getStartLocation().getLongitude() != fromDB.getGameDbData().getStartLocation().getLongitude()){
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
        Game g = getTestGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String id = g.getGameId();
        assertEquals(id, g.addToDB());
    }


    /**
     * This tests that the player list is in synchro with the DB
     */
    @Test
    public void GamePlayerListChangesWithDB(){
        Game g = getTestGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String id = g.getGameId();

        //Something went wrong, game might not have been uploaded properly
        if(id.equals("")){
            fail();
        }
        List<Player> playerss = new ArrayList<>();
        playerss.add(new Player(5, "Ron", "Zurich", 3, 4));
        playerss.add(new Player(6, "Wisley", "Amsterdam", 3, 4));
        ref.child("open_games").child(id).child("players").setValue(playerss);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(playerss, g.getGameDbData().getPlayers());
    }

    @Test
    public void getGameDataSnapshotRetrievesGameFromDB(){
        Game g = getTestGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String id = g.getGameId();
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
            Game g = getTestGame();
            g.setPlayers(null);
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }

    @Test
    public void setPlayersFailsOnEmptyList(){
        Game g = getTestGame();
        try{
            g.setPlayers(new ArrayList<Player>());
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }

    @Test
    public void setPlayersSetsPlayersLocally(){
        Game g = getTestGame();
        List<Player> p = new ArrayList<>();
        Player toAdd = new Player(542, "Iron Man", "malibu California", 99, 102);
        Player toAdd2 = new Player(544, "Pepper Pots", "malibu California", 99, 102);
        p.add(toAdd);
        p.add(toAdd2);
        g.setPlayers(p);
        assertEquals(p,g.getGameDbData().getPlayers());
    }

    @Test
    public void setPlayersSetsPlayersOnDB(){
        Game g = getTestGame();
        g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Player> p = new ArrayList<>();
        Player toAdd = new Player(542, "Iron Man", "malibu California", 99, 102);
        Player toAdd2 = new Player(544, "Pepper Pots", "malibu California", 99, 102);
        p.add(toAdd);
        p.add(toAdd2);
        g.setPlayers(p);
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        assertEquals(p,g.getGameDbData().getPlayers());
    }

    @Test
    public void addGameListenerFailsOnNullArg(){
        try{
            Game g = getTestGame();
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
        Game g = getTestGame();
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
    public void GameShortConstructorThrowsErrorOnNullArg(){
        try {
            Game g = new Game("name", new ArrayList<Player>(), 3, null);
        }catch(IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @Test
    public void GameLongConstructorThrowsErrorOnNullArg(){
        try {
            Game g = new Game("name", new ArrayList<Player>(), 3, null, null, null);
        }catch(IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @Test
    public void GameLongConstructorWorksProperly(){
        try {
            List<Player> pList = new ArrayList<Player>();
            pList.add(new Player(34));
            Game g = new Game("name", pList, 3, new ArrayList<Riddle>(), new ArrayList<Coin>(), new Location(""));
        }catch(IllegalArgumentException e){
            fail();
        }
    }

    @Test
    public void getIdReturnsId(){
        Game g = getTestGame();
        String id = g.addToDB();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(id, g.getGameId());
    }

    @Test
    public void testStartGameDoesNotCrash() {
        Game game = getTestGame();
        game.startGame();
        Game.startGame(game);
        assertEquals(1, 1);
    }

    @Test
    public void askPlayerFailsOnNull(){
        Game g = getTestGame();
        try{
            g.askPlayer(null, null);
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }

    @Test
    public void askPlayerQuestionShouldReturnFalse() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        Game game = getTestGame();
        assertEquals(game.askPlayer(game.getGameDbData().getPlayers().get(0), riddleList.get(0)), false);
    }


    @Test
    public void getGameDataReturnsGameData(){
        String name = "TestGame";
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "James", "Lausanne", 3, 4));
        players.add(new Player(2, "Potter", "Nyon", 3, 4));
        int maxPlayers = 4;
        Location targetLocation = new Location("");//provider name is unnecessary
        targetLocation.setLatitude(10);
        targetLocation.setLongitude(20);
        assertEquals(new GameDbData(name, players, maxPlayers, targetLocation).getPlayers().get(0), getTestGame().getGameDbData().getPlayers().get(0));
    }

    @Test
    public void equalsFailsOnNull(){
        Game g = getTestGame();
        assertFalse(g.equals(null));
    }

    @Test
    public void equalsWorksOnSameRef(){
        Game g = getTestGame();
        Game sameRef = g;
        assertTrue(g.equals(sameRef));
    }

    /*On the matter of testing whether equals actually works, we already test in GameDbData that
    it works, and the Game equals method is just a call to that. Besides to test Database things
    we need the test file to be in the InstrumentedTest folder. To test with Mockito, we need the
    test to be in the Unit test folder. So we cannot do both without heavily modifying the class,
    and as stated, equals is already tested in GameDbData, and this equals is litterally just
    a call to that one*/


}
