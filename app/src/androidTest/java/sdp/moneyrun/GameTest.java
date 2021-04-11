package sdp.moneyrun;


import android.location.Location;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GameTest {
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
/*
    @Test
    public void basicRiddleTest() {
        String question = "What is the color of the sky";
        String correctAnswer = "blue";
        String[] possibleAnswers = {"blue", "green", "yellow", "brown"};

        Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");
        assertEquals(question, riddle.getQuestion());
        assertEquals(correctAnswer, riddle.getAnswer());
        assertArrayEquals(possibleAnswers, riddle.getPossibleAnswers());
    }*/

    @Test
    public void RiddleThrowsExceptionWhenArgumentsAreNull() {
        try{
            Riddle riddle = new Riddle(null, "blue", "green", "yellow", "brown", "a");
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{
            Riddle riddle = new Riddle("a", null, "green", "yellow", "brown", "a");
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{
            Riddle riddle = new Riddle("a", "blue", null, "yellow", "brown", "a");
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{
            Riddle riddle = new Riddle("a", "blue", "green", null, "brown", "a");
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{
            Riddle riddle = new Riddle("a", "blue", "green", "yellow", null, "a");
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{
            Riddle riddle = new Riddle("a", "blue", "green", "yellow", "brown", null);
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }


    }
    /*
    @Test
    public void testGameConstructorThrowsExceptionWhenNullArguments() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();

        players.add(new Player(3,"Bob", "Epfl",0,0));
        try {
            Game game = new Game(null, riddleList, null,null);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(players, null, coins,null);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(players, riddleList, null,new Location("LocationManager#GPS_PROVIDER"));
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
    }

    @Test
    public void testStartGameDoesNotCrash() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(players, riddleList,coins, new Location("LocationManager#GPS_PROVIDER"));
        game.startGame();
        Game.startGame(game);
        assertEquals(1, 1);
    }

    @Test
    public void askPlayerQuestionShouldReturnFalse() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(players, riddleList,coins, new Location("LocationManager#GPS_PROVIDER"));
        assertEquals(game.askPlayer(players.get(0), riddleList.get(0)), false);
    }

    @Test
    public void getRandomQuestionReturnsAQuestion() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(players, riddleList, coins,new Location("LocationManager#GPS_PROVIDER"));
        Riddle riddle = game.getRandomRiddle();
        assertTrue(riddle.getClass() == Riddle.class);



    }
    */

    @Test
    public void GameConstructorThrowsErrorOnNullArg(){
        try {
            Game g = new Game("name", new ArrayList<Player>(), 3, null);
        }catch(IllegalArgumentException e){
            assertTrue(true);
        }
    }


    @Test
    public void GameIsAddedToDBOnCreation(){
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


    /**
     * This tests that the player list is in synchro with the DB
     */

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

/*
    @Test
    public void getGameDataSnapshotFailsIfGameNotPresentInDB(){
        try {
            Task<DataSnapshot> taskk = Game.getGameDataSnapshot("01234567899876543210");
            while (!taskk.isComplete()) {
                System.out.println("waiting");
            }
            Thread.sleep(3000);
        }catch (Exception e){
            return;
        }
        fail();
    }*/

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
    public void addPlayerAddsPlayerToDB(){

    }

    @Test
    public void removePlayerRemovesPlayerFromDB(){

    }

    @Test
    public void getIdReturnsId(){

    }

}
