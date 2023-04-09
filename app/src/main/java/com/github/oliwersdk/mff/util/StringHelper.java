package com.github.oliwersdk.mff.util;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

public final class StringHelper {
  private StringHelper() {
    throw new RuntimeException("must not instantiate StringHelper");
  }

  public static String capitalizeFirst(String value) {
    final int len;
    if (value == null || (len = value.length()) == 0)
      return value;

    final var builder = new StringBuilder()
      .append(toUpperCase(value.charAt(0)));

    for (var index = 1; index < len; index++)
      builder.append(toLowerCase(value.charAt(index)));

    return builder.toString().trim();
  }
}