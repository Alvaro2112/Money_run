package sdp.moneyrun.map;

import android.content.Intent;

import androidx.annotation.NonNull;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OfflineMapTest {
    @NonNull
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
                offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                                                      @Override
                                                      public void onList(@NonNull OfflineRegion[] offlineRegions) {
                                                          if (offlineRegions.length == 1) {
                                                              assertEquals(1, offlineRegions.length);
                                                              assertTrue(activity.getHasFoundMap());

                                                          } else {
                                                              assertFalse(activity.getHasFoundMap());
                                                          }
                                                      }

                                                      @Override
                                                      public void onError(String error) {
                                                      }
                                                  }
                );

            });
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals(1, 2);
        }

    }

}
