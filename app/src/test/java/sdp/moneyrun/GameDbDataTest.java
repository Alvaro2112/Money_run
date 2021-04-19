package sdp.moneyrun;

import android.location.Location;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameDbDataTest {

    GameDbData getTestData() {
        String name = "game";
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "James", "Lausanne", 3, 4));
        players.add(new Player(2, "Potter", "Nyon", 3, 4));
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("?", "a", "b", "c", "d", "e"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(1, 2, 3));
        int maxPlayers = 4;
        Location targetLocation = new Location("");//provider name is unnecessary
        return new GameDbData(name, players, maxPlayers, targetLocation);
    }

    @Mock
    Location mockLocation;


    @Test
    public void constructorFailsOnNullArg(){
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "James", "Lausanne", 3, 4));
        assertThrows(IllegalArgumentException.class, ()->{
            GameDbData g = new GameDbData("name", players, 3, null);
        });
    }

    @Test
    public void constructorFailsOnEmptyPlayerList(){
        assertThrows(IllegalArgumentException.class, ()->{
            GameDbData g = new GameDbData("name", new ArrayList<Player>(), 3, new Location(""));
        });
    }

    @Test
    public void constructorFailsOnNegativeMaxPlayers(){
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "James", "Lausanne", 3, 4));
        assertThrows(IllegalArgumentException.class, ()->{
            GameDbData g = new GameDbData("name", players, -4, new Location(""));
        });
    }

    @Test
    public void copyConstructorFailsOnNullArg(){
        assertThrows(IllegalArgumentException.class, ()->{
            GameDbData g = new GameDbData(null);
        });
    }

    @Test
    public void copyConstructorWorksNormally(){
        GameDbData g = getTestData();
        GameDbData copy = new GameDbData(g);
        assertEquals(g.getName(), copy.getName());
        assertEquals(g.getPlayers(), copy.getPlayers());
        assertEquals(g.getMaxPlayerNumber(), g.getMaxPlayerNumber());
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
    public void setPlayersWorksNormally(){
        GameDbData g = getTestData();
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "Tony Stark", "Malibu", 3, 4));
        players.add(new Player(2, "Pepper potts", "Stark tower", 3, 4));
        g.setPlayers(players);
        assertEquals(players, g.getPlayers());
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
            GameDbData g = getTestData();
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
        GameDbData g = getTestData();
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
        GameDbData g = getTestData();
        g.removePlayer(new Player(1, "James", "Lausanne", 3, 4));
        assertThrows(IllegalArgumentException.class, () -> {
            g.removePlayer(new Player(2, "Potter", "Nyon", 3, 4));
        });
    }

    @Test
    public void removePlayerRemovesPlayerFromList(){
        List<Player> expected = new ArrayList<>();
        expected.add(new Player(1, "James", "Lausanne", 3, 4));
        GameDbData g = getTestData();
        g.removePlayer(new Player(2, "Potter", "Nyon", 3, 4));
        assertEquals(expected, g.getPlayers());
    }

    @Test
    public void equalsWorksAsIntended(){
        String name = "game";
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "James", "Lausanne", 3, 4));
        players.add(new Player(2, "Potter", "Nyon", 3, 4));
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("?", "a", "b", "c", "d", "e"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(1, 2, 3));
        int maxPlayers = 4;
        mockLocation = Mockito.mock(Location.class);
        when(mockLocation.getLatitude()).thenReturn(4.0);
        when(mockLocation.getLongitude()).thenReturn(3.0);

        GameDbData g = new GameDbData(name, players, maxPlayers, mockLocation);
        GameDbData sameRef = g;
        GameDbData sameContent = new GameDbData(g);

        assertTrue(g.equals(sameRef));
        assertFalse(g.equals(null));
        assertTrue(g.equals(sameContent));

    }


    @Test
    public void setPlayersFailsWhenHostNotPresent(){
        //TODO
    }
}
