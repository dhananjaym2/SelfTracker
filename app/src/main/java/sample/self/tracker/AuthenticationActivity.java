package sample.self.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import sample.self.tracker.data.AppPreferencesHelper;
import sample.self.tracker.utils.AppTextUtils;
import sample.self.tracker.utils.AppUtils;
import sample.self.tracker.utils.DialogButtonCallBack;

public class AuthenticationActivity extends AppCompatActivity
    implements View.OnClickListener, DialogButtonCallBack {

  private boolean isPasswordVisible;
  private AppPreferencesHelper appPreferencesHelper;

  private TextView tv_loginOrRegisterMessage;
  private EditText et_userId, et_password;
  private ImageView showPasswordImage;
  private Button buttonLogin;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_authentication);

    appPreferencesHelper = new AppPreferencesHelper(this);

    initViews();
  }

  private void initViews() {
    tv_loginOrRegisterMessage = findViewById(R.id.tv_loginOrRegisterMessage);
    et_userId = findViewById(R.id.et_userId);
    et_password = findViewById(R.id.et_password);
    showPasswordImage = findViewById(R.id.showPasswordImageButton);
    showPasswordImage.setOnClickListener(this);
    buttonLogin = findViewById(R.id.buttonLogin);
    buttonLogin.setOnClickListener(this);

    if (appPreferencesHelper.getUserId() != null) {
      // a user is already registered with the app. Show the login related messages
      tv_loginOrRegisterMessage.setText(R.string.LoginMessage);
      buttonLogin.setText(R.string.Login);
    } else {
      // no user is registered with the app.
      tv_loginOrRegisterMessage.setText(R.string.RegisterMessage);
      buttonLogin.setText(R.string.Register);
    }
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.buttonLogin:
        if (areCredentialsValid()) {
          validateLogin();
        }
        break;

      case R.id.showPasswordImageButton:
        et_password.setInputType(AppTextUtils.getInputTypeForPasswordField(!isPasswordVisible));
        et_password.setSelection(et_password.getText().toString().length());
        showPasswordImage.setImageResource(getDrawableForPassword(isPasswordVisible));
        isPasswordVisible = !isPasswordVisible;
    }
  }

  private void validateLogin() {
    if (appPreferencesHelper.getUserId() != null) {
      // login
      doCredentialsMatch();
    } else {
      // register
      storeUserCredentialsLocally();
      launchNextPage();
    }
  }

  private void doCredentialsMatch() {
    if (appPreferencesHelper.getUserId().equalsIgnoreCase(et_userId.getText().toString().trim()) &&
        appPreferencesHelper.getPassword()
            .equalsIgnoreCase(et_password.getText().toString().trim())) {
      launchNextPage();
    } else {
      AppUtils.showAlertMessage(this, getString(R.string.login_failed), this);
    }
  }

  private void launchNextPage() {
    Intent mapIntent = new Intent(this, MapActivity.class);
    startActivity(mapIntent);
    finish();
  }

  private void storeUserCredentialsLocally() {
    appPreferencesHelper.setUserId(et_userId.getText().toString().trim());
    appPreferencesHelper.setPassword(et_password.getText().toString().trim());
  }

  private boolean areCredentialsValid() {
    final int userIdMinimumLength = 2;
    if (et_userId.getText().toString().trim().length() >= userIdMinimumLength) {
      final int passwordMinimumLength = 6;
      if (et_password.getText().toString().trim().length() >= passwordMinimumLength) {
        return true;
      } else {
        et_password.setError(getString(R.string.passwordLessThanMinLength, passwordMinimumLength));
      }
    } else {
      et_userId.setError(getString(R.string.userIdLessThanMinLength, userIdMinimumLength));
    }
    return false;
  }

  private int getDrawableForPassword(boolean isVisible) {
    if (isVisible) {
      return R.drawable.ic_show_password_eye;
    } else {
      return R.drawable.ic_hide_password_crossed_eye;
    }
  }

  @Override public void onDialogButtonClick(Intent intent) {
    if (intent != null && intent.hasExtra(DialogButtonCallBack.clickedValue)) {
      if (intent.getBooleanExtra(DialogButtonCallBack.clickedValue, false)) {
        et_password.setText("");
      }
    }
  }
}