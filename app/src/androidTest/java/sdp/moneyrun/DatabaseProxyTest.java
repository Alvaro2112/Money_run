package sdp.moneyrun;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseProxyTest {
    @Test
    public void getPlayerFromDatabase() throws Throwable {

        final Player player = new Player(1236);
        player.setAddress("FooBarr");
        player.setName("Johann");
        final DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Task<DataSnapshot> testTask = db.getPlayerTask(player.getPlayerId());
      //  Thread.sleep(1000);
        testTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                   assert( player.equals(db.getPlayerFromTask(testTask)));
                }else{
                    assert (false);
                }
            }
        });
        while(!testTask.isComplete()){
            System.out.println("false");
        }
    }

}
