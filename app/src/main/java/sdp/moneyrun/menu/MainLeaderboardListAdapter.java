package sdp.moneyrun.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sdp.moneyrun.R;
import sdp.moneyrun.player.Player;

public class MainLeaderboardListAdapter extends ArrayAdapter<Player> {

    private final String[] rank = {"\uD83E\uDD47", "\uD83E\uDD48", "\uD83E\uDD49"};
    private final int COLOR_GOLD = Color.rgb(255, 204, 51);

    Player user;

    public MainLeaderboardListAdapter(Activity context, ArrayList<Player> playerList, Player user) {
        super(context,0 , playerList);
        if(user == null){
            throw new IllegalArgumentException("user should not be null.");
        }

        this.user = user;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.main_leaderboard_item_layout, null, true);
        Player player = getItem(position);
        TextView player_position = view.findViewById(R.id.main_player_position);
        TextView player_name = view.findViewById(R.id.main_player_name);
        TextView player_score = view.findViewById(R.id.main_player_score);

        String text_position;
        if(position < rank.length){
            text_position = rank[position];
        }else{
            text_position = " " + (position + 1);
        }

        player_position.setText(text_position);
        player_name.setText(String.valueOf(player.getName()));
        if(user.equals(player)){
            player_name.setTextColor(COLOR_GOLD);
            player_name.setTypeface(player_name.getTypeface(), Typeface.BOLD);
        }
        player_score.setText(String.valueOf(player.getScore()));

        return view;
    }
}
