package com.github.oliwersdk.mff;

import static com.github.oliwersdk.mff.lang.Language.ENGLISH;

import com.github.oliwersdk.mff.lang.Language;
import com.github.oliwersdk.mff.lang.LanguageProps;

import java.util.function.Consumer;

public final class GlobalSettings {
  public static final boolean IS_PRODUCTION = false;
  private LanguageProps language;
  private Consumer<LanguageProps> languageListener;

  GlobalSettings() {
    this.language = ENGLISH.props();
  }

  public LanguageProps language() {
    return this.language;
  }

  public void setLanguage(Language language) {
    if (language == null) // "reset"
      language = ENGLISH;

    if (this.language.type() == language)
      return;

    languageListener
      .accept((this.language = language.props()));
  }

  public void setLanguageListener(Consumer<LanguageProps> listener) {
    this.languageListener = listener;
  }
}