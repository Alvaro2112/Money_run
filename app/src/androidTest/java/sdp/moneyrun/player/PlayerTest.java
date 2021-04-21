package sdp.moneyrun.player;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import sdp.moneyrun.database.DatabaseProxy;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PlayerTest {
    private  long ASYNC_CALL_TIMEOUT = 10L;

    @Test
    public void setAddressWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        String newAddress = "New Address";
        int id = 1234567891;
        Player player = new Player(id, name, address,0,0 ,0);
        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            assert(false);
        }
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player p = snapshot.getValue(Player.class);
                //player.setAddress(p.getAddress());
                assertThat(p.getAddress(), is(newAddress));
                updated.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addPlayerListener(player,listener);
        player.setAddress(newAddress, true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removePlayerListener(player, listener);
    }

    @Test
    public void setNameWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        String newName = "New Address";
        int id = 1234567892;
        Player player = new Player(id, name, address,0,0,0 );
        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            assert(false);
        }
        ValueEventListener listener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player p = snapshot.getValue(Player.class);
                assertThat(p.getName(), is(newName));
                updated.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addPlayerListener(player, listener);

        player.setName(newName, true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removePlayerListener(player,listener);
    }

    @Test
    public void setPlayedGamesWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        int newPlayedGames = 75;
        int id = 1234567893;
        Player player = new Player(id, name, address,0,0,0 );
        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            assert(false);
        }

        ValueEventListener listener =  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player p = snapshot.getValue(Player.class);
                assertThat(p.getNumberOfPlayedGames(), is(newPlayedGames));
                updated.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addPlayerListener(player, listener);

        player.setNumberOfPlayedGames(newPlayedGames, true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removePlayerListener(player, listener);
    }

    @Test
    public void setDiedGamesWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        int newDiedGames = 75;
        int id = 1234567894;
        Player player = new Player(id, name, address,0,0,0 );
        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            assert(false);
        }

        ValueEventListener listener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player p = snapshot.getValue(Player.class);
                assertThat(p.getNumberOfDiedGames(), is(newDiedGames));
                updated.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addPlayerListener(player, listener);

        player.setNumberOfDiedGames(newDiedGames, true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removePlayerListener(player,listener);
    }

    @Test
    public void updatePlayedGamesWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        int id = 1234567895;
        Player player = new Player(id, name, address,0,0 ,0);
        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            assert(false);
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player p = snapshot.getValue(Player.class);
                assertThat(p.getNumberOfPlayedGames(), is(1));
                updated.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addPlayerListener(player, listener);
        player.updatePlayedGames(true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removePlayerListener(player,listener);
    }

    @Test
    public void updateDiedGamesWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        int id = 1234567896;
        Player player = new Player(id, name, address,0,0,0 );
        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            assert(false);
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player p = snapshot.getValue(Player.class);
                assertThat(p.getNumberOfDiedGames(), is(1));
                updated.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addPlayerListener(player, listener);
        player.updateDiedGames(true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removePlayerListener(player,listener);
    }

    @Test
    public void setScoreWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        int score = 75;
        int id = 1234567897;
        Player player = new Player(id, name, address,0,0,0 );
        DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            assert(false);
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player p = snapshot.getValue(Player.class);
                assertThat(p.getScore(), is(score));
                updated.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addPlayerListener(player, listener);
        player.setScore(score, true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removePlayerListener(player,listener);
    }



}