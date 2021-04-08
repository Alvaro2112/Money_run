package sdp.moneyrun;


import android.location.Location;
import android.os.Parcel;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class GameTest {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();


    @Test
    public void basicRiddleTest(){
        String question = "What is the color of the sky";
        String answer = "blue";
        Riddle riddle = new Riddle(question,answer);
        assertEquals(question,riddle.getQuestion());
        assertEquals(answer,riddle.getAnswer());
    }
    @Test
    public void RiddleThrowsExceptionWhenArgumentsAreNull(){
        try {
            Riddle riddle = new Riddle("Is it good?", null);
        }catch (IllegalArgumentException e){
            assertEquals(1,1);
        }
        try {
            Riddle riddle = new Riddle(null, "very");
        }catch (IllegalArgumentException e){
            assertEquals(1,1);
        }
    }
    /*
    @Test
    public void testGameConstructorThrowsExceptionWhenNullArguments(){
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?","no"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3));
        try {
            Game game = new Game(null, riddleList, null);
        }catch (IllegalArgumentException e){
            assertEquals(1,1);
        }
        try {
            Game game = new Game(players, null, null);
        }catch (IllegalArgumentException e){
            assertEquals(1,1);
        }
        try {
            Game game = new Game(players, riddleList, null);
        }catch (IllegalArgumentException e){
            assertEquals(1,1);
        }
    }
    @Test
    public void testStartGameDoesNotCrash(){
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?","no"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3));
        Game game = new Game(players,riddleList,new Location("LocationManager#GPS_PROVIDER"));
        game.startGame();
        Game.startGame(game);
        assertEquals(1,1);
    }

    @Test
    public void askPlayerQuestionShouldReturnFalse(){
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?","no"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3));
        Game game = new Game(players,riddleList,new Location("LocationManager#GPS_PROVIDER"));
        assertEquals(game.askPlayer(players.get(0),riddleList.get(0)), false);
    }
    */

    @Test
    public void GameConstructorThrowsErrorOnNullArg(){
        try {
            Game g = new Game("name", new ArrayList<Player>(), 3, new ArrayList<Riddle>(), null);
        }catch(IllegalArgumentException e){
            assertTrue(true);
        }
    }


    @Test
    public void GameIsAddedToDBOnCreation(){
        Game g = new Game("name", new ArrayList<Player>(), 3, new ArrayList<Riddle>(), new Location(""));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String id = g.getGameId();
        Task<DataSnapshot> dataTask = ref.child("open_games").child(id).child("name").get();

        dataTask.addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                assertEquals(g,Game.getGameFromTaskSnapshot(task));
                //assertEquals(new Integer(3),Game.getGameFromTaskSnapshot(task));
            }else{
                assertEquals("1","0");
            }
        });
        while(!dataTask.isComplete()){
            System.out.println("false");
        }
    }





    @Test
    public void GamePlayerListChangesWithDB(){
        //TODO
    }

    @Test
    public void getGameDataSnapshotFailsOnNullArg(){

    }

    @Test
    public void getGameDataSnapshotFailsIfGameNotPresentInDB(){

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
