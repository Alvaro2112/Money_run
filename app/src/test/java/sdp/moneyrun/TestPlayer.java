package sdp.moneyrun;

import org.junit.Test;

import java.util.Objects;
import java.util.Random;

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

    @Test(expected = IllegalArgumentException.class)
    public void playerThrowsExceptionOnNullAddress(){
        Random r = new Random();
        int playerId = r.nextInt();
        if (playerId == 0 ) playerId++;
        String address = null;
        String name = "Rodric";
        int played = r.nextInt();
        int died = r.nextInt();
        Player p = new Player(playerId, name, address, died, played);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playerThrowsExceptionOnEmptyAddress(){
        Random r = new Random();
        int playerId = r.nextInt();
        if (playerId == 0 ) playerId++;
        String address = "";
        String name = "Rodric";
        int played = r.nextInt();
        int died = r.nextInt();
        Player p = new Player(playerId, name, address, died, played);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playerThrowsExceptionOnNullName(){
        Random r = new Random();
        int playerId = r.nextInt();
        if (playerId == 0 ) playerId++;
        String address = "Foobar";
        String name = null;
        int played = r.nextInt();
        int died = r.nextInt();
        Player p = new Player(playerId, name, address, died, played);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playerThrowsExceptionOnEmptyName(){
        Random r = new Random();
        int playerId = r.nextInt();
        if (playerId == 0 ) playerId++;
        String address = "Foobar";
        String name = "";
        int played = r.nextInt();
        int died = r.nextInt();
        Player p = new Player(playerId, name, address, died, played);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playerThrowsExceptionOn0Id(){
        Random r = new Random();
        int playerId = 0;
        String address = "Foobar";
        String name = "Rodric";
        int played = r.nextInt();
        int died = r.nextInt();
        Player p = new Player(playerId, name, address, died, played);
    }

    @Test
    public void setNumberDiedGamesUpdatesAttributeCorrectly(){
        Player p = new Player(123, "STUFF", "OTHER stuff", 0,0);
        int died = 43;
        p.setNumberOfDiedGames(died);
        assertEquals(died, p.getNumberOfDiedGames());
    }

    @Test
    public void setNumberPlayedGamesUpdatesAttributeCorrectly(){
        Player p = new Player(123, "STUFF", "OTHER stuff", 0,0);
        int played = 43;
        p.setNumberOfPlayedGames(played);
        assertEquals(played, p.getNumberOfPlayedGames());
    }

    @Test
    public void setAddressCorrectlyUpdatesAddress(){
        Player p = new Player(123, "STUFF", "OTHER stuff", 0,0);
        String address = "Foobar";
        p.setAddress(address);
        assertEquals(address, p.getAddress());

    }

    @Test
    public void setNameCorrectlyUpdatesName(){
        Player p = new Player(123, "STUFF", "OTHER stuff", 0,0);
        String address = "Foobar";
        p.setName(address);
        assertEquals(address, p.getName());

    }

    @Test
    public void setScoreCorrectlyUpdatesScore(){
        Player p = new Player(123, "STUFF", "OTHER stuff", 0,0);
        int score = 8;
        p.setScore(score);
        assertEquals(score, p.getScore());

    }

}