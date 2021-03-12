package sdp.moneyrun;

import org.junit.Rule;
import org.junit.Test;

public class DatabaseProxyTest {
    @Test
    public void getPlayerFromDatabase(){
        Player player = new Player(1234);
        player.setAddress("FooBarr");
        player.setName("John Doe");
        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        System.out.println(db.getPlayer(player.getPlayerId()));
    }
}
