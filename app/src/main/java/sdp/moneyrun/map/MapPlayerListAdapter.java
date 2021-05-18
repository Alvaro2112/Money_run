package sdp.moneyrun.map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.player.PlayerListAdapter;

public class MapPlayerListAdapter extends PlayerListAdapter {

    public MapPlayerListAdapter(Activity context, ArrayList<Player> playerList) {
        super(context, playerList);
    }

    @Nullable
    public View getView(int position, @Nullable View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_item_layout, parent, false);

        Player player = getItem(position);

        return Helpers.getView(position, view, player);
    }
}