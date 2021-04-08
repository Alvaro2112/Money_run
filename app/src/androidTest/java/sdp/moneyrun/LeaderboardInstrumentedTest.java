package sdp.moneyrun;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LeaderboardInstrumentedTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("sdp.moneyrun", appContext.getPackageName());
    }

    @Test
    public void addPlayerWorks(){
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{

                //Address was not set here before I don't know why
                Player player = new Player(123, "Tess", "SomeAdress", 0,0);
                player.setScore(8008);
                a.addPlayer(player);

                assertEquals(a.getPlayerList().size(),6);
            });
        }
        catch (Exception e){
            assertEquals(2,1);
        }
    }

    @Test
    public void addPLayerAddsPlayerToView(){
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{
                //Address was not set here before I don't know why
                Player player = new Player(123, "Tess", "SomeAdress", 0,0);
                player.setScore(8008);
                a.addPlayer(player);
                assertEquals( a.getLdbAdapter().getCount(), 6);

            });
        }
    }


    @Test
    public void addPlayerNullThrowsException(){
        exception.expect(RuntimeException.class);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{
                a.addPlayer(null);
            });
        }

    }

    @Test
    public void AddPlayerListWorks(){
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{

                //Address was not set here before I don't know why
                Player player = new Player(123, "Tess", "SomeAdress", 0,0);
                player.setScore(8008);

                //Address was not set here before I don't know why
                Player player2 = new Player(12, "Rafa", "SomeAdress", 0,0);
                player2.setScore(8001);
                ArrayList<Player> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addPlayerList(list);
                assertEquals(a.getPlayerList().size(),7);
            });
        }
        catch (Exception e){
            e.printStackTrace();
            assertEquals(2,1);
        }
    }

    @Test
    public void AddPlayerListAddsAllPlayerToView(){
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{

                //Address was not set here before I don't know why
                Player player = new Player(123, "Tess", "SomeAdress", 0,0);
                player.setScore(8008);

                //Address was not set here before I don't know why
                Player player2 = new Player(12, "Rafa", "SomeAdress", 0,0);
                player2.setScore(8001);
                ArrayList<Player> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addPlayerList(list);
                assertEquals( a.getLdbAdapter().getCount(), 7);

            });
        }
    }


    @Test
    public void addPlayerListThrowsNullException(){
        exception.expect(RuntimeException.class);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{
                a.addPlayerList(null);
            });
        }
    }
    @Test
    public void testIfOneDummyPlayerIsSet(){
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{
                boolean check = false;
                for(Player p : a.getPlayerList()){
                    if(p.getName().equals("Dummy Player 4"))
                        check = true;
                }
                assertEquals(true,check);
            });
        }
    }
    @Test
    public void setMainPlayerGetsTheRightInfoAndSetsThePlayerAttributes(){
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{
               Intent intent = new Intent(a,LeaderboardActivity.class);
               String[] info = {"John","Here"};
               intent.putExtra("PlayerId",48390);
               intent.putExtra("playerId" + 48390, info);
                a.setMainPlayer(48390,info);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Player p = a.getUserPlayer();
               assertNotNull(p);
               assertNotNull(p.getName());
               assertNotNull(p.getAddress());
            });
        }
    }


}
