package com.github.oliwersdk.mff.util;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public final class ViewHelper {
  private ViewHelper() {
    throw new RuntimeException("must not instantiate ViewHelper");
  }

  public static void setBackgroundTint(View view, int colorId) {
    view.setBackgroundTintList(
      view
        .getResources()
        .getColorStateList(colorId)
    );
  }

  public static void setBackgroundTint(AppCompatActivity activity, int viewId, int colorId) {
    setBackgroundTint(activity.findViewById(viewId), colorId);
  }
}