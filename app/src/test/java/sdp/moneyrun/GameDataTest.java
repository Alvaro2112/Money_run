package sdp.moneyrun;

import android.location.Location;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class GameDataTest {

    GameData getTestData() {
        String name = "game";
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "James", "Lausanne", 3, 4));
        players.add(new Player(2, "Potter", "Nyon", 3, 4));
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("?", "a", "b", "c", "d", "e"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(1, 2));
        int maxPlayers = 4;
        Location targetLocation = new Location("");//provider name is unnecessary
        return new GameData(name, players, maxPlayers, riddles, targetLocation, coins);
    }


    @Test
    public void constructorFailsOnNullArg(){
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "James", "Lausanne", 3, 4));
        assertThrows(IllegalArgumentException.class, ()->{
            GameData g = new GameData("name", players, 3, new ArrayList<Riddle>(), null, null);
        });
    }

    @Test
    public void constructorFailsOnEmptyPlayerList(){
        assertThrows(IllegalArgumentException.class, ()->{
            GameData g = new GameData("name", new ArrayList<Player>(), 3, new ArrayList<Riddle>(), new Location(""), new ArrayList<Coin>());
        });
    }

    @Test
    public void constructorFailsOnNegativeMaxPlayers(){
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "James", "Lausanne", 3, 4));
        assertThrows(IllegalArgumentException.class, ()->{
            GameData g = new GameData("name", players, -4, new ArrayList<Riddle>(), new Location(""), new ArrayList<Coin>());
        });
    }

    @Test
    public void copyConstructorFailsOnNullArg(){
        assertThrows(IllegalArgumentException.class, ()->{
            GameData g = new GameData(null);
        });
    }

    @Test
    public void getNameReturnsName(){
        assertEquals("game", getTestData().getName());
    }

    @Test
    public void getPlayersReturnsPlayers(){
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "James", "Lausanne", 3, 4));
        players.add(new Player(2, "Potter", "Nyon", 3, 4));
        assertEquals(players, getTestData().getPlayers());
    }

    @Test
    public void getMaxPlayersReturnsMaxPlayers(){
        int maxPlayers = 4;
        assertEquals(maxPlayers, getTestData().getMaxPlayerNumber());
    }

    @Test
    public void getRiddlesReturnsRiddles(){
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("?", "a", "b", "c", "d", "e"));
        assertEquals(riddles, getTestData().getRiddles());

    }

    @Test
    public void setPlayersFailsOnNullArg(){
        assertThrows(IllegalArgumentException.class, () -> {
            getTestData().setPlayers(null);
        });
    }

    @Test
    public void setPlayersFailsOnEmptyArg(){
        assertThrows(IllegalArgumentException.class, () -> {
            getTestData().setPlayers(new ArrayList<Player>());
        });
    }

    @Test
    public void addPlayerFailsOnNullArg(){
        assertThrows(IllegalArgumentException.class, () -> {
            getTestData().addPlayer(null);
        });
    }

    @Test
    public void addPlayerFailsOnFullPlayerList(){
        assertThrows(IllegalArgumentException.class, () -> {
            GameData g = getTestData();
            g.addPlayer(new Player(3, "Ron", "Lausanne", 3, 4));
            g.addPlayer(new Player(4, "Wisley", "Nyon", 3, 4));
            g.addPlayer(new Player(5, "Laura", "Nyon", 3, 4));
        });
    }

    @Test
    public void addPlayerAddsPlayerToList(){
        List<Player> expected = new ArrayList<>();
        expected.add(new Player(1, "James", "Lausanne", 3, 4));
        expected.add(new Player(2, "Potter", "Nyon", 3, 4));
        expected.add(new Player(3, "Ron", "Lausanne", 3, 4));
        GameData g = getTestData();
        g.addPlayer(new Player(3, "Ron", "Lausanne", 3, 4));
        assertEquals(expected, g.getPlayers());
    }


    @Test
    public void removePlayerFailsOnNullArg(){
        assertThrows(IllegalArgumentException.class, () -> {
            getTestData().removePlayer(null);
        });
    }

    @Test
    public void removePlayerFailsIfOneElementLEft(){
        GameData g = getTestData();
        g.removePlayer(new Player(1, "James", "Lausanne", 3, 4));
        assertThrows(IllegalArgumentException.class, () -> {
            g.removePlayer(new Player(2, "Potter", "Nyon", 3, 4));
        });
    }

    @Test
    public void removePlayerRemovesPlayerFromList(){
        List<Player> expected = new ArrayList<>();
        expected.add(new Player(1, "James", "Lausanne", 3, 4));
        GameData g = getTestData();
        g.removePlayer(new Player(2, "Potter", "Nyon", 3, 4));
        assertEquals(expected, g.getPlayers());
    }


    @Test
    public void setPlayersFailsWhenHostNotPresent(){
        //TODO
    }
}
