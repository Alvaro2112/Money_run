package sdp.moneyrun;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class SignUpInterfaceTest {

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void emailCorrectlyTyped(){
        Intents.init();
        String email = "exemple@epfl.ch";
        Espresso.onView(withId(R.id.signUpEmailText)).perform(typeText(email), closeSoftKeyboard());
        Espresso.onView(withId(R.id.signUpEmailText)).check(matches(withText(email)));
        Intents.release();
    }

    @Test
    public void passwordCorrectlyTyped(){
        Intents.init();
        String password = "abcd";
        Espresso.onView(withId(R.id.signUpPassword)).perform(typeText(password), closeSoftKeyboard());
        Espresso.onView(withId(R.id.signUpPassword)).check(matches(withText(password)));
        Intents.release();
    }

    @Test
    public void anActivityIsStartedOnSubmit(){
        Intents.init();
        String email = "exemple@epfl.ch";
        String password = "Barents$8467";
        Espresso.onView(withId(R.id.signUpEmailText)).perform(typeText(email), closeSoftKeyboard());
        Espresso.onView(withId(R.id.signUpPassword)).perform(typeText(password), closeSoftKeyboard());
        Espresso.onView(withId(R.id.signUpSubmitButton)).perform(click());
        Intents.release();
    }

    @Test
    public void authIsPerformed(){
        FirebaseAuth.getInstance().useEmulator("10.0.2.2'", 9099);
        Intents.init();
        String email = "exemple@epfl.ch";
        String password = "abcd";
        Espresso.onView(withId(R.id.signUpEmailText)).perform(typeText(email), closeSoftKeyboard());
        Espresso.onView(withId(R.id.signUpPassword)).perform(typeText(password), closeSoftKeyboard());
        Espresso.onView(withId(R.id.signUpSubmitButton)).perform(click());
    }

}
