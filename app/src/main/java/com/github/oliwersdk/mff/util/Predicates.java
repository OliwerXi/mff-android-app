package com.github.oliwersdk.mff.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Predicates {
  private Predicates() {
    throw new RuntimeException("must not instantiate Predicates");
  }

  public static <Param> void doIf(Param with, Predicate<Param> predicate, Consumer<Param> execute) {
    if (!predicate.test(with))
      return;
    execute.accept(with);
  }

  public static <Param> Param firstIfOrElse(Predicate<Param> predicate, Param first, Supplier<Param> or) {
    return predicate.test(first) ? first : or.get();
  }
}