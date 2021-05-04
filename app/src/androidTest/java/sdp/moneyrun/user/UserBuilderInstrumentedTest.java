package sdp.moneyrun.user;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class UserBuilderInstrumentedTest {

    @Test(expected = IllegalArgumentException.class)
    public void setAddressWithNullArgumentThrowsArgumentException(){
        UserBuilder b = new UserBuilder();
        b.setAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNameWithNullArgumentThrowsArgumentException(){
        UserBuilder b = new UserBuilder();
        b.setName(null);
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompleteAddressFieldThrowsStateException(){
        UserBuilder b = new UserBuilder();
        b.setName("Stuff");
        b.setUserId(7);
        b.build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompleteNameFieldThrowsStateException(){
        UserBuilder b = new UserBuilder();
        b.setAddress("Stuff");
        b.setUserId(7);
        b.build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompletePlayerIdFieldThrowsStateException(){
        UserBuilder b = new UserBuilder();
        b.setName("Stuff");
        b.setAddress("Sthg");
        b.build();
    }

    @Test
    public void buildWithAppropriateProcessReturnsCorrectPlayer(){
        Random r = new Random();
        UserBuilder b = new UserBuilder();
        String address = "Something";
        String name = "Other stuff";
        int playerId = r.nextInt();
        int numberOfDiedGames = r.nextInt();
        int numberOfPlayedGames = r.nextInt();
        int score = r.nextInt();

        User player = new User(playerId, name, address, numberOfDiedGames, numberOfPlayedGames,score);
        b.setUserId(playerId);
        b.setName(name);
        b.setScore(score);
        b.setAddress(address);
        b.setNumberOfDiedGames(numberOfDiedGames);
        b.setNumberOfPlayedGames(numberOfPlayedGames);
        assert(player.equals(b.build()));
    }
}