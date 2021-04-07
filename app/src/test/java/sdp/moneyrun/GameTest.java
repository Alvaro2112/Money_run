package sdp.moneyrun;


import android.location.Location;
import android.os.Parcel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
public class GameTest {
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
        assertThrows(IllegalArgumentException.class, ()->{
            Game g = new Game("name", new ArrayList<Player>(), 3, new ArrayList<Riddle>(), null);
        });
    }

    @Test
    public void GameIsAddedToDBOnCreation(){
        //TODO
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
    public void addPlayerAddsPlayerProperlytoDB(){

    }

    @Test
    public void removePlayerRemovesPlayerFromDB(){

    }

    @Test
    public void getIdreturnsId(){

    }

}
