package sdp.moneyrun.player;

import android.app.Activity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public abstract class PlayerListAdapter extends ArrayAdapter<Player> {

    public PlayerListAdapter(Activity context, ArrayList<Player> playerList) {
        super(context, 0, playerList);
    }

}