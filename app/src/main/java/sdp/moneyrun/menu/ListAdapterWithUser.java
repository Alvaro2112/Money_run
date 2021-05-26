package sdp.moneyrun.menu;

import android.app.Activity;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.util.List;

import sdp.moneyrun.user.User;

public class ListAdapterWithUser extends ArrayAdapter<User> {

    @Nullable
    private final User currentUser;

    public ListAdapterWithUser(Activity context, List<User> userList, @Nullable User user) {
        super(context, 0, userList);
        if (user == null) {
            throw new IllegalArgumentException("user should not be null.");
        }

        this.currentUser = user;
    }

    /**
     * @return the current user
     */
    @Nullable
    protected User getCurrentUser() {
        return currentUser;
    }
}
