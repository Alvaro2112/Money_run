package sdp.moneyrun.user;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class UserBuilderInstrumentedTest {

    @Test(expected = IllegalArgumentException.class)
    public void setAddressWithNullArgumentThrowsArgumentException() {
        UserBuilder b = new UserBuilder();
        b.setAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNameWithNullArgumentThrowsArgumentException() {
        UserBuilder b = new UserBuilder();
        b.setName(null);
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompleteAddressFieldThrowsStateException() {
        UserBuilder b = new UserBuilder();
        b.setName("Stuff");
        b.setUserId("7");
        b.build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompleteNameFieldThrowsStateException() {
        UserBuilder b = new UserBuilder();
        b.setAddress("Stuff");
        b.setUserId("7");
        b.build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompletePlayerIdFieldThrowsStateException() {
        UserBuilder b = new UserBuilder();
        b.setName("Stuff");
        b.setAddress("Sthg");
        b.build();
    }

    @Test
    public void buildWithAppropriateProcessReturnsCorrectPlayer() {
        Random r = new Random();
        UserBuilder b = new UserBuilder();
        String address = "Something";
        String name = "Other stuff";

        User player = new User("1", name, address, 2, 3, 4);
        b.setUserId("1");
        b.setName(name);
        b.setScore(4);
        b.setAddress(address);
        b.setNumberOfDiedGames(2);
        b.setNumberOfPlayedGames(3);
        assert (player.equals(b.build()));
    }
}