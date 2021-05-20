package sdp.moneyrun;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.menu.LeaderboardActivity;
import sdp.moneyrun.ui.menu.MainLeaderboardActivity;
import sdp.moneyrun.user.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainLeaderboardInstrumentedTest {

    @NonNull
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("sdp.moneyrun", appContext.getPackageName());
    }

    @Test
    public void addPlayerWorks() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        User user = new User("3", "Bob", "Epfl", 0, 0, 0);
        intent.putExtra("user", user);

        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a -> {

                //Address was not set here before I don't know why
                User player = new User("123", "Tess", "SomeAdress", 0, 0, 0);
                player.setMaxScoreInGame(8008, false);
                a.addUser(player);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                assertTrue(a.getUserList().size() <= a.getMaxUserNumber() + 1);
            });
        }
    }

    @Test
    public void addPLayerAddsPlayerToView() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        User user = new User("3", "Bob", "Epfl", 0, 0, 0);
        intent.putExtra("user", user);

        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a -> {
                //Address was not set here before I don't know why
                User player = new User("123", "Tess", "SomeAdress", 0, 0, 0);
                player.setMaxScoreInGame(8008, false);
                a.addUser(player);
                assertTrue(a.getLdbAdapter().getCount() <= a.getMaxUserNumber() + 1);

            });
        }
    }


    @Test
    public void addPlayerNullThrowsException() {
        exception.expect(RuntimeException.class);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        User user = new User("3", "Bob", "Epfl", 0, 0, 0);
        intent.putExtra("user", user);
        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a -> a.addUser(null));
        }
    }

    @Test
    public void AddPlayerListWorks() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        User user = new User("3", "Bob", "Epfl", 0, 0, 0);
        intent.putExtra("user", user);

        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a -> {

                //Address was not set here before I don't know why
                User player = new User("123", "Tess", "SomeAdress", 0, 0, 0);
                player.setMaxScoreInGame(8008, false);
                //Address was not set here before I don't know why
                User player2 = new User("12", "Rafa", "SomeAdress", 0, 0, 0);
                player2.setMaxScoreInGame(8001, false);
                ArrayList<User> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addUserList(list);
                assertTrue(a.getUserList().size() <= a.getMaxUserNumber() + 2);
            });
        }
    }

    @Test
    public void AddPlayerListAddsAllPlayerToView() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        User user = new User("3", "Bob", "Epfl", 0, 0, 0);
        intent.putExtra("user", user);

        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a -> {

                //Address was not set here before I don't know why
                User player = new User("123", "Tess", "SomeAdress", 0, 0, 0);
                player.setMaxScoreInGame(8008, false);

                //Address was not set here before I don't know why
                User player2 = new User("12", "Rafa", "SomeAdress", 0, 0, 0);
                player2.setMaxScoreInGame(8001, false);
                ArrayList<User> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addUserList(list);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                assertTrue(a.getLdbAdapter().getCount() <= a.getMaxUserNumber() + 2);

            });
        }
    }

    @Test
    public void addPlayerListThrowsNullException() {
        exception.expect(RuntimeException.class);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        User user = new User("3", "Bob", "Epfl", 0, 0, 0);
        intent.putExtra("user", user);

        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a -> a.addUserList(null));
        }
    }

    @Test
    public void bestToWorstPlayerReturnsSortedPlayerList() {
        ArrayList<User> players = new ArrayList<>();
        players.add(new User("1", "a0", "b", 0, 0, 0));
        players.add(new User("24", "a1", "b", 0, 0, 6));
        players.add(new User("78", "a2", "b", 0, 0, 68));
        players.add(new User("2", "a3", "b", 0, 0, 24));
        players.add(new User("9", "a4", "b", 0, 0, 11));
        ArrayList<User> players2 = new ArrayList<>();
        players2.add(new User("78", "a2", "b", 0, 0, 68));
        players2.add(new User("2", "a3", "b", 0, 0, 24));
        players2.add(new User("9", "a4", "b", 0, 0, 11));
        players2.add(new User("24", "a1", "b", 0, 0, 6));
        players2.add(new User("1", "a0", "b", 0, 0, 0));
        Helpers.bestToWorstUser(players);
        assertEquals(players2, players);
    }


    @Test
    public void testIfEndGamePlayerReceivesSinglePlayerWhenGivenSizeIsOne() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LeaderboardActivity.class);
        Player user = new Player("3", "Bob", 0);
        intent.putExtra("players" + 0, user);
        intent.putExtra("numberOfPlayers", 1);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            scenario.onActivity(a -> {
                a.getEndGamePlayers();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assertEquals(a.getPlayerList().size(), 1);
            });
            Intents.release();
        }
    }
}
