package sdp.moneyrun.authentication;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import sdp.moneyrun.R;
import sdp.moneyrun.database.PlayerDatabaseProxy;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.authentication.LoginActivity;
import sdp.moneyrun.ui.authentication.RegisterUserActivity;
import sdp.moneyrun.ui.authentication.SignUpActivity;
import sdp.moneyrun.ui.menu.MenuActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)

public class LoginInstrumentedTest {
    @BeforeClass
    public static void setPersistence(){
        if(!MainActivity.calledAlready){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }

    private final String TAG = LoginActivity.class.getSimpleName();

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
            Espresso.onView(ViewMatchers.withId(R.id.signUpButton)).perform(ViewActions.click());
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

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
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

            Espresso.onView(withId(R.id.loginEmailAddress)).check(matches(withError(expected)));
            Intents.release();
        }
    }


    @Test
    public void loginNoPasswordError() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Intents.init();
            final String email = "someone@epfl.ch";
            final String expected = "Password is required";
            Espresso.onView(withId(R.id.loginEmailAddress)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.loginButton)).perform(ViewActions.click());
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

            Espresso.onView(withId(R.id.loginPassword)).check(matches(withError(expected)));
            Intents.release();
        }
    }

    @Test
    public void loginInvalidEmailError(){
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Intents.init();

            String email = "invalidemail";
            String password = "abc";
            String expected = "Email format is invalid";
            Espresso.onView(withId(R.id.loginEmailAddress)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.loginPassword)).perform(typeText(password), closeSoftKeyboard());
            Espresso.onView(withId(R.id.loginButton)).perform(ViewActions.click());
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

            Espresso.onView(withId(R.id.loginEmailAddress)).check(matches(withError(expected)));

            Intents.release();
        }
    }

    @Test
    public void loginWithRegisteredUserAndValidPlayerStartsActivity(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Intents.init();
            String email = "logintest@epfl.ch";
            String password = "login123456789";
            AtomicReference<Player> playerUserRef = new AtomicReference<>();

            // Define player instance given email and password user
            scenario.onActivity(activity -> {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity, task -> {
                            if (!task.isSuccessful()) {
                                assertEquals(0, 1);
                            } else {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                int playerId = firebaseUser.getUid().hashCode();

                                // Build a new instance of player for the user to create a
                                // valid instance in the database
                                playerUserRef.set(new Player(playerId));
                                playerUserRef.get().setName("Bob");
                                playerUserRef.get().setAddress("Somewhere");
                            }
                        });
            });

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Intents.init();
            }

            Player playerUser = playerUserRef.get();

            PlayerDatabaseProxy db = new PlayerDatabaseProxy();
            db.putPlayer(playerUser);

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Intents.release();
            }

            Espresso.onView(withId(R.id.loginEmailAddress)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.loginPassword)).perform(typeText(password), closeSoftKeyboard());
            Espresso.onView(withId(R.id.loginButton)).perform(click());

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Intents.release();
            }

            intended(hasComponent(MenuActivity.class.getName()));
            db.removePlayer(playerUser);
            Intents.release();
        }
    }

    @Test
    public void loginWithRegisteredUserAndInvalidPlayerStartsActivity(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Intents.init();
            String email = "logintest@epfl.ch";
            String password = "login123456789";
            AtomicReference<Player> playerUserRef = new AtomicReference<>();

            // Define player instance given email and password user
            scenario.onActivity(activity -> {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity, task -> {
                            if (!task.isSuccessful()) {
                                assertEquals(0, 1);
                            } else {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                int playerId = firebaseUser.getUid().hashCode();

                                // Build a new instance of player for the user to create a
                                // valid instance in the database
                                playerUserRef.set(new Player(playerId));
                                playerUserRef.get().setName("Bob");
                                playerUserRef.get().setAddress("Somewhere");
                            }
                        });
            });

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Intents.init();
            }

            Player playerUser = playerUserRef.get();

            PlayerDatabaseProxy db = new PlayerDatabaseProxy();
            db.removePlayer(playerUser);

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Intents.release();
            }

            Espresso.onView(withId(R.id.loginEmailAddress)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.loginPassword)).perform(typeText(password), closeSoftKeyboard());
            Espresso.onView(withId(R.id.loginButton)).perform(click());

            Thread.sleep(4000);

            intended(hasComponent(RegisterUserActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }

    @Test
    public void logOutWorks(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Intents.init();
            String email = "logintest@epfl.ch";
            String password = "login123456789";
            AtomicReference<Player> playerUserRef = new AtomicReference<>();

            // Define player instance given email and password user
            scenario.onActivity(activity -> {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity, task -> {
                            if (!task.isSuccessful()) {
                                assertEquals(0, 1);
                            } else {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                int playerId = firebaseUser.getUid().hashCode();

                                // Build a new instance of player for the user to create a
                                // valid instance in the database
                                playerUserRef.set(new Player(playerId));
                                playerUserRef.get().setName("Bob");
                                playerUserRef.get().setAddress("Somewhere");
                            }
                        });
            });

            Thread.sleep(5000);

            Player playerUser = playerUserRef.get();

            PlayerDatabaseProxy db = new PlayerDatabaseProxy();
            db.putPlayer(playerUser);

            Thread.sleep(4000);

            Espresso.onView(withId(R.id.loginEmailAddress)).perform(typeText(email), closeSoftKeyboard());
            Espresso.onView(withId(R.id.loginPassword)).perform(typeText(password), closeSoftKeyboard());
            Espresso.onView(withId(R.id.loginButton)).perform(click());

            Thread.sleep(4000);

            assertNotNull(FirebaseAuth.getInstance().getCurrentUser());
            onView(withId(R.id.drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT)))
                    .perform(DrawerActions.open());
            Espresso.onView(withId(R.id.log_out_button)).perform(ViewActions.click());

            Thread.sleep(1000);

            assertNull(FirebaseAuth.getInstance().getCurrentUser());
            intended(hasComponent(LoginActivity.class.getName()));

            db.removePlayer(playerUser);
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }
    @Test
    public void guestButtonStartsRegisterPlayerWhenNotNull(){
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Intents.init();
            Espresso.onView(withId(R.id.guestButton)).perform(ViewActions.click());
            Thread.sleep(4000);
            intended(hasComponent(RegisterUserActivity.class.getName()));
            Intents.release();
        }catch (Exception e){
            fail();
        }
    }

}
