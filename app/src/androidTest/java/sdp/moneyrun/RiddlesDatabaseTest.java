package sdp.moneyrun;

import android.content.Context;

import junit.framework.TestCase;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Test;

import sdp.moneyrun.database.RiddlesDatabase;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class RiddlesDatabaseTest extends TestCase {

    private Context context = ApplicationProvider.getApplicationContext();


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