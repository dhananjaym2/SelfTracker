package sample.self.tracker.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import sample.self.tracker.R;

public class AppUtils {
  public static void showErrorMessage(Activity activity, String messageToShow,
      final DialogButtonCallBack dialogButtonCallback) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setTitle(activity.getString(R.string.app_name));
    builder.setMessage(messageToShow);
    builder.setNegativeButton(activity.getString(android.R.string.cancel),
        new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialogButtonCallback.onDialogButtonClick(
                new Intent().putExtra(DialogButtonCallBack.clickedValue, false));
          }
        });
    builder.setCancelable(false);
    builder.setPositiveButton(activity.getString(android.R.string.ok),
        new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialogButtonCallback.onDialogButtonClick(
                new Intent().putExtra(DialogButtonCallBack.clickedValue, true));
          }
        });
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  public static boolean isGooglePlayServicesAvailable(Context context) {
    GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
    int status = googleApiAvailability.isGooglePlayServicesAvailable(context);
    return status == ConnectionResult.SUCCESS;
  }
}