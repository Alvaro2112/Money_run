package sdp.moneyrun.map;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;

import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.nio.charset.Charset;

import sdp.moneyrun.R;
import sdp.moneyrun.ui.map.OfflineMapDownloaderActivity;
import sdp.moneyrun.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

public class OfflineMapDownloaderTest {

    private Intent getStartIntent() {
        User currentUser = new User(999, "CURRENT_USER", "Epfl"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), OfflineMapDownloaderActivity.class);
        toStart.putExtra("user", currentUser);
        return toStart;
    }

    @Test
    public void DownloadsAfterStartingActivityTest() {
        try (ActivityScenario<OfflineMapDownloaderActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a -> {
                assertEquals(a.getHasStartedDownload(),true);
            });
        }catch (Exception e) {
        e.printStackTrace();
        assertEquals(1,2);
        }
    }

    @Test
    public void closeButtonWorksTest() {

        try (ActivityScenario<OfflineMapDownloaderActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(withId(R.id.downloader_exit)).perform(ViewActions.click());
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
        try (ActivityScenario<OfflineMapDownloaderActivity> scenario = ActivityScenario.launch(getStartIntent())) {
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
                        assertEquals(offlineRegions.length == 1,true);
                        String name;
                        try {
                            name = new JSONObject(new String(offlineRegions[0].getMetadata(), Charset.forName(OfflineMapDownloaderActivity.JSON_CHARSET)))
                                    .getString(OfflineMapDownloaderActivity.JSON_FIELD_REGION_NAME);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            name = " ";
                        }
                        assertEquals(name , "offline map");
                    }
                    @Override
                    public void onError(String error) {
                    }
                }
            );
                assertEquals(activity.getIsEndNotified(),true);

        });
    }catch (Exception e) {
            e.printStackTrace();
            assertEquals(1,2);
        }
    }
}
