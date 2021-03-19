package sdp.moneyrun;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseProxyTest {
    @Test
    public void getPlayerFromDatabase(){

        Player player = new Player(1236);
        player.setAddress("FooBarr");
        player.setName("John Doe");
        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Task<DataSnapshot> task = db.getRawPlayerData(player.getPlayerId());
        Player player2 = db.getPlayer(player.getPlayerId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(DatabaseProxyTest.class.getSimpleName() ,task.getResult().toString());
        System.out.println("to String : " + task.getResult().toString());
        System.out.println("String. : " + String.valueOf(task.getResult().getValue()));
        assertEquals(player,player2);
    }
}
