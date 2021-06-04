package sdp.moneyrun;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.BeforeClass;
import org.junit.Test;

import sdp.moneyrun.game.GameRepresentation;
import sdp.moneyrun.location.LocationRepresentation;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

public class HelpersInstrumentedTest {

    @BeforeClass
    public static void setPersistence() {
        if (!MainActivity.calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }

    private Intent getStartIntent() {
        User currentUser = new User("999", "CURRENT_USER"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), MenuActivity.class);
        toStart.putExtra("user", currentUser);
        return toStart;
    }

    @Test(expected = IllegalArgumentException.class)
    public void joinLobbyFromJoinButtonThrowsExceptionOnNullGameId() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            LocationRepresentation locationRep = new LocationRepresentation(10, 10);
            GameRepresentation gameRep = new GameRepresentation(null, "game", 1, 10, locationRep);
            User currentUser = new User("999", "CURRENT_USER"
                    , 0, 0, 0);

            scenario.onActivity(a -> Helpers.joinLobbyFromJoinButton(gameRep,
                    FirebaseDatabase.getInstance().getReference(),
                    a,
                    currentUser, null));
        }
    }
}
