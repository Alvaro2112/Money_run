package sdp.moneyrun;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GameLobbyActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<GameLobbyActivity> testRule = new ActivityScenarioRule<>(GameLobbyActivity.class);

    @Test
    public void activityStartsProperly() {
        assertEquals(Lifecycle.State.RESUMED, testRule.getScenario().getState());
    }
}
