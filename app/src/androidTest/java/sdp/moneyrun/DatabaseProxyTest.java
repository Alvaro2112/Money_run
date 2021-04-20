package sdp.moneyrun;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.player.Player;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DatabaseProxyTest {
    private  long ASYNC_CALL_TIMEOUT = 5L;
    @Test
    public void getPlayerFromDatabase() throws Throwable {

        final Player player = new Player(1236, "Johann", "FooBarr", 0, 0,0);
        final DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Task<DataSnapshot> testTask = db.getPlayerTask(player.getPlayerId());
      //  Thread.sleep(1000);
        testTask.addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
               assert( player.equals(db.getPlayerFromTask(testTask)));
            }else{
                assert (false);
            }
        });
        while(!testTask.isComplete()){
            System.out.println("false");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addPlayerListenerThrowsExceptionOnNullPlayer() {
        DatabaseProxy db = new DatabaseProxy();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db.addPlayerListener(null, listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addPlayerListenerThrowsExceptionOnNullListener() {
        DatabaseProxy db = new DatabaseProxy();
        Player player = new Player(1, "a","b",0,0,0);
        db.addPlayerListener(player, null);
    }


    @Test
    public void addPlayerListenerCorrectlyUpdates(){
        CountDownLatch added = new CountDownLatch(1);
        CountDownLatch received = new CountDownLatch(1);
        Player player = new Player(564123, "Johann", "FooBarr", 0, 0,0);
        final DatabaseProxy db = new DatabaseProxy();

        DatabaseReference dataB = FirebaseDatabase.getInstance().getReference("players");
        dataB.setValue(player).addOnCompleteListener(task -> added.countDown());

        String newName = "Simon";
        try {
            added.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(added.getCount(), is(0L));
        } catch (InterruptedException e) {
            assert(false);
        }
        ValueEventListener listener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player p = snapshot.getValue(Player.class);

                player.setName(p.getName());
                received.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addPlayerListener(player, listener);
        Player p = new Player(564123,newName,"FooBarr",0,0,0);
        db.putPlayer(p);
        try {
            received.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(received.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            assert(false);
//        }
        assertThat(player.getName(),is(newName));
        db.removePlayerListener(player, listener);

    }

    @Test(expected = IllegalArgumentException.class)
    public void removePlayerListenerThrowsExceptionOnNullPlayer() {
        DatabaseProxy db = new DatabaseProxy();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db.removePlayerListener(null, listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removePlayerListenerThrowsExceptionOnNullListener() {
        DatabaseProxy db = new DatabaseProxy();
        Player player = new Player(1, "a","b",0,0,0);
        db.removePlayerListener(player, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putPlayerWithNullInstanceThrowsException(){
        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removePlayerWithNullInstanceThrowsException(){
        DatabaseProxy db = new DatabaseProxy();
        db.removePlayer(null);
    }
}
