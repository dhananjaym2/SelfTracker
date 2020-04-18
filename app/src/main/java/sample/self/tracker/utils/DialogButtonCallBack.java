package sample.self.tracker.utils;

import android.content.Intent;

public interface DialogButtonCallBack {
  String clickedValue = "DialogButtonCallBack.clickedValue";

  void onDialogButtonClick(Intent intent);
}
