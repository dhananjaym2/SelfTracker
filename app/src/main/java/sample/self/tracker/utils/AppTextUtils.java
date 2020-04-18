package sample.self.tracker.utils;

import android.text.InputType;

public class AppTextUtils {

  public static int getInputTypeForPasswordField(boolean shouldBeVisible) {
    if (shouldBeVisible) {
      return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
    } else {
      return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;
    }
  }
}
