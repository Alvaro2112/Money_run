package sdp.moneyrun;

import android.Manifest;
import android.app.AlertDialog;
import android.widget.Button;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import sdp.moneyrun.permissions.PermissionsRequester;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PermissionRequesterInstrumentedTest {

    private final String coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();



    @Test
    public void requesterThrowsExceptionWhenActivityNull() {
        exception.expect(RuntimeException.class);

        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    null,
                    activity.getRequestPermissionsLauncher(),
                    "Test message",
                    false,
                    coarseLocation,
                    fineLocation);
        });
    }

    @Test
    public void requesterThrowsExceptionWhenLauncherNull() {
        exception.expect(RuntimeException.class);

        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    activity,
                    null,
                    "Test message",
                    false,
                    coarseLocation,
                    fineLocation);
        });
    }

    @Test
    public void requesterThrowsExceptionWhenMessageNull() {
        exception.expect(RuntimeException.class);

        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    activity,
                    activity.getRequestPermissionsLauncher(),
                    null,
                    false,
                    coarseLocation,
                    fineLocation);
        });
    }

    @Test
    public void requesterThrowsExceptionWhenNoPermission() {
        exception.expect(RuntimeException.class);

        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    activity,
                    activity.getRequestPermissionsLauncher(),
                    "Test message",
                    false);
        });
    }

    @Test
    public void requesterThrowsExceptionWhenPermissionNull() {
        exception.expect(RuntimeException.class);

        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    activity,
                    activity.getRequestPermissionsLauncher(),
                    "Test message",
                    false,
                    coarseLocation,
                    null);
        });
    }

    @Test
    public void requesterGetActivityReturnsRightObject(){
        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    activity,
                    activity.getRequestPermissionsLauncher(),
                    "Test message",
                    false,
                    coarseLocation,
                    fineLocation);

            assertEquals(activity, pr.getActivity());
        });
    }

    @Test
    public void requesterGetRequestPermissionsLauncherReturnsRightObject(){
        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    activity,
                    activity.getRequestPermissionsLauncher(),
                    "Test message",
                    false,
                    coarseLocation,
                    fineLocation);

            assertEquals(activity.getRequestPermissionsLauncher(), pr.getRequestPermissionsLauncher());
        });
    }

    @Test
    public void requesterGetPermissionsReturnsRightObject(){
        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    activity,
                    activity.getRequestPermissionsLauncher(),
                    "Test message",
                    false,
                    coarseLocation,
                    fineLocation);

            String[] expectedPermissions = {coarseLocation, fineLocation};
            assertEquals(expectedPermissions, pr.getPermissions());
        });
    }

    @Test
    public void requesterGetRequestMessageReturnsRightObject(){

        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    activity,
                    activity.getRequestPermissionsLauncher(),
                    "Test message",
                    false,
                    coarseLocation,
                    fineLocation);

            String expectedRequestMessage = "Test message";
            assertEquals(expectedRequestMessage, pr.getRequestMessage());
        });
    }

    @Test
    public void requesterGetForceShowRequestMessageReturnsRightObject(){
        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    activity,
                    activity.getRequestPermissionsLauncher(),
                    "Test message",
                    false,
                    coarseLocation,
                    fineLocation);

            assertEquals(false, pr.getForceShowRequest());
        });
    }

    @Test
    public void requesterRunsCorrectly(){
        activityRule.getScenario().onActivity(a -> {
            LoginActivity activity = (LoginActivity) a;
            PermissionsRequester pr = new PermissionsRequester(
                    activity,
                    activity.getRequestPermissionsLauncher(),
                    "Test message",
                    true,
                    coarseLocation,
                    fineLocation);

            pr.requestPermission();

            assertTrue(true);
        });
    }
}