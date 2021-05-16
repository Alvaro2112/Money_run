package sdp.moneyrun;

import android.location.Location;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.database.GameDbData;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameDbDataTest {

    @Mock
    Location mockLocation;

    GameDbData getTestData() {
        String name = "game";
        List<Player> players = new ArrayList<>();
        Player host = new Player("1", "James", 4);
        players.add(host);
        players.add(new Player("2", "Potter", 4));
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("?", "a", "b", "c", "d", "e"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(1, 2, 3));
        int maxPlayers = 4;

        mockLocation = Mockito.mock(Location.class);

        return new GameDbData(name, host, players, maxPlayers, mockLocation, true, new ArrayList<>());
    }

    @Test
    public void constructorFailsOnNullArg() {
        List<Player> players = new ArrayList<>();
        List<Coin> coin = new ArrayList<>();

        Player host = new Player("1", "James", 4);
        assertThrows(IllegalArgumentException.class, () -> {
            GameDbData g = new GameDbData("name", host, players, 3, null, true, coin);
        });
    }

    @Test
    public void constructorFailsOnNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            List<Player> players = new ArrayList<>();
            Player host = new Player("1", "James", 4);
            GameDbData g = new GameDbData(null, host, new ArrayList<Player>(), 3, new Location(""), true, new ArrayList<>());

        });
    }

    @Test
    public void constructorFailsOnNullHost() {
        assertThrows(IllegalArgumentException.class, () -> {
            List<Player> players = new ArrayList<>();
            GameDbData g = new GameDbData("name", null, players, 3, new Location(""), true, new ArrayList<>());
        });
    }

    @Test
    public void constructorFailsOnNullPlayers() {
        assertThrows(IllegalArgumentException.class, () -> {
            Player host = new Player("1", "James", 4);
            GameDbData g = new GameDbData("name", host, null, 3, new Location(""), true, new ArrayList<>());
        });
    }

    @Test
    public void constructorFailsOnNegativeMaxPlayers() {
        List<Player> players = new ArrayList<>();
        Player host = new Player("1", "James", 4);
        assertThrows(IllegalArgumentException.class, () -> {
            GameDbData g = new GameDbData("name", host, players, -4, new Location(""), true, new ArrayList<>());
        });
    }

    @Test
    public void constructorForCloneThrowsExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> new GameDbData(null));
        new GameDbData();
    }

    @Test
    public void constructorForCloneWorks() {
        GameDbData gameData = getTestData();

        new GameDbData(gameData);
    }

    @Test
    public void getNameReturnsName() {
        assertEquals("game", getTestData().getName());
    }

    @Test
    public void getHostReturnsHost() {
        Player host = new Player("1", "James", 4);
        assertEquals(host, getTestData().getHost());
    }

    @Test
    public void getPlayersReturnsPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("1", "James", 4));
        players.add(new Player("2", "Potter", 4));
        assertEquals(players, getTestData().getPlayers());
    }

    @Test
    public void getMaxPlayersReturnsMaxPlayers() {
        int maxPlayers = 4;
        assertEquals(maxPlayers, getTestData().getMaxPlayerCount());
    }

    @Test
    public void getStartLocationWorks() {

        assertEquals(getTestData().getStartLocation(), mockLocation);
    }

    @Test
    public void getIsVisiblegetsIsVisible() {
        assertTrue(getTestData().getIsVisible());
    }

    @Test
    public void setIsVisibleSetsIsVisible() {
        GameDbData test = getTestData();
        test.setIsVisible(false);
        assertFalse(test.getIsVisible());
    }

    @Test
    public void setPlayersFailsOnNullArg() {
        assertThrows(IllegalArgumentException.class, () -> {
            getTestData().setPlayers(null);
        });
    }

    @Test
    public void setPlayersFailsOnEmptyArg() {
        assertThrows(IllegalArgumentException.class, () -> {
            getTestData().setPlayers(new ArrayList<Player>());
        });
    }

    @Test
    public void setPlayersWorksNormally() {
        GameDbData g = getTestData();
        List<Player> players = new ArrayList<>();
        players.add(new Player("1", "Tony Stark", 4));
        players.add(new Player("2", "Pepper potts", 4));
        g.setPlayers(players);
        assertEquals(players, g.getPlayers());
    }

    @Test
    public void addPlayerFailsOnNullArg() {
        assertThrows(IllegalArgumentException.class, () -> {
            getTestData().addPlayer(null);
        });
    }

    @Test
    public void addPlayerFailsOnFullPlayerList() {
        assertThrows(IllegalArgumentException.class, () -> {
            GameDbData g = getTestData();
            g.addPlayer(new Player("3", "Ron", 4));
            g.addPlayer(new Player("4", "Wisley", 4));
            g.addPlayer(new Player("5", "Laura", 4));
        });
    }

    @Test
    public void addPlayerAddsPlayerToList() {
        List<Player> expected = new ArrayList<>();
        expected.add(new Player("1", "James", 4));
        expected.add(new Player("2", "Potter", 4));
        expected.add(new Player("3", "Ron", 4));
        GameDbData g = getTestData();
        g.addPlayer(new Player("3", "Ron", 4));
        assertEquals(expected, g.getPlayers());
    }


    @Test
    public void removePlayerFailsOnNullArg() {
        assertThrows(IllegalArgumentException.class, () -> {
            getTestData().removePlayer(null);
        });
    }

    @Test
    public void removePlayerFailsIfOneElementLEft() {
        GameDbData g = getTestData();
        g.removePlayer(g.getPlayers().get(0));
        assertThrows(IllegalArgumentException.class, () -> {
            g.removePlayer(new Player("2", "Potter", 4));
        });
    }

    @Test
    public void removePlayerRemovesPlayerFromList() {
        List<Player> expected = new ArrayList<>();
        expected.add(new Player("1", "James", 4));
        GameDbData g = getTestData();
        g.removePlayer(new Player("2", "Potter", 4));
        assertEquals(expected, g.getPlayers());
    }

    @Test
    public void equalsWorksAsIntended() {
        String name = "game";
        List<Player> players = new ArrayList<>();
        Player host = new Player("1", "James", 4);
        players.add(host);
        players.add(new Player("2", "Potter", 4));
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("?", "a", "b", "c", "d", "e"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(1, 2, 3));
        int maxPlayers = 4;
        mockLocation = Mockito.mock(Location.class);
        when(mockLocation.getLatitude()).thenReturn(4.0);
        when(mockLocation.getLongitude()).thenReturn(3.0);

        GameDbData g = new GameDbData(name, host, players, maxPlayers, mockLocation, true, new ArrayList<>());
        GameDbData sameContent = new GameDbData(name, host, players, maxPlayers, mockLocation, true, new ArrayList<>());

        assertEquals(g, g);
        assertNotEquals(null, g);
        assertEquals(g, sameContent);
    }

    @Test
    public void hashThrowsNoException() {
        getTestData().hashCode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCoinsThrowsException() {
        GameDbData data = getTestData();
        data.setCoins(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCoinThrowsException() {
        GameDbData data = getTestData();
        data.setCoin(0, null);
    }

    @Test
    public void setCoinReturnFalseForIndexTooBig() {
        GameDbData data = getTestData();
        assert (!data.setCoin(0, new Coin()));
    }


    @Test
    public void isDeletedIsFalseWhenCreated() {
        GameDbData g = getTestData();
        assertEquals(false, g.getIsDeleted());
    }

    @Test
    public void setIsDeletedWorks() {
        GameDbData g = getTestData();
        g.setIsDeleted(true);
        assertEquals(true, g.getIsDeleted());
    }
}
