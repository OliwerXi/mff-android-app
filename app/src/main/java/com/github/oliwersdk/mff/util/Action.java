package com.github.oliwersdk.mff.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Action {
  private Action() {
    throw new RuntimeException("must not instantiate Action");
  }

  public static <T> T modify(T value, Consumer<T> modification) {
    modification.accept(value);
    return value;
  }

  public static <T> T modify(Supplier<T> supplier, Consumer<T> modification) {
    return modify(supplier.get(), modification);
  }
}