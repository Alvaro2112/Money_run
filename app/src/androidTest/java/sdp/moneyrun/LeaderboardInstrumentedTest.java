package sdp.moneyrun;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.game.GameLobbyActivity;
import sdp.moneyrun.ui.menu.LeaderboardActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LeaderboardInstrumentedTest {

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

    @Test
    public void useAppContext() {
        // Context of the app under test.

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("sdp.moneyrun", appContext.getPackageName());
    }

    @Test
    public void addPlayerWorks() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {

                //Address was not set here before I don't know why
                Player player = new Player("123", "Tess", 0);
                player.setScore(8008, false);
                a.addPlayer(player);

                assertEquals(a.getPlayerList().size(), 1);
            });
        } catch (Exception e) {
            assertEquals(2, 1);
        }
    }

    @Test
    public void addPLayerAddsPlayerToView() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {
                //Address was not set here before I don't know why
                Player player = new Player("123", "Tess", 0);
                player.setScore(8008, false);
                a.addPlayer(player);
                assertEquals(a.getLdbAdapter().getCount(), 1);

            });
        }
    }


    @Test
    public void addPlayerNullThrowsException() {
        exception.expect(RuntimeException.class);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> a.addPlayer(null));
        }
    }

    @Test
    public void AddPlayerListWorks() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {

                //Address was not set here before I don't know why
                Player player = new Player("123", "Tess", 0);
                player.setScore(8008, false);
                //Address was not set here before I don't know why
                Player player2 = new Player("12", "Rafa", 0);
                player2.setScore(8001, false);
                ArrayList<Player> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addPlayerList(list);
                assertEquals(a.getPlayerList().size(), 2);
            });
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals(2, 1);
        }
    }

    @Test
    public void AddPlayerListAddsAllPlayerToView() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {

                //Address was not set here before I don't know why
                Player player = new Player("123", "Tess", 0);
                player.setScore(8008, false);

                //Address was not set here before I don't know why
                Player player2 = new Player("12", "Rafa", 0);
                player2.setScore(8001, false);
                ArrayList<Player> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addPlayerList(list);
                assertEquals(a.getLdbAdapter().getCount(), 2);

            });
        }
    }

    @Test
    public void addPlayerListThrowsNullException() {
        exception.expect(RuntimeException.class);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> a.addPlayerList(null));
        }
    }

    @Test
    public void setMainPlayerGetsTheRightInfoAndSetsThePlayerAttributes() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {
                String playerId = "48390";
                String name = "John";
                String address = "Here";
                Player user = new Player(playerId);
                user.setName(name);

                Intent intent = new Intent(a, LeaderboardActivity.class);
                intent.putExtra("user", user);

                a.setMainPlayer(user);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Player p = a.getUserPlayer();
                assertNotNull(p);
                assertNotNull(p.getName());
            });
        }
    }

    @Test
    public void bestToWorstPlayerReturnsSortedPlayerList() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("1", "a0", 0));
        players.add(new Player("24", "a1", 6));
        players.add(new Player("78", "a2", 68));
        players.add(new Player("2", "a3", 24));
        players.add(new Player("9", "a4", 11));
        ArrayList<Player> players2 = new ArrayList<>();
        players2.add(new Player("78", "a2", 68));
        players2.add(new Player("2", "a3", 24));
        players2.add(new Player("9", "a4", 11));
        players2.add(new Player("24", "a1", 6));
        players2.add(new Player("1", "a0", 0));
        Helpers.bestToWorstPlayer(players);
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

    @Test
    public void goToMenuButtonWorks() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LeaderboardActivity.class);
        Player user = new Player("3", "Bob", 0);
        User user2 = new User("rrrrr");
        intent.putExtra("userEnd",user2);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
                onView(ViewMatchers.withId(R.id.leaderboard_button_end)).perform(ViewActions.click());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intended(hasComponent(MenuActivity.class.getName()));
            Intents.release();
        }catch (Exception e){
            fail();
        }
    }
}
