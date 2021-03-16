package sdp.moneyrun;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class DashBoardInstrumentedTest {


    @Rule
    public ActivityScenarioRule<DashBoardActivity> testRule = new ActivityScenarioRule<>(DashBoardActivity.class);

    @Test
    public void signOutLaunchesIntent(){
        Intents.init();
        onView(withId(R.id.sign_out_btn)).perform(click());
        intended(hasComponent(GoogleAuthActivity.class.getName()));
        Intents.release();
    }
}
