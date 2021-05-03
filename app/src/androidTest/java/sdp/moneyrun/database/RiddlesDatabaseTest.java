package sdp.moneyrun.database;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.database.FirebaseDatabase;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import sdp.moneyrun.ui.MainActivity;


public class RiddlesDatabaseTest extends TestCase {

    private final Context context = ApplicationProvider.getApplicationContext();


    @BeforeClass
    public static void setPersistence(){
        if(!MainActivity.calledAlready){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }

    @Test
    public void testGetInstance() {
        RiddlesDatabase db = RiddlesDatabase.createInstance(context);
        assertEquals(RiddlesDatabase.getInstance(), db);
        RiddlesDatabase.reset();

    }

    public void testGetInstanceOnNonExistentThrowsException() {

        try
        {
            RiddlesDatabase db = RiddlesDatabase.getInstance();
            Assert.fail("Should have thrown Arithmetic exception");
        }
        catch(RuntimeException e)
        {
            //success
        }finally {
            RiddlesDatabase.reset();

        }
    }

    @Test
    public void testCreateInstanceFailsOnDoubleCreateInstance() {

        try {
            RiddlesDatabase db = RiddlesDatabase.createInstance(context);
            RiddlesDatabase db2 = RiddlesDatabase.createInstance(context);
            Assert.fail("Should have thrown Arithmetic exception");


        } catch (RuntimeException e) {
            assertEquals(1, 1);
        }finally {
            RiddlesDatabase.reset();

        }

    }

    @Test
    public void testCreateInstance() {
        RiddlesDatabase db = RiddlesDatabase.createInstance(context);
        RiddlesDatabase.reset();

    }


}