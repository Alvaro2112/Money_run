package sdp.moneyrun;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
@RunWith(AndroidJUnit4.class)

public class SignUpActivityTest {

    @Test
    public void emailCorrectlyTyped(){
        try(ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
            Intents.init();
            String email = "exemple@epfl.ch";
            Espresso.onView(withId(R.id.signUpEmailText)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.signUpEmailText)).check(matches(withText(email)));
            Intents.release();
        }
    }

    @Test
    public void passwordCorrectlyTyped(){
        try(ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
            Intents.init();
            String password = "abcd";
            Espresso.onView(withId(R.id.signUpPassword)).perform(typeText(password), closeSoftKeyboard());
            Espresso.onView(withId(R.id.signUpPassword)).check(matches(withText(password)));
            Intents.release();
        }
    }


    /*
    Warning : Manually deleting the user (at this address
    https://console.firebase.google.com/u/0/project/money-run-4f27f/authentication/users)
    created on the firebase authentication system is needed
    before relaunching this method
     */
    @Test
    public void anActivityIsStartedOnSubmit(){
        try(ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
            Intents.init();
            String email = getSaltString() + "@gmail.com";
            String password = "Barents$8467";
            Espresso.onView(withId(R.id.signUpEmailText)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.signUpPassword)).perform(typeText(password), closeSoftKeyboard());
            Espresso.onView(withId(R.id.signUpSubmitButton)).perform(click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Thread.sleep(1000);
            intended(hasComponent(RegisterPlayerActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void anActivityIsNotStartedOnAlreadyUsedEmail(){
        try(ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
            Intents.init();
            String email = "exampletofail@fail.com";
            String password = "Barents$8467";
            Espresso.onView(withId(R.id.signUpEmailText)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.signUpPassword)).perform(typeText(password), closeSoftKeyboard());
            Espresso.onView(withId(R.id.signUpSubmitButton)).perform(click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Thread.sleep(1000);
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //adapted from https://stackoverflow.com/questions/28408114/how-can-to-test-by-espresso-android-widget-textview-seterror/28412476
    public static TypeSafeMatcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }
                EditText editText = (EditText) view;
                return editText.getError().toString().equals(expected);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    @Test
    public void submitWithoutEmail() {
        try (ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
            Intents.init();
            final String expected = "Email is required";
            Espresso.onView(withId(R.id.signUpSubmitButton)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.signUpEmailText)).check(matches(withError(expected)));
            Intents.release();
        }
    }

    @Test
    public void submitWithIncorrectMailFormat(){
        try (ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
            Intents.init();
            String email = "exemple";
            String password = "abcd";
            Espresso.onView(withId(R.id.signUpEmailText)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.signUpPassword)).perform(typeText(password), closeSoftKeyboard());
            final String expected = "Please enter a valid email address";
            Espresso.onView(withId(R.id.signUpSubmitButton)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.signUpEmailText)).check(matches(withError(expected)));
            Intents.release();
        }
    }

    @Test
    public void submitWithoutPassword(){
        try (ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
            Intents.init();
            String email = "exemple@epfl.ch";
            Espresso.onView(withId(R.id.signUpEmailText)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.signUpSubmitButton)).perform(click());
            final String expected = "Password is required";
            Espresso.onView(withId(R.id.signUpPassword)).check(matches(withError(expected)));
            Intents.release();
        }
    }

    @Test
    public void submitWithInsufficientPassword(){
        try (ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
            Intents.init();
            String email = "exemple@epfl.ch";
            String password = "abcd";
            Espresso.onView(withId(R.id.signUpEmailText)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.signUpPassword)).perform(typeText(password), closeSoftKeyboard());
            final String expected = "Password is too weak";
            Espresso.onView(withId(R.id.signUpSubmitButton)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.signUpPassword)).check(matches(withError(expected)));
            Intents.release();
        }
    }

    /*
    From https://stackoverflow.com/questions/45841500/generate-random-emails/55768012
     */
    private  String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}






