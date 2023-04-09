package com.github.oliwersdk.mff.util;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.core.util.Pair;

public final class Storage {
  private Storage() {
    throw new RuntimeException("must not instantiate Storage");
  }

  public static Pair<String, Long> extractFileInfo(ContentResolver resolver, Uri uri) {
    if (uri == null || !uri.getScheme().equals("content"))
      return null;

    String name = null;
    long size = 0L;

    try (final var cursor = resolver.query(uri, null, null, null, null)) {
      if (!cursor.moveToFirst()) {
        return null;
      }

      final var nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
      if (nameIndex >= 0)
        name = cursor.getString(nameIndex);

      final var sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
      if (sizeIndex >= 0)
        size = cursor.getLong(sizeIndex);
    }

    if (name == null) {
      name = uri.getPath();
      final int cut;

      if ((cut = name.lastIndexOf('/')) != -1) {
        name = name.substring(cut + 1);
      }
    }
    return new Pair<>(name, size);
  }
}