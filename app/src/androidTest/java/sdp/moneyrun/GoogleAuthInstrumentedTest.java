package sdp.moneyrun;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;


@RunWith(AndroidJUnit4.class)
public class GoogleAuthInstrumentedTest {

  //  @Rule
   //public ActivityScenarioRule<GoogleAuthActivity> testRule = new ActivityScenarioRule<>(GoogleAuthActivity.class);

    @Test
    public void test1(){
        try(ActivityScenario<GoogleAuthActivity> scenario = ActivityScenario.launch(GoogleAuthActivity.class)) {
            Intents.init();
            //Espresso.onView(ViewMatchers.withId(R.id.sign_in_btn)).perform(ViewActions.click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            //intended(hasComponent(DashBoardActivity.class.getName()));
            Intents.release();
        }
    }

}





