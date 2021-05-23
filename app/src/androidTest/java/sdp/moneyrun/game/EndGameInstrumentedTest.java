package sdp.moneyrun.game;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import sdp.moneyrun.R;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.game.EndGameActivity;
import sdp.moneyrun.ui.menu.LeaderboardActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class EndGameInstrumentedTest {

    @BeforeClass

    public static void setPersistence() {
        if (!MainActivity.calledAlready) {
            try {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                MainActivity.calledAlready = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public static User  getUser(){
        String name = "John Doe";
        String address = "Someeewhere";
        String id = "1234567891";

        User user = new User(id, name, address, 0, 0, 0);
        return user;
    }
    public static Intent getEndGameIntent(){
        Intents.init();
        UserDatabaseProxy db = new UserDatabaseProxy();
        User user = getUser();
        db.putUser(user);
        Intent endGameIntent = new Intent(ApplicationProvider.getApplicationContext(), EndGameActivity.class);
        endGameIntent.putExtra("score", 3);
        endGameIntent.putExtra("numberOfCollectedCoins", 2);
        endGameIntent.putExtra("hasDied", true);

        endGameIntent.putExtra("playerId", "1234567891");
        try{
            Thread.sleep(5000);

        }catch (Exception e){
            fail();
        }

        return endGameIntent;
    }




    @Test
    public void updateTextFailsWithoutLists() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {

            Espresso.onView(ViewMatchers.withId(R.id.end_game_text)).check(matches(withText("Unfortunately the coin you collected have been lost")));
        } catch (Exception e) {
            assertEquals(-1, 2);
            e.printStackTrace();
        }
    }

    @Test(expected = Exception.class)
    public void linkToResultsFailsCorrectly() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {
            scenario.onActivity(activity -> {
                activity.linkToResult(null);
            });
        }
    }


    @Test
    public void updateTextDisplaysGoodNumbers() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {
            scenario.onActivity(a -> a.updateText(1, 1, true));

            StringBuilder textBuilder = new StringBuilder();
            textBuilder = textBuilder.append("You have gathered").append(1).append("coins");
            textBuilder = textBuilder.append("\n");
            textBuilder = textBuilder.append("For a total score of ").append(1);
            String text = textBuilder.toString();
            Espresso.onView(withId(R.id.end_game_text)).check(matches(withText(text)));
        } catch (Exception e) {
            assertEquals(-1, 2);
            e.printStackTrace();
        }
    }


    @Test
    public void toMenuButtonWorks() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.end_game_button_to_menu)).perform(ViewActions.click());
            Thread.sleep(2000);
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            intended(hasComponent(MenuActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test

    public void endGameUpdatesUserScores() {
        Intent endGameIntent = getEndGameIntent();
        User user = getUser();
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(endGameIntent)) {
            try {
                Thread.sleep(5000);
            }
            catch (Exception e){
                fail();
            }
            CountDownLatch updated = new CountDownLatch(1);
            UserDatabaseProxy userdb = new UserDatabaseProxy();

            userdb.getUserTask(user.getUserId()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User p = userdb.getUserFromTask(task);
                    assertEquals(1,p.getNumberOfPlayedGames());
                    assertEquals(1,p.getNumberOfDiedGames());
                    assertEquals(3,p.getMaxScoreInGame());
                    updated.countDown();
                }
            });
            updated.await(5L, TimeUnit.SECONDS);
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test
    public void launchIntentWithScoreOfCoins() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Intent endGameIntent = new Intent(appContext, EndGameActivity.class);
        ArrayList<Integer> coins = new ArrayList<>();
        endGameIntent.putExtra("score", 3);
        endGameIntent.putExtra("numberOfCollectedCoins", 2);

        endGameIntent.putExtra("playerId", "1234567891");
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(endGameIntent)) {
            StringBuilder textBuilder = new StringBuilder();
            textBuilder = textBuilder.append("You have gathered").append(2).append("coins");
            textBuilder = textBuilder.append("\n");
            textBuilder = textBuilder.append("For a total score of ").append(3);
            String text = textBuilder.toString();
            Espresso.onView(withId(R.id.end_game_text)).check(matches(withText(text)));

        }

    }


    @Test
    public void toLeaderboardButtonWorks() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.end_game_button_to_results)).perform(ViewActions.click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            intended(hasComponent(LeaderboardActivity.class.getName()));
            Intents.release();
        }

    }


}
