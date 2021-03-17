package sdp.moneyrun;

import org.junit.Rule;
import org.junit.Test;

public class DatabaseProxyTest {
    @Test
    public void getPlayerFromDatabase(){
        System.out.println("Got there");

        Player player = new Player(1234);
        player.setAddress("FooBarr");
        player.setName("John Doe");
        System.out.println("Got there too");

        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        System.out.println(db.getPlayer(player.getPlayerId()));
    }
}
