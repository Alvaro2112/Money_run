package sdp.moneyrun;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CoinTest {

    @Test
    public void checkCoinGetLatitude() {
        Coin coin = new Coin(23.0,2.0);
        assertEquals(coin.getLatitude(),23.0,0.0001);
    }

    @Test
    public void checkCoinGetLongitude() {
        Coin coin = new Coin(23.0,2.0);
        assertEquals(coin.getLongitude(),2.0,0.0001);
    }


}
