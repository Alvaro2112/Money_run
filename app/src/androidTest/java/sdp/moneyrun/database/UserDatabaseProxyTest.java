package sdp.moneyrun.database;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import sdp.moneyrun.user.User;

@RunWith(AndroidJUnit4.class)
public class UserDatabaseProxyTest {

    @Test(expected = IllegalArgumentException.class)
    public void putUserFailsCorrectly1(){
        UserDatabaseProxy userDatabaseProxy = new UserDatabaseProxy();
        userDatabaseProxy.putUser(new User(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putUserFailsCorrectly2(){
        UserDatabaseProxy userDatabaseProxy = new UserDatabaseProxy();
        userDatabaseProxy.putUser(null, task -> {
        });
    }

    @Test
    public void updatedFriendListFromDatabaseFailsCorrectly(){
        UserDatabaseProxy userDatabaseProxy = new UserDatabaseProxy();
        Assert.assertNull(userDatabaseProxy.updatedFriendListFromDatabase(null));
    }

}
