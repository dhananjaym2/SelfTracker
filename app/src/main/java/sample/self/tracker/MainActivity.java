package sample.self.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private Handler handler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    startTimer();
  }

  private void startTimer() {
    handler = new Handler(this.getMainLooper());
    Runnable runnable = new Runnable() {
      @Override public void run() {
        launchNextPage();
      }
    };

    long timeAfterWhichLaunchNextScreen = 3000;
    handler.postDelayed(runnable, timeAfterWhichLaunchNextScreen);
  }

  private void launchNextPage() {
    if (!isFinishing()) {
      MainActivity.this.runOnUiThread(new Runnable() {
        @Override public void run() {
          Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
          startActivity(intent);
          finish();
        }
      });
    }
  }
}
