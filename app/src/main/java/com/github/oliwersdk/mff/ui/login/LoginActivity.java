package com.github.oliwersdk.mff.ui.login;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.github.oliwersdk.mff.SharedConstants.GLOBAL_SETTINGS;
import static com.github.oliwersdk.mff.util.Predicates.doIf;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.oliwersdk.mff.R;
import com.github.oliwersdk.mff.lang.Language;
import com.github.oliwersdk.mff.lang.LanguageProps;
import com.github.oliwersdk.mff.network.ServerAPI;
import com.github.oliwersdk.mff.ui.authenticated.AuthenticatedActivity;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO: fix storage permissions
public final class LoginActivity extends AppCompatActivity
  implements Language.RefreshableComponent {
  private final AtomicBoolean isLoggingIn = new AtomicBoolean();
  private TextView loginEmailLabel;
  private TextView loginPasswordLabel;
  private Button loginButton;
  private TextView clickToRegister;
  private EditText loginEmail;
  private EditText loginPassword;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (supportRequestWindowFeature(Window.FEATURE_NO_TITLE)) {
      doIf(getSupportActionBar(), Objects::nonNull, ActionBar::hide);
    }

    // set the view and assign & handle layouts accordingly
    setContentView(R.layout.login_activity);
    this.loginEmailLabel = findViewById(R.id.loginEmailLabel);
    this.loginPasswordLabel = findViewById(R.id.loginPasswordLabel);
    this.loginButton = findViewById(R.id.loginButton);
    this.clickToRegister = findViewById(R.id.loginSwitchToRegister);
    this.loginEmail = findViewById(R.id.loginEmail);
    this.loginPassword = findViewById(R.id.loginPassword);
    initListeners();

    // language handles
    update(GLOBAL_SETTINGS.language());
    GLOBAL_SETTINGS.setLanguageListener(this::update);

    // TODO: check if the user has a token stored from earlier logins, if so... validate and if success, call handleAuthentication(token)
  }

  @Override
  public void update(LanguageProps props) {
    loginEmailLabel.setText(props.emailAddressLabel());
    loginPasswordLabel.setText(props.passwordLabel());
    loginButton.setText(props.loginButtonText());
    clickToRegister.setText(props.clickHereToRegisterText());
  }

  private void initListeners() {
    findViewById(R.id.loginButton).setOnClickListener(view -> {
      if (isLoggingIn.get())
        return;

      final var email = loginEmail.getText();
      if (email.length() == 0) {
        makeText(this, "Email is required.", LENGTH_SHORT).show();
        return;
      }

      final var password  = loginPassword.getText();
      if (password.length() == 0) {
        makeText(this, "Password is required.", LENGTH_SHORT).show();
        return;
      }

      isLoggingIn.set(true);
      supplyAsync(() -> ServerAPI.requestLogin(email.toString(), password.toString()))
        .thenAccept(response -> {
          isLoggingIn.set(false);
          if (response == null || response.token() == null) {
            toast("Invalid credentials.");
            return;
          }
          handleAuthentication(response.token());
        });
    });
  }

  private void handleAuthentication(String token) {
    final var intent = new Intent(this, AuthenticatedActivity.class);
    intent.putExtra("token", token);
    startActivity(intent);
    finish();
  }

  private void toast(String text) {
    if (getMainLooper().isCurrentThread())
      makeText(this, text, LENGTH_SHORT).show();
    else
      runOnUiThread(() -> makeText(this, text, LENGTH_SHORT).show());
  }
}