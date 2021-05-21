package sdp.moneyrun.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.R;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.user.User;

public class AddFriendListListAdapter extends ListAdapterWithUser {

    private final int color_light_gray = Color.rgb(220, 220, 220);
    private final int color_gold = Color.rgb(255,215,0);

    private final int CORNER_RADIUS = 20;
    private final int BUTTON_WIDTH = 5;

    public AddFriendListListAdapter(Activity context, List<User> userList, User user) {
        super(context,userList, user);
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.add_friend_list_item_layout, null, false);
        User userRequested = getItem(position);

        TextView userNameView = view.findViewById(R.id.add_friend_list_player_name);
        Button userButtonView = view.findViewById(R.id.add_friend_list_button);

        //Define name
        userNameView.setText(String.valueOf(userRequested.getName()));

        //Define button, add user id as button tag to retrieve it later
        userButtonView.setId(position);
        userButtonView.setTag(R.string.add_friend_tag_1, userRequested.getUserId());

        // Change button given some state: can follow, already followed or invalid
        if(userRequested.getUserId() == null ||
                userRequested.getUserId().equals(getCurrentUser().getUserId())){
            setInvalidButtonType(userButtonView);
        }else if(getCurrentUser().getFriendIdList().contains(userRequested.getUserId())){
            userButtonView.setTag(R.string.add_friend_tag_0, true);
            setButtonType(userButtonView, false);
        }else{
            userButtonView.setTag(R.string.add_friend_tag_0, false);
            setButtonType(userButtonView, true);
        }

        userButtonView.setOnClickListener(v -> defineFollowButton((Button) v));

        return view;
    }

    /**
     * Functionality for every follow/unfollow buttons to add or remove user from the friend list
     * @param button the button view
     */
    private void defineFollowButton(Button button){

        boolean hasFollowed = (boolean) button.getTag(R.string.add_friend_tag_0);

        //Update database and user friend list
        if(getCurrentUser().getUserId() == null){
            throw new IllegalArgumentException("user id should not be null");
        }
        UserDatabaseProxy db = new UserDatabaseProxy();
        db.getUserTask(getCurrentUser().getUserId()).addOnCompleteListener(task -> {
            User userFromDb = db.getUserFromTask(task);
            if(userFromDb == null){
                return;
            }

            String friendId = (String) button.getTag(R.string.add_friend_tag_1);
            if(friendId == null){
                return;
            }

            //Remove or add user from the user's friend list
            //and update button look
            if(hasFollowed){
                removeUserFromFriendList(userFromDb, friendId, button);
            }else{
                addUserFromFriendList(userFromDb, friendId, button);
            }
            db.putUser(userFromDb);

            //Reverse button state
            button.setTag(R.string.add_friend_tag_0, !hasFollowed);
        });
    }

    /**
     * Implementation to do when removing a user from friend list
     * @param userFromDb the user
     * @param friendId the friend id
     * @param button the button
     */
    private void removeUserFromFriendList(User userFromDb, String friendId, Button button){
        userFromDb.removeFriendId(friendId);
        getCurrentUser().removeFriendId(friendId);
        setButtonType(button, true);
    }

    /**
     * Implementation to do when adding a user from friend list
     * @param userFromDb the user
     * @param friendId the friend id
     * @param button the button
     */
    private void addUserFromFriendList(User userFromDb, String friendId, Button button){
        userFromDb.addFriendId(friendId);
        getCurrentUser().addFriendId(friendId);
        setButtonType(button, false);
    }

    /**
     * Define invalid button type
     * @param button the button
     */
    private void setInvalidButtonType(Button button){
        button.setEnabled(false);
        button.setVisibility(View.GONE);
    }

    /**
     * Define button type
     * @param button the button
     */
    private void setButtonType(Button button, boolean follow){
        int text = follow ? R.string.add_friend_button_follow_text : R.string.add_friend_button_unfollow_text;
        int color = follow ? color_gold : color_light_gray;

        button.setBackground(getButtonBackground(color));
        button.setText(text);
    }

    /**
     * Define the look of the button.
     * @param backgroundColor the button's background color
     * @return the button's background
     */
    private GradientDrawable getButtonBackground(int backgroundColor){

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(backgroundColor);
        gradientDrawable.setCornerRadius(CORNER_RADIUS);
        gradientDrawable.setStroke(BUTTON_WIDTH, Color.GRAY);

        return gradientDrawable;
    }
}
