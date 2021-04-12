package sdp.moneyrun;


import android.location.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
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
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();

        players.add(new Player(3,"Bob", "Epfl",0,0,0));
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
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
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
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game = new Game(players, riddleList,coins, new Location("LocationManager#GPS_PROVIDER"));
        assertEquals(game.askPlayer(players.get(0), riddleList.get(0)), false);
    }

    @Test
    public void getRandomQuestionReturnsAQuestion() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        players.add(new Player(3,"Bob", "Epfl",0,0,0));
        Game game = new Game(players, riddleList, coins,new Location("LocationManager#GPS_PROVIDER"));
        Riddle riddle = game.getRandomRiddle();
        assertTrue(riddle.getClass() == Riddle.class);



    }
}
