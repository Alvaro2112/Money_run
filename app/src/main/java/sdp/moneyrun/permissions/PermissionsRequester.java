package sdp.moneyrun.permissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * This class implements a permissions requester.
 * @author Arnaud Poletto
 */
public class PermissionsRequester {
    private final Activity activity;
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private final String[] permissions;

    private final String requestMessage;
    private final boolean forceShowRequest;

    /**
     * @param activity the current activity
     * @param requestPermissionsLauncher the permissions launcher
     * @param requestMessage the informative message for the user about requested permissions
     * @param forceShowRequest a boolean forcing the appearance of the informative message
     * @param permissions the permissions requested
     */
    public PermissionsRequester(
            AppCompatActivity activity,
            ActivityResultLauncher<String[]> requestPermissionsLauncher,
            String requestMessage,
            boolean forceShowRequest,
            String... permissions){
        if(activity == null) {
            throw new IllegalArgumentException("Activity should not be null.");
        }
        if(requestPermissionsLauncher == null){
            throw new IllegalArgumentException("Permission launcher should not be null.");
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
        this.requestPermissionsLauncher = requestPermissionsLauncher;
        this.permissions = permissions;

        this.requestMessage = requestMessage;
        this.forceShowRequest = forceShowRequest;
    }

    /**
     * Requests the permissions to the user.
     * @return true if all permissions have been granted, false otherwise
     */
    public boolean requestPermission(){
        if(!hasPermissions()){
            if(shouldShowRequestPermissionsRationale() || forceShowRequest){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                CharSequence positiveButtonText = "OK";
                CharSequence negativeButtonText = "CANCEL";
                DialogInterface.OnClickListener positiveButtonListener = (dialogInterface, i) -> requestPermissionsLauncher.launch(permissions);

                builder.setTitle("Grant permissions");
                builder.setMessage(requestMessage);
                builder.setPositiveButton(positiveButtonText, positiveButtonListener);
                builder.setNegativeButton(negativeButtonText, null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }else{
                requestPermissionsLauncher.launch(permissions);
            }
        }

        return hasPermissions();
    }

    /**
     * @return true if the permissions are already granted, false otherwise
     */
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

    /**
     * @return true if an informative message should be sent to the user about requested permissions, false otherwise
     */
    private boolean shouldShowRequestPermissionsRationale(){
        for(String permission : permissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
               return true;
            }
        }
        return false;
    }

    /**
     * @return the current activity
     */
    public Activity getActivity(){
        return activity;
    }

    /**
     * @return the permissions launcher
     */
    public ActivityResultLauncher<String[]> getRequestPermissionsLauncher(){
        return requestPermissionsLauncher;
    }

    /**
     * @return the permissions requested
     */
    public String[] getPermissions(){
        return permissions.clone();
    }

    /**
     * @return the informative message for the user about requested permissions
     */
    public String getRequestMessage() {
        return requestMessage;
    }

    /**
     * @return a boolean forcing the appearance of the informative message
     */
    public boolean getForceShowRequest(){
        return forceShowRequest;
    }
}