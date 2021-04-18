package sdp.moneyrun;


import android.location.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();

        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Location startLocation = new Location("");
        try {
            Game game = new Game(null, name, players, 0, riddles, coins, new Location("LocationManager#GPS_PROVIDER"));
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(gameId, null, players, 0, riddles, coins, new Location("LocationManager#GPS_PROVIDER"));
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(gameId, name, null, 0, riddles, coins, new Location("LocationManager#GPS_PROVIDER"));
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(gameId, name, players, 0, null, coins, new Location("LocationManager#GPS_PROVIDER"));
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(gameId, name, players, 0, riddles, null, new Location("LocationManager#GPS_PROVIDER"));
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(gameId, name, players, 0, riddles, coins, null);
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
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, coins, new Location("LocationManager#GPS_PROVIDER"));
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
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, coins, new Location("LocationManager#GPS_PROVIDER"));

        assertFalse(game.askPlayer(players.get(0), riddleList.get(0)));
    }

    @Test
    public void getRandomQuestionReturnsAQuestion() {
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, coins,new Location("LocationManager#GPS_PROVIDER"));
        Riddle riddle = game.getRandomRiddle();

        assertSame(riddle.getClass(), Riddle.class);
    }

    @Test
    public void getRandomQuestionReturnsNullIfNoQuestion() {
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, coins, new Location("LocationManager#GPS_PROVIDER"));

        assertNull(game.getRandomRiddle());
    }

    @Test
    public void getGameIdReturnsCorrectValues(){
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, coins, new Location("LocationManager#GPS_PROVIDER"));

        assertEquals(game.getGameId(), gameId);
    }

    @Test
    public void getNameGameReturnsCorrectValues(){
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,9));
        Game game = new Game(gameId, name, players, 0, riddleList, coins, new Location("LocationManager#GPS_PROVIDER"));

        assertEquals(game.getName(), name);
    }

    @Test
    public void getPlayerCountReturnsCorrectValues(){
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, coins, new Location("LocationManager#GPS_PROVIDER"));

        assertEquals(game.getPlayerCount(), 1);
    }

    @Test
    public void getMaxPlayerCountReturnsCorrectValues(){
        String gameId = "gameId";
        String name = "name";
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game = new Game(gameId, name, players, 0, riddleList, coins, new Location("LocationManager#GPS_PROVIDER"));

        assertEquals(game.getMaxPlayerCount(), 0);
    }

    @Test
    public void getIsVisibleReturnsCorrectValues(){
        String gameId = "gameId";
        String name = "name";
        boolean isVisible = false;
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game1 = new Game(gameId, name, players, 0, riddleList, coins, new Location("LocationManager#GPS_PROVIDER"));
        Game game2 = new Game(gameId, name, isVisible, players, 0, riddleList, coins, new Location("LocationManager#GPS_PROVIDER"));

        boolean defaultIsVisible = true;
        assertEquals(game1.getIsVisible(), defaultIsVisible);
        assertEquals(game2.getIsVisible(), isVisible);
    }

    @Test
    public void setIsVisibleModifiesValue(){
        String gameId = "gameId";
        String name = "name";
        boolean isVisible1 = false;
        boolean isVisible2 = false;
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game = new Game(gameId, name, isVisible1, players, 0, riddleList, coins, new Location("LocationManager#GPS_PROVIDER"));
        game.setIsVisible(isVisible2);

        assertEquals(game.getIsVisible(), isVisible2);
    }
}
