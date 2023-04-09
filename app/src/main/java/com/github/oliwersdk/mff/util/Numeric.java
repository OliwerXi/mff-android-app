package com.github.oliwersdk.mff.util;

import static java.lang.Byte.parseByte;
import static java.util.Optional.ofNullable;

import java.util.Optional;

public final class Numeric {
  private Numeric() {
    throw new RuntimeException("must not instantiate Numeric");
  }

  public static Optional<Byte> tryParseByte(String value) {
    return ofNullable(
      !value.matches("^(-[1-9]|-12[0-8]|-?[1-9][0-9]|-?1[01][0-9]|[0-9]|12[0-7])$")
        ? null
        : parseByte(value)
    );
  }

  public static byte tryParseByteOrElse(String value, byte or) {
    return tryParseByte(value).orElse(or);
  }

  public static byte tryParseByteOrElse(String value, int or) {
    return tryParseByteOrElse(value, (byte) or);
  }
}