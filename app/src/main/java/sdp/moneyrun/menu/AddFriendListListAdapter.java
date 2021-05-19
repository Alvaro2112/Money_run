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

public class AddFriendListListAdapter extends ArrayAdapter<User> {

    private final User user;

    public AddFriendListListAdapter(Activity context, List<User> userList, User user) {
        super(context,0 , userList);
        if(user == null){
            throw new IllegalArgumentException("user should not be null.");
        }

        this.user = user;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.add_friend_list_item_layout, null, true);
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
                userRequested.getUserId().equals(user.getUserId())){
            setInvalidButtonType(userButtonView);
        }else if(user.getFriendIdList().contains(userRequested.getUserId())){
            userButtonView.setTag(R.string.add_friend_tag_0, true);
            setUnfollowButtonType(userButtonView);
        }else{
            userButtonView.setTag(R.string.add_friend_tag_0, false);
            setFollowButtonType(userButtonView);
        }

        userButtonView.setOnClickListener(this::defineFollowButton);

        return view;
    }

    /**
     * Functionality for every follow/unfollow buttons to add or remove user from the friend list
     * @param view the button view
     */
    private void defineFollowButton(View view){
        Button button = (Button) view;
        if(button == null){
            throw new IllegalArgumentException("button view should not be null");
        }

        boolean hasFollowed = (boolean) button.getTag(R.string.add_friend_tag_0);

        //Update database and user friend list
        if(user.getUserId() == null){
            throw new IllegalArgumentException("user id should not be null");
        }
        UserDatabaseProxy db = new UserDatabaseProxy();
        db.getUserTask(user.getUserId()).addOnCompleteListener(task -> {
            User userFromDb = db.getUserFromTask(task);
            if(userFromDb == null){
                return;
            }

            String friendId = (String) view.getTag(R.string.add_friend_tag_1);
            if(friendId == null){
                return;
            }

            //Remove or add user from the user's friend list
            if(hasFollowed){
                userFromDb.removeFriendId(friendId);
                user.removeFriendId(friendId);
            }else{
                userFromDb.addFriendId(friendId);
                user.addFriendId(friendId);
            }
            db.putUser(userFromDb);

            //Update button look
            if(hasFollowed){
                setFollowButtonType(button);
            }else{
                setUnfollowButtonType(button);
            }

            //Reverse button state
            button.setTag(R.string.add_friend_tag_0, !hasFollowed);
        });
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
     * Define unfollow button type
     * @param button the button
     */
    private void setUnfollowButtonType(Button button){
        int color_light_gray = Color.rgb(220, 220, 220);

        button.setBackground(getButtonBackground(color_light_gray));
        button.setText(R.string.add_friend_button_unfollow_text);
    }

    /**
     * Define follow button type
     * @param button the button
     */
    private void setFollowButtonType(Button button){
        int color_gold = Color.rgb(255,215,0);

        button.setBackground(getButtonBackground(color_gold));
        button.setText(R.string.add_friend_button_follow_text);
    }

    /**
     * Define the look of the button.
     * @param backgroundColor the button's background color
     * @return the button's background
     */
    private GradientDrawable getButtonBackground(int backgroundColor){
        int radius = 20;
        int width = 5;

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(backgroundColor);
        gradientDrawable.setCornerRadius(radius);
        gradientDrawable.setStroke(width, Color.GRAY);

        return gradientDrawable;
    }
}
