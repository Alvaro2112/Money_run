package sdp.moneyrun.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sdp.moneyrun.R;
import sdp.moneyrun.user.User;

public class FriendListListAdapter extends ListAdapterWithUser {

    public FriendListListAdapter(Activity context, List<User> userList, User user) {
        super(context,userList, user);
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item_layout, parent, false);
        User userRequested = getItem(position);

        TextView userNameView = view.findViewById(R.id.friend_list_name);
        TextView playedView = view.findViewById(R.id.friend_list_n_played_result);
        TextView maxScoreView = view.findViewById(R.id.friend_list_logo_max_score_result);

        userNameView.setText(String.valueOf(userRequested.getName()));
        playedView.setText(String.valueOf(userRequested.getNumberOfPlayedGames()));
        maxScoreView.setText(String.valueOf(userRequested.getMaxScoreInGame()));

        //Define a tag to recognize the user.
        view.setTag(userRequested.getUserId());

        return view;
    }
}
