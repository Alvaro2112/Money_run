package sdp.moneyrun.database;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PlayerDatabaseProxyTest {

    @Test(expected = IllegalArgumentException.class)
    public void getLeaderboardPlayersFailsCorrectly(){
        PlayerDatabaseProxy playerDatabaseProxy = new PlayerDatabaseProxy();
        playerDatabaseProxy.getLeaderboardPlayers(-1);
    }
}
