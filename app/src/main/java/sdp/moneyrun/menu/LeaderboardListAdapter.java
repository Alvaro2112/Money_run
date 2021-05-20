package sdp.moneyrun.menu;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.player.Player;

public class LeaderboardListAdapter extends ArrayAdapter<Player> {

    public LeaderboardListAdapter(Activity context, List<Player> playerList) {
        super(context, 0, playerList);
    }

    @Nullable
    public View getView(int position, @Nullable View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_item_layout, parent, false);

        Player player = getItem(position);

        return Helpers.getView(position, view, player);
    }
}