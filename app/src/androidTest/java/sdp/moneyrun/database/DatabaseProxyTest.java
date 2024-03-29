package sdp.moneyrun.database;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import sdp.moneyrun.database.game.GameDatabaseProxy;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.MainActivity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class DatabaseProxyTest {
    private final long ASYNC_CALL_TIMEOUT = 5L;
    private final String DATABASE_PLAYER = "players";

    @BeforeClass
    public static void setPersistence() {
        if (!MainActivity.calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
        FirebaseDatabase.getInstance().goOnline();
    }


    @Test
    public void getDatabaseWorks() {
        final PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        assertEquals(db.getReference().getDatabase(), db.getDatabase());
    }

    @Test(expected = IllegalArgumentException.class)
    public void putGameFailsCorrectly() {
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        gdp.putGame(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void addGameListenerFailsCorrectly() {
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        gdp.addGameListener(null, null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void putPlayerFailsCorrectly1() {
        Player player = new Player("564123", "Johann", 0);
        final PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        db.putPlayer(player, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putPlayerFailsCorrectly2() {
        final PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        CountDownLatch added = new CountDownLatch(1);
        OnCompleteListener addedListener = task -> added.countDown();
        db.putPlayer(null, addedListener);
    }

    @Test
    public void getPlayerFromDatabase() {
        final Player player = new Player("1236", "Johann", 0);
        final PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        CountDownLatch updated = new CountDownLatch(1);
        //adding to db
        CountDownLatch added = new CountDownLatch(1);
        OnCompleteListener listener = task -> added.countDown();
        db.putPlayer(player, listener);
        Task<DataSnapshot> testTask = db.getPlayerTask(player.getPlayerId());
        testTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                assert (player.equals(db.getPlayerFromTask(testTask)));
                updated.countDown();
            } else {
                System.out.println(task.getException());
                assert (false);
            }
        });
        while (!testTask.isComplete()) {
            System.out.println("false");
        }
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertEquals(0L, updated.getCount());
        } catch (InterruptedException e) {
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_PLAYER).child("1236").removeValue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addPlayerListenerThrowsExceptionOnNullPlayer() {
        PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db.addPlayerListener(null, listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addPlayerListenerThrowsExceptionOnNullListener() {
        PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        Player player = new Player("1", "a", 0);
        db.addPlayerListener(player, null);
    }

    @Test
    public void addPlayerListenerCorrectlyUpdates() {
        CountDownLatch received = new CountDownLatch(1);
        Player player = new Player("564123", "Johann", 0);
        final PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        DatabaseReference dataB = FirebaseDatabase.getInstance().getReference("players").child(String.valueOf(player.getPlayerId()));
        CountDownLatch added = new CountDownLatch(1);
        OnCompleteListener addedListener = task -> added.countDown();
        db.putPlayer(player, addedListener);
        db.putPlayer(player);
        String newName = "Simon";
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player p = snapshot.getValue(Player.class);
                player.setName(p.getName());
                if (p.getName().equals(newName)) {
                    received.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert (false);
            }
        };
        db.addPlayerListener(player, listener);
        String id = "564123";
        Player p = new Player(id, newName, 0);
        db.putPlayer(p);
        try {
            received.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(received.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert (false);
        }
        assertThat(player.getName(), is(newName));
        db.removePlayerListener(player, listener);
        FirebaseDatabase.getInstance().getReference().child(DATABASE_PLAYER).child(id).removeValue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void removePlayerListenerThrowsExceptionOnNullPlayer() {
        PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db.removePlayerListener(null, listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removePlayerListenerThrowsExceptionOnNullListener() {
        PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        Player player = new Player("1", "a", 0);
        db.removePlayerListener(player, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putPlayerWithNullInstanceThrowsException() {
        PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        db.putPlayer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removePlayerWithNullInstanceThrowsException() {
        PlayerDatabaseProxy db = new PlayerDatabaseProxy();
        db.removePlayer(null);
    }
}
