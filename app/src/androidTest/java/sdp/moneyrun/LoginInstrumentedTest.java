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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)

public class LoginInstrumentedTest {

    //adapted from https://stackoverflow.com/questions/28408114/how-can-to-test-by-espresso-android-widget-textview-seterror/28412476
    private static Matcher<View> withError(final String expected) {
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
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("sdp.moneyrun", appContext.getPackageName());
    }

    @Test
    public void signUpButtonToSignUpPage() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Intents.init();
            Espresso.onView(withId(R.id.signUpButton)).perform(ViewActions.click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

            intended(hasComponent(SignUpActivity.class.getName()));
            Intents.release();

        }

    }

    @Test
    public void loginNoEmailError() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Intents.init();
            final String expected = "Email is required";
            Espresso.onView(withId(R.id.loginButton)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.loginEmailAddress)).check(matches(withError(expected)));
            Intents.release();
        }
    }


    @Test
    public void loginNoPasswordError() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Intents.init();
            final String email = "kk@epfl.ch";
            final String expected = "Password is required";
            Espresso.onView(withId(R.id.loginEmailAddress)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.loginButton)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.loginPassword)).check(matches(withError(expected)));
            Intents.release();
        }
    }


}
