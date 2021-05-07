package sdp.moneyrun.map;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;

import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;

import org.junit.Test;

import sdp.moneyrun.R;
import sdp.moneyrun.ui.map.OfflineMapActivity;
import sdp.moneyrun.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

public class OfflineMapTest {
    private Intent getStartIntent() {
        User currentUser = new User("999", "CURRENT_USER", "Epfl"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), OfflineMapActivity.class);
        toStart.putExtra("user", currentUser);
        return toStart;
    }

    @Test
    public void closeButtonWorksTest() {

        try (ActivityScenario<OfflineMapActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(withId(R.id.close_map_offline)).perform(ViewActions.click());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(Lifecycle.State.DESTROYED, scenario.getState());

            Intents.release();
        }
    }


    @Test
    public void downloadsOneMapTest() {

        try (ActivityScenario<OfflineMapActivity> scenario = ActivityScenario.launch(getStartIntent())) {

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(activity -> {
                OfflineManager offlineManager = OfflineManager.getInstance(activity.getApplicationContext());
                offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback(){
                                                      @Override
                                                      public void onList(OfflineRegion[] offlineRegions) {
                                                          if(offlineRegions.length == 1){
                                                              assertEquals(offlineRegions.length == 1,true);
                                                              assertEquals(activity.getHasFoundMap(),true);

                                                          }
                                                          else{
                                                              assertEquals(activity.getHasFoundMap(),false);
                                                          }
                                                       }
                                                      @Override
                                                      public void onError(String error) {
                                                      }
                                                  }
                );

            });
        }catch (Exception e) {
            e.printStackTrace();
            assertEquals(1,2);
        }

    }

}
