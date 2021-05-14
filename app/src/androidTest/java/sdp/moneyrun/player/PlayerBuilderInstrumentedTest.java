package sdp.moneyrun.player;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class PlayerBuilderInstrumentedTest {

    @Test(expected = IllegalArgumentException.class)
    public void setAddressWithNullArgumentThrowsArgumentException() {
        PlayerBuilder b = new PlayerBuilder();
        b.setAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNameWithNullArgumentThrowsArgumentException() {
        PlayerBuilder b = new PlayerBuilder();
        b.setName(null);
    }


    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompleteNameFieldThrowsStateException() {
        PlayerBuilder b = new PlayerBuilder();
        b.setAddress("Stuff");
        b.setPlayerId("7");
        b.build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithNonCompletePlayerIdFieldThrowsStateException() {
        PlayerBuilder b = new PlayerBuilder();
        b.setName("Stuff");
        b.setAddress("Sthg");
        b.build();
    }

    @Test
    public void buildWithAppropriateProcessReturnsCorrectPlayer() {
        Random r = new Random();
        PlayerBuilder b = new PlayerBuilder();
        String name = "Other stuff";
        String playerId = Integer.toString(r.nextInt());
        int score = r.nextInt();

        Player player = new Player(playerId, name, score);
        b.setPlayerId(playerId);
        b.setName(name);
        b.setScore(score);
        assert (player.equals(b.build()));
    }
}
