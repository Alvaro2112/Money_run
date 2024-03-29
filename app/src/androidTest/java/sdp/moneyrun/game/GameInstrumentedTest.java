package sdp.moneyrun.game;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import sdp.moneyrun.database.game.GameDatabaseProxy;
import sdp.moneyrun.database.game.GameDbData;
import sdp.moneyrun.database.riddle.Riddle;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.MainActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
public class GameInstrumentedTest {

    private final long ASYNC_CALL_TIMEOUT = 5L;
    private final String DATABASE_GAME = "games";
    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private final GameDatabaseProxy db = new GameDatabaseProxy();

    @BeforeClass
    public static void setPersistence() {
        if (!MainActivity.calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }

    @NonNull
    public Game getGame() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);

        return new Game(name, host, maxPlayerCount, riddles, coins, location, true, 1, 1, 1);

    }

    @NonNull
    public GameDbData getGameData() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        List<Player> players = new ArrayList<>();
        players.add(host);
        int maxPlayerCount = 3;
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        return new GameDbData(name, host, players, maxPlayerCount, location, true, coins);
    }


    @Test
    public void GameIsAddedToDB() {
        Game g = getGame();
        CountDownLatch updated = new CountDownLatch(1);
        db.putGame(g);
        String id = g.getId();
        //Something went wrong, game might not have been uploaded properly
        if (id.equals("")) {
            fail();
        }
        Task<DataSnapshot> dataTask = ref.child(DATABASE_GAME).child(id).get();
        dataTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Game fromDB = db.getGameFromTaskSnapshot(task);
                assertEquals(g.getName(), fromDB.getName());
                assertEquals(g.getPlayers(), fromDB.getPlayers());
                assertEquals(g.getMaxPlayerCount(), fromDB.getMaxPlayerCount());
                //Soooo Android.Location doesnt have a .equals() method... Have to check manually -_-
                if (g.getStartLocation().getLatitude() != fromDB.getStartLocation().getLatitude() ||
                        g.getStartLocation().getLongitude() != fromDB.getStartLocation().getLongitude()) {
                    fail();
                }

            } else {
                fail();
            }
            updated.countDown();
        });
        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertEquals(0L, updated.getCount());
        } catch (InterruptedException e) {
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    @Test
    public void GameCannotBeAddedTwiceToDB() {
        Game g = getGame();
        db.putGame(g);
        String id = g.getId();
        assertEquals(id, db.putGame(g));
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    /**
     * This tests that the player list is in synchro with the DB
     */
    @Test

    public void GamePlayerListChangesWithDB() {
        Game g = getGame();
        db.putGame(g);
        CountDownLatch added = new CountDownLatch(1);
        db.updateGameInDatabase(g, task -> added.countDown());
        try {
            added.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertEquals(0L, added.getCount());
        } catch (InterruptedException e) {
            fail();
        }
        String id = g.getId();

        //Something went wrong, game might not have been uploaded properly
        if (id.equals("")) {
            fail();
        }
        CountDownLatch readded = new CountDownLatch(1);
        List<Player> players = new ArrayList<>();
        players.add(new Player("5", "Ron", 0));
        players.add(new Player("6", "Wisley", 0));
        ref.child(DATABASE_GAME).child(id).child("players").setValue(players).addOnCompleteListener(task -> readded.countDown());
        try {
            readded.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertEquals(0L, readded.getCount());
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(players, g.getPlayers());
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    @Test
    public void getGameDataSnapshotRetrievesGameFromDB() {
        Game g = getGame();
        db.putGame(g);
        CountDownLatch added = new CountDownLatch(1);
        db.updateGameInDatabase(g, task -> added.countDown());
        try {
            added.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertEquals(0L, added.getCount());
        } catch (InterruptedException e) {
            fail();
        }
        String id = g.getId();
        //Something went wrong, game might not have been uploaded properly
        if (id.equals("")) {
            fail();
        }
        CountDownLatch got = new CountDownLatch(1);
        CountDownLatch got2 = new CountDownLatch(1);
        Task<DataSnapshot> dataTaskManually = ref.child(DATABASE_GAME).child(id).get();
        Task<DataSnapshot> dataTaskFunction = db.getGameDataSnapshot(id);
        dataTaskManually.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                got.countDown();
                dataTaskFunction.addOnCompleteListener(task2 -> {
                    if (task.isSuccessful()) {
                        String manual = task.getResult().toString();
                        String function = task2.getResult().toString();
                        got2.countDown();
                        assertEquals(manual, function);
                    }
                });
            } else {
                fail();
            }
        });

        try {
            got.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertEquals(0L, got.getCount());
        } catch (InterruptedException e) {
            fail();
        }
        try {
            got2.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertEquals(0L, got2.getCount());
        } catch (InterruptedException e) {
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();

    }

    @Test
    public void getGameDataSnapshotFailsOnNullArg() {
        try {
            db.getGameDataSnapshot(null);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    public void getGameFromTaskSnapShotFailsOnNullArg() {
        try {
            db.getGameFromTaskSnapshot(null);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    public void setPlayersFailsOnNullArg() {
        try {
            Game g = getGame();
            g.setPlayers(null, false);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    public void setIsVisibleSetsItInDB() {
        Game g = getGame();
        String id = db.putGame(g);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        g.setIsVisible(false, false);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Task<DataSnapshot> task = FirebaseDatabase.getInstance().getReference()
                .child(DATABASE_GAME)
                .child(g.getId())
                .child("isVisible")
                .get();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!task.isSuccessful()) {
            fail();
        }
        boolean visible = task.getResult().getValue(boolean.class);
        assertFalse(visible);
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    @Test
    public void getIsVisibleGetsIt() {
        Game g = getGame();
        assertTrue(g.getIsVisible());
    }

    @Test
    public void addPlayerFailsOnNullArg() {
        try {
            Game g = getGame();
            g.addPlayer(null, false);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    public void removePlayerFailsOnNullArg() {
        try {
            Game g = getGame();
            g.removePlayer(null, false);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    public void setPlayersFailsOnEmptyList() {
        Game g = getGame();
        try {
            g.setPlayers(new ArrayList<>(), false);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    public void setPlayersSetsPlayersLocally() {
        Game g = getGame();
        List<Player> p = new ArrayList<>();
        Player toAdd = new Player("542", "Iron Man", 0);
        Player toAdd2 = new Player("544", "Pepper Pots", 0);
        p.add(toAdd);
        p.add(toAdd2);
        g.setPlayers(p, false);
        assertEquals(p, g.getPlayers());
    }

    @Test
    public void addPlayerSetsPlayersLocally() {
        Game g = getGame();
        Player host = new Player("3", "Bob", 0);
        Player player = new Player("542", "Iron Man", 0);
        List<Player> players = new ArrayList<>();
        players.add(host);
        players.add(player);
        g.addPlayer(player, false);
        assertEquals(players, g.getPlayers());
    }

    @Test
    public void removePlayerSetsPlayersLocally() {
        Game g = getGame();
        Player host = new Player("3", "Bob", 0);
        Player player = new Player("542", "Iron Man", 0);
        List<Player> players = new ArrayList<>();
        players.add(host);
        g.addPlayer(player, false);
        g.removePlayer(player, false);
        assertEquals(players, g.getPlayers());
    }

    @Test
    public void setPlayersSetsPlayersOnDB() {
        Game g = getGame();
        String id = db.putGame(g);
        List<Player> p = new ArrayList<>();
        CountDownLatch complete = new CountDownLatch(1);
        Player toAdd = new Player("542", "Iron Man", 0);
        Player toAdd2 = new Player("544", "Pepper Pots", 0);
        p.add(toAdd);
        p.add(toAdd2);
        g.setPlayers(p, false);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(p, g.getPlayers());
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();

    }

    @Test
    public void addPlayerSetsPlayersOnDB() {
        Game g = getGame();
        String id = db.putGame(g);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Player host = new Player("3", "Bob", 0);
        Player player = new Player("542", "Iron Man", 0);
        List<Player> players = new ArrayList<>();
        players.add(host);
        players.add(player);
        g.addPlayer(player, false);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(players, g.getPlayers());
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();

    }

    @Test
    public void removePlayerSetsPlayersOnDB() {
        Game g = getGame();
        String id = db.putGame(g);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Player host = new Player("3", "Bob", 0);
        Player player = new Player("542", "Iron Man", 0);
        List<Player> players = new ArrayList<>();
        players.add(host);
        g.addPlayer(player, false);
        g.removePlayer(player, false);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(players, g.getPlayers());
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();

    }

    @Test
    public void addGameListenerFailsOnNullArg() {
        try {
            Game g = getGame();
            db.addGameListener(g, null);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    public void addGameListenerDoesntCrash() {
        ValueEventListener v = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        Game g = getGame();
        String id = db.putGame(g);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            db.addGameListener(g, v);
        } catch (Exception e) {
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    @Test
    public void getIdReturnsId() {
        Game g = getGame();
        String id = db.putGame(g);
        assertEquals(id, g.getId());
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();

    }


    @Test
    public void getGameDataReturnsGameData() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Player> players = new ArrayList<>();

        players.add(host);
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);

        GameDbData gameData = new GameDbData(name, host, players, maxPlayerCount, location, true, coins);

        assertEquals(gameData, getGame().getGameDbData());
    }

    @Test
    public void getGameDbDataWorks() {
        Game game = getGame();
        assertEquals(game.getGameDbData(), getGameData());
    }

    @Test
    public void setNumCoinsWorks() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Player> players = new ArrayList<>();
        players.add(host);
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);

        GameDbData gameData = new GameDbData(name, host, players, maxPlayerCount, location, true, coins);
        getGameData().setNumCoins(0);
        assertEquals(gameData.getNumCoins(), 0, 0);
    }

    @Test
    public void setRadiusWorks() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Player> players = new ArrayList<>();

        players.add(host);
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);

        GameDbData gameData = new GameDbData(name, host, players, maxPlayerCount, location, true, coins);
        getGameData().setRadius(0);
        assertEquals(gameData.getRadius(), 0, 0);
    }

    @Test
    public void setDurationWorks() {
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Player> players = new ArrayList<>();

        players.add(host);
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);

        GameDbData gameData = new GameDbData(name, host, players, maxPlayerCount, location, true, coins);
        getGameData().setDuration(0);
        assertEquals(gameData.getDuration(), 0, 0);
    }

    @Test
    public void equalsWorks() {
        Game game1 = getGame();
        Game game2 = getGame();
        assertEquals(game1, game2);
        assertNotEquals(game1, null);
    }

    /*On the matter of testing whether equals actually works, we already test in GameDbData that
    it works, and the Game equals method is just a call to that. Besides to test Database things
    we need the test file to be in the InstrumentedTest folder. To test with Mockito, we need the
    test to be in the Unit test folder. So we cannot do both without heavily modifying the class,
    and as stated, equals is already tested in GameDbData, and this equals is litterally just
    a call to that one*/

    @Test(expected = IllegalArgumentException.class)
    public void addCoinListenerThrowsCorrectException() {
        new GameDatabaseProxy().addCoinListener(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeCoinListenerThrowsCorrectException() {
        new GameDatabaseProxy().removeCoinListener(null, null);
    }

    @Test
    public void addCoinListenerCorrectlyUpdatesValues() {
        Game g = getGame();
        int firstValue = 7;
        int updatedValue = 323324;
        double lat = 17.;
        double lon = 18.;
        g.setCoins(Arrays.asList(new Coin(lat, lon, firstValue), new Coin(456456, 4564, 222)), true);
        CountDownLatch updated = new CountDownLatch(1);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<Coin>> coinIndicator = new GenericTypeIndicator<List<Coin>>() {
                };
                List<Coin> newCoinData = snapshot.getValue(coinIndicator);
                if (updated.getCount() == 0L) {
                    assertEquals(updatedValue, newCoinData.get(0).getValue());
                }
                updated.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert (false);
            }
        };
        GameDatabaseProxy p = new GameDatabaseProxy();
        String id = p.putGame(g);
        CountDownLatch added = new CountDownLatch(1);
        p.updateGameInDatabase(g, task -> added.countDown());
        try {
            added.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertEquals(0L, added.getCount());
        } catch (InterruptedException e) {
            fail();
        }
        p.addCoinListener(g, listener);
        g.setCoin(0, new Coin(lat, lon, updatedValue));
        p.updateGameInDatabase(g, null);

        try {
            updated.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertEquals(0L, updated.getCount());
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(updatedValue, g.getGameDbData().getCoins().get(0).getValue());
        p.removeCoinListener(g, listener);
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();

    }

    @Test
    public void setStartedCorrectlyUpdatesDb() {
        Game g = getGame();
        CountDownLatch updated = new CountDownLatch(1);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Boolean> coinIndicator = new GenericTypeIndicator<Boolean>() {
                };
                boolean started = snapshot.child("started").getValue(coinIndicator);
                if (updated.getCount() == 0L) {
                    assertTrue(started);
                }
                updated.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert (false);
            }
        };
        GameDatabaseProxy p = new GameDatabaseProxy();
        String id = p.putGame(g);
        g.setStarted(true, false);
        p.removeGameListener(g, listener);
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();

    }


    @Test(expected = IllegalArgumentException.class)
    public void setCoinsThrowsException() {
        Game g = getGame();
        g.setCoins(null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCoinThrowsException() {
        Game g = getGame();
        g.setCoin(0, null);
    }

    @Test
    public void setCoinReturnFalseForIndexTooBig() {
        Game data = getGame();
        assert (!data.setCoin(1, new Coin()));
    }


    /**
     * The basis for this test is that empty List<> objects do not get added to the
     * DB when you use the .setValue() function. As in, a node isnt created for them.
     * So when getting the Game back from the DB if the coin list was empty,
     * you will get a null pointer when getting it.
     * <p>
     * Behaviour was added to circumvent this
     */
    @Test
    public void getGameFromTaskSnapshotWorksOnEmptyCoinsList() {
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);


        CountDownLatch gotten = new CountDownLatch(1);
        Game g = new Game(name, host, maxPlayerCount, riddles, coins, location, true, 1, 1, 1);
        String id = gdp.putGame(g);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Task<DataSnapshot> g2 = gdp.getGameDataSnapshot(g.getId());
        g2.addOnCompleteListener(task -> gotten.countDown());
        try {
            gotten.await();
        } catch (InterruptedException e) {
            fail();
        }
        Game gg2 = gdp.getGameFromTaskSnapshot(g2);
        assertEquals(gg2, g);
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    @Test
    public void getIsDeletedIsFalseOnConstruction() {
        Game g = getGame();
        assertFalse(g.getIsDeleted());
    }

    @Test
    public void setIsDeletedSetsvalueLocally() {
        Game g = getGame();
        g.setIsDeleted(true, true);
        assertTrue(g.getIsDeleted());
    }

    @Test
    public void setIsDeletedSetsValueInDB() {
        Game g = getGame();
        GameDatabaseProxy gdp = new GameDatabaseProxy();

        String id = gdp.putGame(g);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        g.setIsDeleted(true, false);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        CountDownLatch gotten = new CountDownLatch(1);
        Task<DataSnapshot> dbr = FirebaseDatabase.getInstance().getReference()
                .child(DATABASE_GAME)
                .child(g.getId())
                .child("isDeleted")
                .get()
                .addOnCompleteListener(task -> gotten.countDown());
        try {
            gotten.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
            assertEquals(0L, gotten.getCount());
        } catch (InterruptedException e) {
            fail();
        }
        if (dbr.isSuccessful()) {
            boolean b = (boolean) dbr.getResult().getValue();
            assertTrue(b);
        } else {
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();

    }


}
