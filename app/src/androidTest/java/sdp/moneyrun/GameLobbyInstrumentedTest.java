package sdp.moneyrun;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class GameLobbyInstrumentedTest {
    @Rule
    public ActivityScenarioRule<MenuActivity> testRule = new ActivityScenarioRule<>(MenuActivity.class);


    @Test
    public void LeaveLobbyButtonWorks() {
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(GameLobbyActivity.class)) {
            Intents.init();
            Espresso.onView(withId(R.id.leave_lobby_button)).perform(ViewActions.click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            intended(hasComponent(MenuActivity.class.getName()));
            Intents.release();
        }
    }
}
