package sdp.moneyrun;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class googleAuthenticationInstrumentedTest {
    FirebaseAuth.getInstance().useEmulator('10.0.2.2', 9099);

    @Rule
    public ActivityScenarioRule<Authentication> testRule = new ActivityScenarioRule<>(Authentication.class);




















}




