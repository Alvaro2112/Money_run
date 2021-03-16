package sdp.moneyrun;

import android.view.View;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
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

//    @Test
//    public void anActivityIsStartedOnSubmit(){
//        try(ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
//            Intents.init();
//            String email = "exemple@epfl.ch";
//            String password = "Barents$8467";
//            Espresso.onView(withId(R.id.signUpEmailText)).perform(typeText(email), closeSoftKeyboard());
//            Espresso.onView(withId(R.id.signUpPassword)).perform(typeText(password), closeSoftKeyboard());
//            Espresso.onView(withId(R.id.signUpSubmitButton)).perform(click());
//            Intents.release();
//        }
//    }

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
}






