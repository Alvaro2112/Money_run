package sdp.moneyrun.game;

import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import sdp.moneyrun.R;
import sdp.moneyrun.database.game.GameDatabaseProxy;
import sdp.moneyrun.database.riddle.Riddle;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.game.GameLobbyActivity;
import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class GameLobbyActivityInstrumentedTest {
    private final String DATABASE_GAME = "games";
    private final long ASYNC_CALL_TIMEOUT = 5L;


    @NonNull
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @BeforeClass
    public static void setPersistence() {
        if (!MainActivity.calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }


    @NonNull
    private Intent getStartIntent() {
        User actualUser = new User("32", "usersAreUnnecessary", 0, 0, 0);
        Player currentUser = new Player("78646", "CURRENT_USER", 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), GameLobbyActivity.class);
        toStart.putExtra("currentUser", currentUser);
        toStart.putExtra("UserTypeCurrentUser", actualUser);
        return toStart;
    }

    @NonNull
    public Game getGame() {
        String name = "LobbyActivityInstrumentedTest";
        Player host = new Player("12634", "Bob", 0);
        int maxPlayerCount = 2;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(37.4219473);
        location.setLongitude(-122.0840015);
        return new Game(name, host, maxPlayerCount, riddles, coins, location, true, 1, 100, 10);
    }

    @Test
    public void backButtonDoesNothing() {

        Player host = new Player("12634", "Bob", 0);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GameLobbyActivity.class);
        intent.putExtra("currentUser", host);
        intent.putExtra("host", true);

        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();

        List<Player> players = game.getPlayers();
        players.add(host);

        String id = gdp.putGame(game);
        CountDownLatch added = new CountDownLatch(1);
        gdp.updateGameInDatabase(game, task -> added.countDown());
        try {
            added.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        intent.putExtra("currentGameId", id);

        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            assertEquals(Lifecycle.State.RESUMED, scenario.getState());
            onView(isRoot()).perform(ViewActions.pressBack());
            assertEquals(Lifecycle.State.RESUMED, scenario.getState());
        }
    }


    @Test
    public void StartGameAsNonHostWorksWhenHostsLaunchesGame() {
        Player host = new Player("12634", "Bob", 0);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GameLobbyActivity.class);
        intent.putExtra("currentUser", host);
        intent.putExtra("host", true);

        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();

        List<Player> players = game.getPlayers();
        players.add(host);

        String id = gdp.putGame(game);
        CountDownLatch added = new CountDownLatch(1);
        gdp.updateGameInDatabase(game, task -> added.countDown());
        try {
            added.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        intent.putExtra("currentGameId", id);

        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(4000);
            onView(ViewMatchers.withId(R.id.launch_game_button)).perform(ViewActions.click());
            Thread.sleep(10000);
            intended(hasComponent(MapActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        Player nonHost = new Player("4", "Carl", 0);
        Intent intent2 = new Intent(ApplicationProvider.getApplicationContext(), GameLobbyActivity.class);
        intent2.putExtra("currentUser", nonHost);
        intent2.putExtra("currentGameId", id);
        players = game.getPlayers();
        players.add(nonHost);
        game.setPlayers(players, false);
        try (ActivityScenario<GameLobbyActivity> scenario2 = ActivityScenario.launch(intent2)) {
            Intents.init();
            Thread.sleep(15000);
            intended(hasComponent(MapActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();

    }

    @Test
    public void StartGameAsHostWorks() {
        Player host = new Player("12634", "Bob", 0);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GameLobbyActivity.class);
        intent.putExtra("currentUser", host);
        intent.putExtra("host", true);
        User actualUser = new User("32", "usersAreUnnecessary", 0, 0, 0);
        intent.putExtra("UserTypeCurrentUser", actualUser);


        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();

        List<Player> players = game.getPlayers();
        players.add(host);

        String id = gdp.putGame(game);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        intent.putExtra("currentGameId", id);

        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(4000);
            onView(ViewMatchers.withId(R.id.launch_game_button)).perform(ViewActions.click());
            Thread.sleep(4000);
            intended(hasComponent(MapActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    @Test
    public void InitializeGameAddsCoinsToDB() {
        Player host = new Player("12634", "Bob", 0);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GameLobbyActivity.class);
        intent.putExtra("currentUser", host);
        intent.putExtra("host", true);
        User actualUser = new User("32", "usersAreUnnecessary", 0, 0, 0);
        intent.putExtra("UserTypeCurrentUser", actualUser);
        intent.putExtra("locationMode", (String) null);
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();

        List<Player> players = game.getPlayers();
        players.add(host);

        String id = gdp.putGame(game);
        CountDownLatch added = new CountDownLatch(1);
        gdp.updateGameInDatabase(game, task -> added.countDown());
        try {
            added.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        intent.putExtra("currentGameId", id);

        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(4000);
            onView(ViewMatchers.withId(R.id.launch_game_button)).perform(ViewActions.click());
            Thread.sleep(20000);
            Intents.release();
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            final GameDatabaseProxy db = new GameDatabaseProxy();

            Task<DataSnapshot> dataTask = ref.child("games").child(id).get();
            dataTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Game fromDB = db.getGameFromTaskSnapshot(task);
                    assertEquals(fromDB.getCoins().size(), 1);
                } else {
                    fail();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }


    @Test
    public void nameDisplaysProperly() {
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        String id = gdp.putGame(game);

        //Tried with CountDownLatch but didn't work
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        intent.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(2000);
            onView(ViewMatchers.withId(R.id.lobby_title)).check(matches(withText(game.getName())));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    @Test
    public void playerListUpdatesWithDB() {
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        Player justJoined = new Player("3", "justJoined", 0);
        List<Player> players = game.getPlayers();
        players.add(justJoined);

        String id = gdp.putGame(game);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        intent.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(4000);
            //  onView(ViewMatchers.withId(R.id.player_list_textView)).check(matches(withText(game.getHost().getName())));
            scenario.onActivity(activity -> assertEquals(activity.getListAdapter().getCount(), 1));

            game.setPlayers(players, false);
            Thread.sleep(4000);
            scenario.onActivity(activity -> assertEquals(activity.getListAdapter().getCount(), 2));
            Thread.sleep(4000);

            //   onView(ViewMatchers.withId(R.id.player_list_textView)).check(matches(withText(game.getHost().getName()+"\n"+justJoined.getName())));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    @Test
    public void playersMissingDisplaysProperly() {
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        String id = gdp.putGame(game);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        intent.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(2000);
            onView(ViewMatchers.withId(R.id.players_missing_TextView))
                    .check(matches(withText("Players missing: "
                            + (game.getMaxPlayerCount() - 1))));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    @Test
    public void playersMissingUpdatesWithDB() {
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        Player justJoined = new Player("3", "justJoined", 0);
        List<Player> players = game.getPlayers();
        players.add(justJoined);


        String id = gdp.putGame(game);
        CountDownLatch added = new CountDownLatch(1);
        try {
            added.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        intent.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(2000);
            onView(ViewMatchers.withId(R.id.players_missing_TextView))
                    .check(matches(withText("Players missing: "
                            + (game.getMaxPlayerCount() - 1))));

            game.setPlayers(players, false);
            Thread.sleep(2000);
            onView(ViewMatchers.withId(R.id.players_missing_TextView))
                    .check(matches(withText("Players missing: "
                            + (game.getMaxPlayerCount() - 2))));
            Intents.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }

    @Test
    public void addPlayerListFailsWhenNull() {
        exception.expect(RuntimeException.class);
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        String id = gdp.putGame(game);

        //I tried using a CountDownLatch here but it doesn't work
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        intent.putExtra("currentGameId", id);

        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a -> a.addPlayerList(null));
        } finally {
            FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
        }
    }


    @Test
    public void LeaveLobbyWorks() {
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        String id = gdp.putGame(game);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        intent.putExtra("currentGameId", id);
        intent.putExtra("currentUser", new Player("1234567891", "alex", 0));


        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(3000);

            onView(ViewMatchers.withId(R.id.leave_lobby_button)).perform(ViewActions.click());
            Thread.sleep(3000);
            intended(hasComponent(MenuActivity.class.getName()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } finally {
            Intents.release();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }


    @Test
    public void LeaveIsDeleteForHost() {
        Game g = getGame();
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), GameLobbyActivity.class);
        Player host = new Player("12634", "Bob", 0);
        toStart.putExtra("currentUser", host);
        User actualUser = new User("32", "usersAreUnnecessary", 0, 0, 0);
        toStart.putExtra("UserTypeCurrentUser", actualUser);
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        String id = gdp.putGame(g);
        CountDownLatch added = new CountDownLatch(1);
        gdp.updateGameInDatabase(g, task -> added.countDown());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        toStart.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(toStart)) {
            Intents.init();
            Thread.sleep(3000);
            onView(withId(R.id.leave_lobby_button)).check(matches(withText("Delete")));
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } finally {
            FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
            Intents.release();
        }
    }

    @Test
    public void launchIsDisabledForNonHost() {
        Game g = getGame();
        g.addPlayer(new Player("999", "CURRENT_USER", 0), true);
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        String id = gdp.putGame(g);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        intent.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            onView(withId(R.id.launch_game_button)).check(matches(not(isEnabled())));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }


    @Test
    public void WhenGameIsDeletedPlayerLeavesLobby() {
        Game g = getGame();
        g.addPlayer(new Player("999", "CURRENT_USER", 0), true);
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        String id = gdp.putGame(g);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        intent.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            g.setIsDeleted(true, false);
            Thread.sleep(2000);
            intended(hasComponent(MenuActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }


    @Test
    public void LeaveIsLeaveForPlayer() {
        Game g = getGame();
        g.addPlayer(new Player("999", "CURRENT_USER", 0), true);
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        String id = gdp.putGame(g);

        //I tried using a CountDownLatch here but it doesn't work
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        intent.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(3000);
            onView(withId(R.id.leave_lobby_button)).check(matches(withText("Leave")));
            Thread.sleep(2000);
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_GAME).child(id).removeValue();
    }


    @Test
    public void deleteGameDeletesItFromDB() {
        Game g = getGame();
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), GameLobbyActivity.class);
        Player host = new Player("12634", "Bob", 0);
        toStart.putExtra("currentUser", host);
        User actualUser = new User("32", "usersAreUnnecessary", 0, 0, 0);
        toStart.putExtra("UserTypeCurrentUser", actualUser);
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        String id = gdp.putGame(g);
        CountDownLatch added = new CountDownLatch(1);
        gdp.updateGameInDatabase(g, task -> added.countDown());
        try {
            added.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        toStart.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(toStart)) {
            Intents.init();
            Thread.sleep(2000);
            onView(withId(R.id.leave_lobby_button)).perform(ViewActions.click());
            Thread.sleep(2000);
            intended(hasComponent(MenuActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        Task<DataSnapshot> deleted = FirebaseDatabase.getInstance().getReference()
                .child("games").child(g.getId()).get();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNull(deleted.getResult().getValue());
    }

}

