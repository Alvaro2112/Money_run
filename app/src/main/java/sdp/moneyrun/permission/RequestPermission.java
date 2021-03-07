package sdp.moneyrun.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

public class RequestPermission {
    private final Context context;
    private final String permission;

    public RequestPermission(Context context, String permission){
        if(context == null) {
            throw new IllegalArgumentException("Context should not be null.");
        }
        if(permission == null){
            throw new IllegalArgumentException("Permission should not be null.");
        }

        this.context = context;
        this.permission = permission;
    }

    public void requestPermission(){
        return;
    }
}