package sample.self.tracker.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import sample.self.tracker.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class NotificationUtils {

  private static final String NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE =
      "SelfTrackerAppNotificationChannelId";

  public static Notification showNotificationForLocation(Context context, Location location) {
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(context, createNotificationChannelId(context,
            (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)));
    return notificationBuilder.setOngoing(false)
        .setSmallIcon(R.drawable.ic_my_current_location)
        .setContentText(
            context.getString(R.string.notification_message_location, location.getLatitude(),
                location.getLongitude()))
        .setPriority(PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .build();
  }

  private static String createNotificationChannelId(Context context,
      NotificationManager notificationManager) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel notificationChannel =
          new NotificationChannel(NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE,
              context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
      // omitted the LED color
      notificationChannel.setImportance(NotificationManager.IMPORTANCE_NONE);
      notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
      notificationManager.createNotificationChannel(notificationChannel);
      return NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE;
    } else {
      // notification channel is not required in devices below Android Oreo
      return "";
    }
  }
}
