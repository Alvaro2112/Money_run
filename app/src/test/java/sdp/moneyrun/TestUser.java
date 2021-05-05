package sdp.moneyrun;

import org.junit.Test;

import java.util.Objects;
import java.util.Random;

import sdp.moneyrun.user.User;

import static org.junit.Assert.assertEquals;

public class TestUser {
    User user = new User(1,"Bob", "New York",0,0,0);

    @Test
    public void InstanceUserWorks(){
        User user1 = new User();
        User user2 = new User(0);
    }

    @Test
    public void testNumberId() {
        assertEquals(1, user.getUserId());
    }

    @Test
    public void testNameSetup() {
        assertEquals(user.getName(), "Bob");
    }

    @Test
    public void testAddressSetup() {
        assertEquals("New York", user.getAddress());
    }

    @Test
    public void testNumberOfDiedGames() {
        user.updateDiedGames();
        assertEquals(1, user.getNumberOfDiedGames());
    }

    @Test
    public void testPlayedGames() {
        user.updatePlayedGames();
        assertEquals(1, user.getNumberOfPlayedGames());
    }

    @Test
    public void testAskUserReturnsEmptyString(){
        assertEquals("",user.ask(""));
    }

    @Test
    public void hashOutputsExpectedValue(){
        int userId = 123;
        int nbrOfDiedGames = 0;
        int nbrOfPlayedGames = 0;
        String address = "Foooooooo";
        String name = "BaaaRRfF";
        User user = new User(userId, name, address, nbrOfDiedGames, nbrOfPlayedGames,0);
        assertEquals(user.hashCode(),
                Objects.hash(userId, name, address, nbrOfPlayedGames, nbrOfDiedGames));
    }

    @Test
    public void equalsReturnFalseForNullObject(){
        int userId = 123;
        int nbrOfDiedGames = 0;
        int nbrOfPlayedGames = 0;
        String address = "Foooooooo";
        String name = "BaaaRRfF";
        User user1 = new User(userId,name,address,0,0,0);

        assert (!user1.equals(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void userThrowsExceptionOnNullAddress(){
        Random r = new Random();
        int userId = r.nextInt();
        if (userId == 0 ) userId++;
        String address = null;
        String name = "Rodric";
        int played = r.nextInt();
        int died = r.nextInt();
        User p = new User(userId, name, address, died, played,0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void userThrowsExceptionOnEmptyAddress(){
        Random r = new Random();
        int userId = r.nextInt();
        if (userId == 0 ) userId++;
        String address = "";
        String name = "Rodric";
        int played = r.nextInt();
        int died = r.nextInt();
        User p = new User(userId, name, address, died, played,0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void userThrowsExceptionOnNullName(){
        Random r = new Random();
        int userId = r.nextInt();
        if (userId == 0 ) userId++;
        String address = "Foobar";
        String name = null;
        int played = r.nextInt();
        int died = r.nextInt();
        User p = new User(userId, name, address, died, played,0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void userThrowsExceptionOnEmptyName(){
        Random r = new Random();
        int userId = r.nextInt();
        if (userId == 0 ) userId++;
        String address = "Foobar";
        String name = "";
        int played = r.nextInt();
        int died = r.nextInt();
        User p = new User(userId, name, address, died, played,0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void userThrowsExceptionOn0Id(){
        Random r = new Random();
        int userId = 0;
        String address = "Foobar";
        String name = "Rodric";
        int played = r.nextInt();
        int died = r.nextInt();
        User p = new User(userId, name, address, died, played,0);
    }

    @Test
    public void setNumberDiedGamesUpdatesAttributeCorrectly(){
        User p = new User(123, "STUFF", "OTHER stuff", 0,0,0);
        int died = 43;
        p.setNumberOfDiedGames(died);
        assertEquals(died, p.getNumberOfDiedGames());
    }

    @Test
    public void setNumberPlayedGamesUpdatesAttributeCorrectly(){
        User p = new User(123, "STUFF", "OTHER stuff", 0,0,0);
        int played = 43;
        p.setNumberOfPlayedGames(played);
        assertEquals(played, p.getNumberOfPlayedGames());
    }

    @Test
    public void setAddressCorrectlyUpdatesAddress(){
        User p = new User(123, "STUFF", "OTHER stuff", 0,0,0);
        String address = "Foobar";
        p.setAddress(address);
        assertEquals(address, p.getAddress());

    }

    @Test
    public void setNameCorrectlyUpdatesName(){
        User p = new User(123, "STUFF", "OTHER stuff", 0,0,0);
        String address = "Foobar";
        p.setName(address);
        assertEquals(address, p.getName());

    }

    @Test
    public void setScoreCorrectlyUpdatesScore(){
        User p = new User(123, "STUFF", "OTHER stuff", 0,0,0);
        int score = 8;
        p.setMaxScoreInGame(score);
        assertEquals(score, p.getMaxScoreInGame());
    }
}