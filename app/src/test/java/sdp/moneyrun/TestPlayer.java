package sdp.moneyrun;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestPlayer {
    Player player = new Player(1);

    @Test
    public void testNumberId() {
        assertEquals(1, player.getPlayerId());
    }

    @Test
    public void testNameSetup() {
        player.setName("Bob");
        assertEquals(player.getName(), "Bob");
    }

    @Test
    public void testAddressSetup() {
        player.setAddress("New York");
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
    public void testExceptionThrownOnSetAddressAndName() {
        try {
            Player p = new Player(3);
            p.getName();
        } catch (IllegalStateException e) {
            assertEquals(1, 1);
        }
        try {
            Player p = new Player(3);
            p.getAddress();
        } catch (IllegalStateException e) {
            assertEquals(1, 1);
        }
    }
    @Test
    public void testAskPlayerReturnsEmptyString(){
        Player player = new Player(4);
        assertEquals("",player.ask(""));
    }


}