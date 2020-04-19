package sample.self.tracker.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class AppUtils {
  public static void showAlertMessage(Activity activity, String messageToShow,
      final DialogButtonCallBack dialogButtonCallback) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setMessage(messageToShow);
    builder.setCancelable(false);
    builder.setPositiveButton(activity.getString(android.R.string.ok),
        new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            if (dialogButtonCallback == null) {
              return;
            }
            dialogButtonCallback.onDialogButtonClick(
                new Intent().putExtra(DialogButtonCallBack.clickedValue, true));
          }
        });
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  public static void showAlertMessageWith2Buttons(Activity activity, String messageToShow,
      String positiveButtonText, String negativeButtonText,
      final DialogButtonCallBack dialogButtonCallback) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setMessage(messageToShow);
    builder.setNegativeButton(negativeButtonText,
        new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            if (dialogButtonCallback == null) {
              return;
            }
            dialogButtonCallback.onDialogButtonClick(
                new Intent().putExtra(DialogButtonCallBack.clickedValue, false));
          }
        });
    builder.setCancelable(false);
    builder.setPositiveButton(positiveButtonText,
        new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            if (dialogButtonCallback == null) {
              return;
            }
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