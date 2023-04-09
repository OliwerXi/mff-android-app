package com.github.oliwersdk.mff.task;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public final class AsyncImageLoader extends AsyncTask<String, Void, Bitmap> {
  // cache bitmaps
  private static final LoadingCache<String, Bitmap> TEMP_CACHE = CacheBuilder
    .newBuilder()
    .expireAfterAccess(5, TimeUnit.MINUTES)
    .build(new CacheLoader<>() {
      @NonNull
      @Override
      public Bitmap load(@NonNull String key) {
        try (final var stream = new URL(key).openStream()) {
          return BitmapFactory.decodeStream(stream);
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
    });

  @SuppressLint("StaticFieldLeak")
  private final ImageView view;

  private AsyncImageLoader(ImageView view) {
    this.view = view;
  }

  @Override
  protected Bitmap doInBackground(String... strings) {
    try {
      final String url;
      return strings.length == 0 || (url = strings[0]) == null
        ? null : TEMP_CACHE.get(url);
    } catch (Exception ignored) {
      return null;
    }
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {
    if (bitmap == null)
      return;
    view.setImageBitmap(bitmap);
  }

  public static void load(ImageView on, String url) {
    new AsyncImageLoader(on)
      .execute(url);
  }
}