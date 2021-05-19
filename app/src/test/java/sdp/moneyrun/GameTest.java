package sdp.moneyrun;


import android.location.Location;

import androidx.annotation.NonNull;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sdp.moneyrun.game.Game;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GameTest {

    @NonNull
    public Game getGame() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");

        return new Game(name, host, maxPlayerCount, riddles, coins, location, true);
    }

    @Test(expected = NullPointerException.class)
    public void setDatabaseVariableFailsCorrectly(){
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        Game game = new Game(name, host, new ArrayList<>(), maxPlayerCount, location, true, coins, 3, 5, 10);
        game.setDatabaseVariable("e", null);
    }

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
    public void gameConstructorDoesNotCrash() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        new Game(name, host, new ArrayList<>(), maxPlayerCount, location, true, coins, 3, 5, 10);
    }

    @Test
    public void hashCodeWorks() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        Game game = new Game(name, host, new ArrayList<>(), maxPlayerCount, location, true, coins, 3, 5, 10);
        assertEquals(game.hashCode(), Objects.hash(game.getGameDbData()));
    }

    @Test
    public void addAlreadyPresentPlayerDoesNothing() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        Player bob = new Player("3", "Bob", 0);
        Player john = new Player("3", "john", 0);

        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        ArrayList<Player> players = new ArrayList<>();
        players.add(bob);
        players.add(john);
        Game game = new Game(name, host, players, maxPlayerCount, location, true, coins, 3, 5, 10);
        game.addPlayer(john, true);
        assertEquals(game.getPlayers(), players);
    }

    @Test
    public void startedIsInitializedToFalse() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        Game game = new Game(name, host, new ArrayList<>(), maxPlayerCount, location, true, coins, 3, 5, 10);
        assertFalse(game.isStarted());
    }

    @Test
    public void getNumCoinsWorks() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        Game game = new Game(name, host, new ArrayList<>(), maxPlayerCount, location, true, coins, 3, 5, 10);
        assertEquals(game.getNumCoins(), 3, 0);
    }

    @Test
    public void getRadiusWorks() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        Game game = new Game(name, host, new ArrayList<>(), maxPlayerCount, location, true, coins, 3, 5, 10);
        assertEquals(game.getRadius(), 5, 0);
    }

    @Test
    public void getDurationWorks() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        Game game = new Game(name, host, new ArrayList<>(), maxPlayerCount, location, true, coins, 3, 5, 10);
        assertEquals(game.getDuration(), 10, 0);
    }


    @Test
    public void RiddleThrowsExceptionWhenArgumentsAreNull() {

        assertThrows(IllegalArgumentException.class, () -> new Riddle(null, "blue", "green", "yellow", "brown", "a"));

        assertThrows(IllegalArgumentException.class, () -> new Riddle("a", null, "green", "yellow", "brown", "very"));

        assertThrows(IllegalArgumentException.class, () -> new Riddle("a", "blue", null, "yellow", "brown", "very"));

        assertThrows(IllegalArgumentException.class, () -> new Riddle("a", "blue", "green", null, "brown", "very"));

        assertThrows(IllegalArgumentException.class, () -> new Riddle("a", "blue", "green", "yellow", null, "very"));

        assertThrows(IllegalArgumentException.class, () -> new Riddle("a", "blue", "green", "yellow", "brown", null));
    }



    @Test
    public void testGameConstructorThrowsExceptionWhenInvalidArguments() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Player> players = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();

        players.add(host);
        Location startLocation = new Location("");
        try {
            new Game(null, host, 3, riddles, coins, startLocation, true);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, null, 3, riddles, coins, startLocation, true);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, 3, null, coins, startLocation, true);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, 3, riddles, null, startLocation, true);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, 3, riddles, coins, null, true);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, -1, riddles, coins, startLocation, true);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        //
        try {
            new Game(null, host, 3, riddles, coins, startLocation, true, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, null, 3, riddles, coins, startLocation, true, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, 3, null, coins, startLocation, true, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, 3, riddles, null, startLocation, true, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, 3, riddles, coins, null, true, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, -1, riddles, coins, startLocation, true, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }

        try {
            new Game(name, host, 3, riddles, coins, startLocation, true, -1, 2, 2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, 1, riddles, coins, startLocation, true, 2, -1, 2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, 1, riddles, coins, startLocation, true, 2, 2, -1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }


        try {
            new Game(null, host, players, 3, startLocation, true, coins);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, null, players, 3, startLocation, true, coins);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, null, 3, startLocation, true, coins);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, players, 3, null, true, coins);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, players, -1, startLocation, true, coins);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, players, 1, startLocation, true, null);
            fail();
        } catch (IllegalArgumentException e) {
            assert (true);
        }
        ///
        try {
            new Game(null, host, players, 3, startLocation, true, coins, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, null, players, 3, startLocation, true, coins, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, null, 3, startLocation, true, coins, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, players, 3, null, true, coins, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, players, -1, startLocation, true, coins, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        try {
            new Game(name, host, players, 1, startLocation, true, null, 1, 1, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assert (true);
        }

        try {
            new Game(name, host, players, 1, startLocation, true, coins, -1, 2, 2);
            fail();
        } catch (IllegalArgumentException e) {
            assert (true);
        }

        try {
            new Game(name, host, players, 1, startLocation, true, coins, 2, -1, 2);
            fail();
        } catch (IllegalArgumentException e) {
            assert (true);
        }
        try {
            new Game(name, host, players, 1, startLocation, true, coins, 2, 2, -1);
            fail();
        } catch (IllegalArgumentException e) {
            assert (true);
        }


    }

    @Test
    public void getRandomQuestionReturnsAQuestion() {
        Game game = getGame();

        Riddle riddle = game.getRandomRiddle();
        assertSame(riddle.getClass(), Riddle.class);
    }

    @Test
    public void getRandomQuestionReturnsNullIfNoQuestion() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        List<Riddle> riddles = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        Location location = new Location("LocationManager#GPS_PROVIDER");

        Game game = new Game(name, host, 3, riddles, coins, location, true);

        assertNull(game.getRandomRiddle());
    }



    @Test
    public void getIdReturnsCorrectValue() {
        Game game = getGame();

        assertNull(game.getId());
    }

    @Test
    public void getNameGameReturnsCorrectValues() {
        Game game = getGame();

        assertEquals(game.getName(), "name");
    }

    @Test
    public void getHostReturnsCorrectValue() {
        Game game = getGame();
        Player host = new Player("3", "Bob", 0);

        assertEquals(game.getHost(), host);
    }

    @Test
    public void getPlayerCountReturnsCorrectValues() {
        Game game = getGame();

        assertEquals(game.getPlayerCount(), 1);
    }

    @Test
    public void getMaxPlayerCountReturnsCorrectValues() {
        Game game = getGame();

        assertEquals(game.getMaxPlayerCount(), 3);
    }

    @Test
    public void getCoinsReturnsCorrectValue() {
        Game game = getGame();
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));

        assertEquals(game.getCoins(), coins);
    }

    @Test
    public void getRiddlesReturnsCorrectValue() {
        Game game = getGame();
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));

        assertEquals(game.getRiddles(), riddles);
    }

    @Test
    public void getStartLocationReturnsCorrectValue() {
        Game game = getGame();

        assertNotNull(game.getStartLocation());
    }

    @Test
    public void getStartedWorks() {
        Game game = getGame();

        assertNotNull(game.getStarted());
    }

    @Test
    public void getIsVisibleReturnsCorrectValue() {
        Game game = getGame();

        assertTrue(game.getIsVisible());
    }

    @Test
    public void getHasBeenAddedReturnsCorrectValue() {
        Game game = getGame();

        assertFalse(game.getHasBeenAdded());
    }

    @Test
    public void setIdWorks() {
        Game game = getGame();
        game.setId("gameId");

        assertEquals(game.getId(), "gameId");
    }

    @Test
    public void setStartedWorks() {
        Game game = getGame();
        game.setStarted(true, true);
        assertEquals(true, game.getStarted());
    }


    @Test(expected = IllegalArgumentException.class)
    public void setIdFailsOnNullArgument() {
        Game game = getGame();
        game.setId(null);
    }

    @Test
    public void setHasBeenAddedWorks() {
        Game game = getGame();
        game.setHasBeenAdded(true);

        assertTrue(game.getHasBeenAdded());
    }
}
