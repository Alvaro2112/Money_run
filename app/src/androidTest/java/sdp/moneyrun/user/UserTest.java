package sdp.moneyrun.user;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.MainActivity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserTest {
    private final long ASYNC_CALL_TIMEOUT = 10L;

    @BeforeClass
    public static void setPersistence(){
        if(!MainActivity.calledAlready){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
        FirebaseDatabase.getInstance().goOffline();
    }


    @Test
    public void setAddressWithDBUpdateWorks(){
        FirebaseDatabase.getInstance().goOnline();
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Someeewhere";
        String newAddress = "New Address";
        String id = "1234567891";
        User player = new User(id, name, address,0 ,0,0);
        UserDatabaseProxy db = new UserDatabaseProxy();CountDownLatch added = new CountDownLatch(1);
        db.putUser(player, task -> added.countDown());
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User p = snapshot.getValue(User.class);
                //player.setAddress(p.getAddress());
                if(p.getAddress().equals(newAddress)) {
                    assertThat(p.getAddress(), is(newAddress));
                    updated.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addUserListener(player,listener);
        player.setAddress(newAddress, true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removeUserListener(player, listener);
        FirebaseDatabase.getInstance().goOnline();
    }

    @Test
    public void setNameWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        String newName = "New Address";
        String id = "1234567892";

        User player = new User(id, name, address,0,0 ,0);
        UserDatabaseProxy db = new UserDatabaseProxy();
        db.putUser(player);

        ValueEventListener listener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Player p = snapshot.getValue(Player.class);
                if (p.getName().equals(newName)) {
                    assertThat(p.getName(), is(newName));
                    updated.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addUserListener(player, listener);

        player.setName(newName, true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removeUserListener(player,listener);
    }

    @Test
    public void setPlayedGamesWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        int newPlayedGames = 75;
        String id = "1234567893";
        User player = new User(id, name, address,0,0,0 );
        User player2 = new User(id, name, address,0,0,0 );


        UserDatabaseProxy db = new UserDatabaseProxy();
        db.putUser(player);

        ValueEventListener listener =  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User p = snapshot.getValue(User.class);
                if (p.getNumberOfPlayedGames() == 75) {
                    updated.countDown();
                }
                player2.setNumberOfPlayedGames(newPlayedGames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addUserListener(player, listener);

        player.setNumberOfPlayedGames(newPlayedGames, true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        assertThat(player2.getNumberOfPlayedGames(), is(newPlayedGames));

        db.removeUserListener(player, listener);
    }

    @Test
    public void setDiedGamesWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        int newDiedGames = 75;
        String id = "1234567894";

        User player = new User(id, name, address,0,0,0 );
        UserDatabaseProxy db = new UserDatabaseProxy();
        db.putUser(player);
        ValueEventListener listener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User p = snapshot.getValue(User.class);
                if(p.getNumberOfDiedGames() == newDiedGames) {
                    assertThat(p.getNumberOfDiedGames(), is(newDiedGames));
                    updated.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addUserListener(player, listener);

        player.setNumberOfDiedGames(newDiedGames, true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removeUserListener(player,listener);
    }

    @Test
    public void updatePlayedGamesWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        String id = "1234567895";

        User player = new User(id, name, address,0 ,0,0);
        UserDatabaseProxy db = new UserDatabaseProxy();
        db.putUser(player);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User p = snapshot.getValue(User.class);
                if(p.getNumberOfPlayedGames()==1) {
                    assertThat(p.getNumberOfPlayedGames(), is(1));
                    updated.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addUserListener(player, listener);
        player.updatePlayedGames(true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removeUserListener(player,listener);
    }

    @Test
    public void updateDiedGamesWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        String id = "1234567896";
        User player = new User(id, name, address,0,0 ,0);
        UserDatabaseProxy db = new UserDatabaseProxy();
        db.putUser(player);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User p = snapshot.getValue(User.class);
                if(p.getNumberOfDiedGames() == 1) {
                    assertThat(p.getNumberOfDiedGames(), is(1));
                    updated.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };
        db.addUserListener(player, listener);
        player.updateDiedGames(true);
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removeUserListener(player,listener);
    }

    @Test
    public void setScoreWithDBUpdateWorks(){
        CountDownLatch updated = new CountDownLatch(1);
        String name = "John Doe";
        String address = "Somewhere";
        int score = 75;
        String id = "1234567897";

        User player = new User(id, name, address,0,0,0 );
        UserDatabaseProxy db = new UserDatabaseProxy();
        db.putUser(player);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User p = snapshot.getValue(User.class);
                if(p.getMaxScoreInGame() == score){
                    assertThat(p.getMaxScoreInGame(), is(score));
                    updated.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert(false);
            }
        };

        db.addUserListener(player, listener);
        player.setMaxScoreInGame(score, true);

        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertThat(updated.getCount(), is(0L));
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        db.removeUserListener(player,listener);
    }



}
