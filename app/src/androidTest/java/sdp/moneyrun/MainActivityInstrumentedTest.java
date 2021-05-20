package sdp.moneyrun;


import androidx.annotation.NonNull;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.authentication.LoginActivity;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @NonNull
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void splashScreenSoundPlays(){
        
    }

    @Test
    public void activityChangesToDashBoardAfter3Sec() {
        Intents.init();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
            return;
        }
        intended(hasComponent(LoginActivity.class.getName()));
        Intents.release();
    }
}
