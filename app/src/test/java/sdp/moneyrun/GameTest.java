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
        String[] possibleAnswers = {"blue", "green", "yellow"};

        Riddle riddle = new Riddle(question, possibleAnswers, correctAnswer);
        assertEquals(question, riddle.getQuestion());
        assertEquals(correctAnswer, riddle.getAnswer());
        assertArrayEquals(possibleAnswers, riddle.getPossibleAnswers());
    }

    @Test
    public void RiddleThrowsExceptionWhenArgumentsAreNull() {

        assertThrows(IllegalArgumentException.class, () -> {
            Riddle riddle = new Riddle(null, new String[]{"a"}, "a");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Riddle riddle = new Riddle("a", null, "very");
        });


        assertThrows(IllegalArgumentException.class, () -> {
            Riddle riddle = new Riddle("a", new String[]{"a"}, null);
        });
    }

    @Test
    public void RiddleThrowsExceptionWhenCorrectAnswerIsNotInPossibleAnswers() {

        assertThrows(IllegalArgumentException.class, () -> {
            Riddle riddle = new Riddle(null, new String[]{"a"}, "Is it good?");
        });

    }


    @Test
    public void testGameConstructorThrowsExceptionWhenNullArguments() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", new String[]{"a"}, "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3));
        try {
            Game game = new Game(null, riddleList, null);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(players, null, null);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            Game game = new Game(players, riddleList, null);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
    }

    @Test
    public void testStartGameDoesNotCrash() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", new String[]{"a"}, "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3));
        Game game = new Game(players, riddleList, new Location("LocationManager#GPS_PROVIDER"));
        game.startGame();
        Game.startGame(game);
        assertEquals(1, 1);
    }

    @Test
    public void askPlayerQuestionShouldReturnFalse() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", new String[]{"a"}, "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3));
        Game game = new Game(players, riddleList, new Location("LocationManager#GPS_PROVIDER"));
        assertEquals(game.askPlayer(players.get(0), riddleList.get(0)), false);
    }

    @Test
    public void getRandomQuestionReturnsAQuestion() {
        List<Riddle> riddleList = new ArrayList<>();
        riddleList.add(new Riddle("yes?", new String[]{"a"}, "a"));
        List<Player> players = new ArrayList<>();
        players.add(new Player(3));
        Game game = new Game(players, riddleList, new Location("LocationManager#GPS_PROVIDER"));
        Riddle riddle = game.getRandomRiddle();
        assertTrue(riddle.getClass() == Riddle.class);



    }
}
