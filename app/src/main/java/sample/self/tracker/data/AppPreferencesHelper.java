package sample.self.tracker.data;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferencesHelper {

  private final String prefFileName = "SelfTrackerPref";
  private static final String PREF_KEY_USER_ID = "PREF_KEY_USER_ID";
  private static final String PREF_KEY_USER_PASSWORD = "PREF_KEY_USER_PASSWORD";

  private final SharedPreferences sharedPreferences;

  public AppPreferencesHelper(Context context) {
    sharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
  }

  public String getUserId() {
    return sharedPreferences.getString(PREF_KEY_USER_ID, null);
  }

  public void setUserId(String userId) {
    sharedPreferences.edit().putString(PREF_KEY_USER_ID, userId).apply();
  }

  public String getPassword() {
    return sharedPreferences.getString(PREF_KEY_USER_PASSWORD, null);
  }

  public void setPassword(String password) {
    sharedPreferences.edit().putString(PREF_KEY_USER_PASSWORD, password).apply();
  }
}