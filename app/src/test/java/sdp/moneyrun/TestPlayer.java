package sdp.moneyrun;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

public class TestPlayer {
    Player player = new Player(1);
    @Test
    public void testNumberId(){
        assertEquals(1, player.getPlayerId());
    }
    @Test
    public void testNameSetup(){
        player.setName("Bob");
        assertEquals(player.getName(), "Bob");
    }
    @Test
    public void testAddressSetup(){
        player.setAddress("New York");
        assertEquals("New York", player.getAddress());
    }
    @Test
    public void testNumberOfDiedGames(){
        player.updateDiedGames();
        assertEquals(1,player.getNumberOfDiedGames());
    }
    @Test
    public void testPlayedGames(){
        player.updatePlayedGames();
        assertEquals(1,player.getNumberOfPlayedGames());
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionThrownOnSetName(){
        Player p = new Player(3);
        p.getName();
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionThrownOnSetAddress(){
        Player p = new Player(3);
        p.getAddress();
    }
    @Test
    public void constructorsReturnSamePlayer(){
        int playerId = 123;
        int nbrOfDiedGames = 0;
        int nbrOfPlayedGames = 0;
        String address = "Foooooooo";
        String name = "BaaaRRfF";
        Player player1 = new Player(playerId);
        player1.setAddress(address);
        player1.setName(name);
        Player player2 = new Player(playerId, name, address, nbrOfDiedGames, nbrOfPlayedGames);
        assertEquals(player1, player2);
    }

    @Test
    public void hashOutputsExpectedValue(){
        int playerId = 123;
        int nbrOfDiedGames = 0;
        int nbrOfPlayedGames = 0;
        String address = "Foooooooo";
        String name = "BaaaRRfF";
        Player player = new Player(playerId, name, address, nbrOfDiedGames, nbrOfPlayedGames);
        assertEquals(player.hashCode(),
                Objects.hash(playerId, name, address, nbrOfPlayedGames, nbrOfDiedGames));
    }

    @Test
    public void equalsReturnFalseForNullObject(){
        int playerId = 123;
        int nbrOfDiedGames = 0;
        int nbrOfPlayedGames = 0;
        String address = "Foooooooo";
        String name = "BaaaRRfF";
        Player player1 = new Player(playerId);
        player1.setAddress(address);
        player1.setName(name);
        assert (!player1.equals(null));
    }




}