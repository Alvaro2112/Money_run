package sdp.moneyrun;

import android.location.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class GameDataTest {



    @Test
    public void constructorFailsOnNullArg(){
        assertThrows(IllegalArgumentException.class, ()->{
            GameData g = new GameData("name", new ArrayList<Player>(), 3, new ArrayList<Riddle>(), null);
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
        String name = "game";
        List<Player> players = new ArrayList<>(List.of(new Player(1), new Player(2)));
        List<Riddle> riddles = new ArrayList<>(List.of(new Riddle("1+1","2")));
        int maxPlayers = 4;
        Location targetLocation = new Location("");//provider name is unnecessary
        GameData testData = new GameData(name, players, maxPlayers, riddles, targetLocation);

        assertEquals("game", testData.getName());

    }

    @Test
    public void getPlayersReturnsPlayers(){
        String name = "game";
        List<Player> players = new ArrayList<>(List.of(new Player(1), new Player(2)));
        List<Riddle> riddles = new ArrayList<>(List.of(new Riddle("1+1","2")));
        int maxPlayers = 4;
        Location targetLocation = new Location("");//provider name is unnecessary
        GameData testData = new GameData(name, players, maxPlayers, riddles, targetLocation);

        assertEquals(players, testData.getPlayers());

    }

    @Test
    public void getLocationReturnsLocation(){
        String name = "game";
        List<Player> players = new ArrayList<>(List.of(new Player(1), new Player(2)));
        List<Riddle> riddles = new ArrayList<>(List.of(new Riddle("1+1","2")));
        int maxPlayers = 4;
        Location targetLocation = new Location("");//provider name is unnecessary
        GameData testData = new GameData(name, players, maxPlayers, riddles, targetLocation);
        assertEquals(targetLocation, testData.getStartLocation());
    }

    @Test
    public void getMaxPlayersReturnsMaxPlayers(){
        String name = "game";
        List<Player> players = new ArrayList<>(List.of(new Player(1), new Player(2)));
        List<Riddle> riddles = new ArrayList<>(List.of(new Riddle("1+1","2")));
        int maxPlayers = 4;
        Location targetLocation = new Location("");//provider name is unnecessary
        GameData testData = new GameData(name, players, maxPlayers, riddles, targetLocation);
        assertEquals(maxPlayers, testData.getMaxPlayerNumber());

    }

    @Test
    public void getRiddlesReturnsRiddles(){
        String name = "game";
        List<Player> players = new ArrayList<>(List.of(new Player(1), new Player(2)));
        List<Riddle> riddles = new ArrayList<>(List.of(new Riddle("1+1","2")));
        int maxPlayers = 4;
        Location targetLocation = new Location("");//provider name is unnecessary
        GameData testData = new GameData(name, players, maxPlayers, riddles, targetLocation);
        assertEquals(riddles, testData.getRiddles());

    }

    @Test
    public void setPlayersFailsOnNullArg(){
        String name = "game";
        List<Player> players = new ArrayList<>(List.of(new Player(1), new Player(2)));
        List<Riddle> riddles = new ArrayList<>(List.of(new Riddle("1+1","2")));
        int maxPlayers = 4;
        Location targetLocation = new Location("");//provider name is unnecessary
        GameData testData = new GameData(name, players, maxPlayers, riddles, targetLocation);
        assertThrows(IllegalArgumentException.class, () -> {
            testData.setPlayers(null);
        });
    }

}
