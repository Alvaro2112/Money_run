package sdp.moneyrun;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class TestPlayer {
    Player player = new Player(1,"Bob", "New York",0,0);

    @Test
    public void testNumberId() {
        assertEquals(1, player.getPlayerId());
    }

    @Test
    public void testNameSetup() {
        assertEquals(player.getName(), "Bob");
    }

    @Test
    public void testAddressSetup() {
        assertEquals("New York", player.getAddress());
    }

    @Test
    public void testNumberOfDiedGames() {
        player.updateDiedGames();
        assertEquals(1, player.getNumberOfDiedGames());
    }

    @Test
    public void testPlayedGames() {
        player.updatePlayedGames();
        assertEquals(1, player.getNumberOfPlayedGames());
    }

    @Test
    public void testAskPlayerReturnsEmptyString(){
        assertEquals("",player.ask(""));
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
        Player player1 = new Player(playerId,name,address,0,0);

        assert (!player1.equals(null));
    }




}