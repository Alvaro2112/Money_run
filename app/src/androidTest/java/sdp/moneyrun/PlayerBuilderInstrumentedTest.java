package sdp.moneyrun;

import org.junit.Test;

import java.util.Random;

public class PlayerBuilderInstrumentedTest {

    @Test(expected = IllegalArgumentException.class)
    public void setAddressWithNullArgumentThrowsArgumentException(){
        PlayerBuilder b = new PlayerBuilder();
        b.setAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNameWithNullArgumentThrowsArgumentException(){
        PlayerBuilder b = new PlayerBuilder();
        b.setName(null);
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompleteAddressFieldThrowsStateException(){
        PlayerBuilder b = new PlayerBuilder();
        b.setName("Stuff");
        b.setPlayerId(7);
        b.build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompleteNameFieldThrowsStateException(){
        PlayerBuilder b = new PlayerBuilder();
        b.setAddress("Stuff");
        b.setPlayerId(7);
        b.build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompletePlayerIdFieldThrowsStateException(){
        PlayerBuilder b = new PlayerBuilder();
        b.setName("Stuff");
        b.setAddress("Sthg");
        b.build();
    }

    @Test
    public void buildWithAppropriateProcessReturnsCorrectPlayer(){
        Random r = new Random();
        PlayerBuilder b = new PlayerBuilder();
        String address = "Something";
        String name = "Other stuff";
        int playerId = r.nextInt();
        int numberOfDiedGames = r.nextInt();
        int numberOfPlayedGames = r.nextInt();
        Player player = new Player(playerId, name, address, numberOfDiedGames, numberOfPlayedGames);
        b.setPlayerId(playerId);
        b.setName(name);
        b.setAddress(address);
        b.setNumberOfDiedGames(numberOfDiedGames);
        b.setNumberOfPlayedGames(numberOfPlayedGames);
        assert(player.equals(b.build()));
    }
}