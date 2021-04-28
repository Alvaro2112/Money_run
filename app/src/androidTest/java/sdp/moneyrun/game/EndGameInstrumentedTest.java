package sdp.moneyrun.game;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import sdp.moneyrun.R;
import sdp.moneyrun.database.PlayerDatabaseProxy;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.game.EndGameActivity;
import sdp.moneyrun.ui.menu.MenuActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

public class EndGameInstrumentedTest {

    @BeforeClass
    public static void setPersistence(){
        if(!MainActivity.calledAlready){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }

    private  long ASYNC_CALL_TIMEOUT = 5L;

    @Test
    public void updateTextFailsWithoutLists() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {

        Espresso.onView(ViewMatchers.withId(R.id.end_game_text)).check(matches(withText("Unfortunately the coin you collected have been lost")));
        }
        catch (Exception e){
                assertEquals(-1,2);
                e.printStackTrace();
            }
        }

    @Test
    public void updateTextDisplaysGoodNumbers() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {
            scenario.onActivity(a -> {
                a.updateText(1, 1,true);
            });

            StringBuilder textBuilder = new StringBuilder();
            textBuilder = textBuilder.append("You have gathered").append(1).append("coins");
            textBuilder = textBuilder.append("\n");
            textBuilder = textBuilder.append("For a total score of ").append(1);
            String text = textBuilder.toString();
            Espresso.onView(withId(R.id.end_game_text)).check(matches(withText(text)));
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void updatePlayerUpdateScore() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {
            int playerid = 98732;
            final Player player = new Player(playerid, "O", "FooBarr", 0, 0,5);
            final PlayerDatabaseProxy db = new PlayerDatabaseProxy();
            db.putPlayer(player);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Player p = snapshot.getValue(Player.class);
                    player.setScore(3*p.getScore() + player.getScore(), false);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    assert(false);
                }
            };
            scenario.onActivity(a -> {
                        Player p = a.updatePlayer(playerid,10);
                    });
            db.addPlayerListener(player,listener );


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            db.removePlayerListener(player, listener);

            assertEquals(35,player.getScore());
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void launchIntentWithListOfCoins() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Intent endGameIntent = new Intent( appContext,EndGameActivity.class);
        ArrayList<Integer> coins = new ArrayList<>();
        coins.add(1);
        endGameIntent.putExtra("collectedCoins",coins);
        endGameIntent.putExtra("playerId",10);
        try(ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(endGameIntent)) {
            StringBuilder textBuilder = new StringBuilder();
            textBuilder = textBuilder.append("You have gathered").append(1).append("coins");
            textBuilder = textBuilder.append("\n");
            textBuilder = textBuilder.append("For a total score of ").append(1);
            String text = textBuilder.toString();
            Espresso.onView(withId(R.id.end_game_text)).check(matches(withText(text)));

        }

    }

    @Test
    public void toMenuButtonWorks() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.end_game_button_to_menu)).perform(ViewActions.click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            intended(hasComponent(MenuActivity.class.getName()));
            Intents.release();

        }

    }


}
