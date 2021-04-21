package sdp.moneyrun.game;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import sdp.moneyrun.R;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.game.GameLobbyActivity;
import sdp.moneyrun.ui.menu.MenuActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GameLobbyActivityInstrumentedTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Rule
    public ActivityScenarioRule<GameLobbyActivity> testRule = new ActivityScenarioRule<>(GameLobbyActivity.class);

    @Test
    public void activityStartsProperly() {
        assertEquals(Lifecycle.State.RESUMED, testRule.getScenario().getState());
    }


    @Test
    public void playerMissingTextViewWorks() {
        String default_text = "Players Missing: 0";
        Espresso.onView(withId(R.id.lobby_players_missing_TextView)).check(matches(withText(default_text)));
    }

    @Test
    public void addPLayerFailsWhenNull(){
        exception.expect(RuntimeException.class);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(GameLobbyActivity.class)) {
            scenario.onActivity(a ->{
                a.addPlayer(null);
            });
        }

    }
    @Test
    public void addPlayerListFailsWhenNull(){
        exception.expect(RuntimeException.class);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(GameLobbyActivity.class)) {
            scenario.onActivity(a ->{
                a.addPlayerList(null);
            });
        }
    }


    @Test
    public void AddPlayerListAddsAllPlayerToView(){
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(GameLobbyActivity.class)) {
            scenario.onActivity(a ->{
                Player player = new Player(123, "Tess", "SomeAdress", 0,0,0);
                Player player2 = new Player(12, "Rafa", "SomeAdress", 0,0,0);
                ArrayList<Player> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addPlayerList(list);
                assertEquals( a.getListAdapter().getCount(), 2);
            });
        }
    }

    @Test
    public void LeaveLobbyWorks() {
        try {
            Intents.init();
            onView(ViewMatchers.withId(R.id.leave_lobby_button)).perform(ViewActions.click());
            Thread.sleep(4000);
            intended(hasComponent(MenuActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }
}
