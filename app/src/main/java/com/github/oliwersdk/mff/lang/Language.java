package com.github.oliwersdk.mff.lang;

import static com.github.oliwersdk.mff.util.StringHelper.capitalizeFirst;

import com.github.oliwersdk.mff.lang.impl.English;
import com.github.oliwersdk.mff.lang.impl.Swedish;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public enum Language {
  ENGLISH(English::new),
  SWEDISH(Swedish::new);

  public static final List<Language> VALUES = List.of(values());
  private final Supplier<LanguageProps> propsSupplier;
  private final String prettyName;

  Language(Supplier<LanguageProps> propsSupplier) {
    this.propsSupplier = propsSupplier;
    this.prettyName = capitalizeFirst(name());
  }

  public LanguageProps props() {
    return propsSupplier.get();
  }

  public String prettified() {
    return this.prettyName;
  }

  public static Comparator<Language> comparator() {
    return (first, second) -> Character.compare(
      second.prettified().charAt(0),
      first.prettified().charAt(0)
    );
  }

  @FunctionalInterface
  public interface RefreshableComponent {
    void update(LanguageProps props);
  }
}