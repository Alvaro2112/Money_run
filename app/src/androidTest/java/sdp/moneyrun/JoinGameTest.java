package sdp.moneyrun;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import sdp.moneyrun.menu.JoinGameImplementation;

import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
public class JoinGameTest {

    @Test
    public void constructorFailsOnNullUser(){
        try{
            new JoinGameImplementation(null, null, null, null, null, true, 1);
        }catch (IllegalArgumentException e){
            return;
        }
        fail();
    }


}
