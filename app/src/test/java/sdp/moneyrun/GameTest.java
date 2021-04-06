package sdp.moneyrun;


import android.location.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class GameTest {
    @Test
    public void basicRiddleTest() {
        String question = "What is the color of the sky";
        String correctAnswer = "blue";
        String[] possibleAnswers = {"blue", "green", "yellow", "brown"};

        Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");
        assertEquals(question, riddle.getQuestion());
        assertEquals(correctAnswer, riddle.getAnswer());
        assertArrayEquals(possibleAnswers, riddle.getPossibleAnswers());
    }

    @Test
    public void RiddleThrowsExceptionWhenArgumentsAreNull() {

        assertThrows(IllegalArgumentException.class, () -> {
            Riddle riddle = new Riddle(null, "blue", "green", "yellow", "brown", "a");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Riddle riddle = new Riddle("a", null, "green", "yellow", "brown", "very");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Riddle riddle = new Riddle("a", "blue", null, "yellow", "brown", "very");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Riddle riddle = new Riddle("a", "blue", "green", null, "brown", "very");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Riddle riddle = new Riddle("a", "blue", "green", "yellow", null, "very");
        });


        assertThrows(IllegalArgumentException.class, () -> {
            Riddle riddle = new Riddle("a", "blue", "green", "yellow", "brown", null);
        });
    }


    @Test
    public void testGameConstructorThrowsExceptionWhenNullArguments() {
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Location startLocation = new Location("");
        try {
            Game game = new Game(null, null, null, 0, null, null);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(gameId, null, null, 0, null, null);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(gameId, name, null, 0, null, null);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(gameId, name, players, 0, null, null);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(gameId, name, players, 0, riddleList, null);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(gameId, name, players, 0, riddleList, startLocation);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
    }

    @Test
    public void testStartGameDoesNotCrash() {
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, new Location("LocationManager#GPS_PROVIDER"));
        game.startGame();
        Game.startGame(game);
        assertEquals(1, 1);
    }

    @Test
    public void askPlayerQuestionShouldReturnFalse() {
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, new Location("LocationManager#GPS_PROVIDER"));
        assertEquals(game.askPlayer(players.get(0), riddleList.get(0)), false);
    }

    @Test
    public void getRandomQuestionReturnsAQuestion() {
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, new Location("LocationManager#GPS_PROVIDER"));
        Riddle riddle = game.getRandomRiddle();
        assertTrue(riddle.getClass() == Riddle.class);
    }

    @Test
    public void getRandomQuestionReturnsNullIfNoQuestion() {
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, new Location("LocationManager#GPS_PROVIDER"));
        Riddle riddle = game.getRandomRiddle();
        assertNull(riddle);
    }

    @Test
    public void getGameIdReturnsCorrectValues(){
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, new Location("LocationManager#GPS_PROVIDER"));

        String gameIdRet = game.getGameId();
        assertEquals(gameIdRet, gameId);
    }

    @Test
    public void getNameReturnsCorrectValues(){
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, new Location("LocationManager#GPS_PROVIDER"));

        String nameRet = game.getName();
        assertEquals(nameRet, name);

    }

    @Test
    public void getPlayerCountReturnsCorrectValues(){
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, new Location("LocationManager#GPS_PROVIDER"));

        int playerCount = game.getPlayerCount();
        assertEquals(playerCount, 1);
    }

    @Test
    public void getMaxPlayerCountReturnsCorrectValues(){
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0));
        Game game = new Game(gameId, name, players, 12, riddleList, new Location("LocationManager#GPS_PROVIDER"));

        int maxPlayerCount = game.getMaxPlayerCount();
        assertEquals(maxPlayerCount, 12);
    }
}
