package sdp.moneyrun.menu;

import android.app.Activity;
import android.widget.ArrayAdapter;

import java.util.List;

import sdp.moneyrun.user.User;

public class ListAdapterWithUser extends ArrayAdapter<User> {

    private final User currentUser;

    public ListAdapterWithUser(Activity context, List<User> userList, User user) {
        super(context,0 , userList);
        if(user == null){
            throw new IllegalArgumentException("user should not be null.");
        }

        this.currentUser = user;
    }

    /**
     * @return the current user
     */
    protected User getCurrentUser(){
        return currentUser;
    }
}
