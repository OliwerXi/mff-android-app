package com.github.oliwersdk.mff.ui.authenticated.fragment;

import static com.github.oliwersdk.mff.SharedConstants.GLOBAL_SETTINGS;
import static com.github.oliwersdk.mff.util.Action.modify;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.github.oliwersdk.mff.lang.Language;
import com.github.oliwersdk.mff.model.User;
import com.github.oliwersdk.mff.ui.authenticated.AuthenticatedActivity;

import java.util.function.Supplier;

public abstract class AuthorizedFragment extends Fragment implements Language.RefreshableComponent {
  private boolean isInitialized;
  private View view;
  protected Supplier<User> userSupplier;
  protected Supplier<String> authTokenSupplier;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    this.view = modify(inflater.inflate(layoutId(), container, false), this::init);
    this.isInitialized = true;
    update(GLOBAL_SETTINGS.language());
    return view;
  }

  protected abstract void init(View view);

  protected abstract int layoutId();

  public abstract AuthenticatedActivity.Page page();

  protected View view() {
    return this.view;
  }

  protected User user() {
    return userSupplier.get();
  }

  protected String authToken() {
    return authTokenSupplier.get();
  }

  public void setUserSupplier(Supplier<User> supplier) {
    if (supplier == null || userSupplier != null)
      return;
    this.userSupplier = supplier;
  }

  public void setAuthTokenSupplier(Supplier<String> supplier) {
    if (supplier == null || authTokenSupplier != null)
      return;
    this.authTokenSupplier = supplier;
  }

  public boolean initialized() {
    return this.isInitialized;
  }
}