package sdp.moneyrun.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import sdp.moneyrun.MainActivity;

public class RequestPermissions {
    private final Activity activity;
    private final String[] permissions;
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher;

    private final String requestMessage;
    private final boolean forceShowRequest;

    public RequestPermissions(AppCompatActivity activity, String requestMessage, boolean forceShowRequest, String... permissions){
        if(activity == null) {
            throw new IllegalArgumentException("Activity should not be null.");
        }
        if(permissions == null){
            throw new IllegalArgumentException("Permissions should not be null.");
        }
        for(String permission : permissions){
            if(permission == null){
                throw new IllegalArgumentException("A permission should not be null.");
            }
        }

        this.activity = activity;
        this.permissions = permissions;
        this.requestPermissionsLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
            for(String permission : map.keySet()){
                boolean isGranted = map.get(permission);
                if (isGranted) {
                    System.out.println("Permission" + permission + " granted.");
                } else {
                    System.out.println("Permission" + permission + " denied.");
                }
            }

        });

        this.requestMessage = requestMessage;
        this.forceShowRequest = forceShowRequest;
    }

    public boolean requestPermission(){
        if(!hasPermissions()){
            if(shouldShowRequestPermissionsRationale() || forceShowRequest){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                CharSequence positiveButtonText = "OK";
                CharSequence negativeButtonText = "CANCEL";
                DialogInterface.OnClickListener positiveButtonListener = (dialogInterface, i) -> requestPermissionsLauncher.launch(permissions);
                DialogInterface.OnClickListener negativeButtonListener = (dialogInterface, i) -> {};

                builder.setTitle("Grant permissions");
                builder.setMessage(requestMessage);
                builder.setPositiveButton(positiveButtonText, positiveButtonListener);
                builder.setNegativeButton(negativeButtonText, negativeButtonListener);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }else{
                requestPermissionsLauncher.launch(permissions);
            }
        }

        return hasPermissions();
    }

    private boolean hasPermissions(){
        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(
                    activity.getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldShowRequestPermissionsRationale(){
        for(String permission : permissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
               return true;
            }
        }
        return false;
    }
}