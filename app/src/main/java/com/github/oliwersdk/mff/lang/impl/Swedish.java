package com.github.oliwersdk.mff.lang.impl;

import com.github.oliwersdk.mff.lang.Language;
import com.github.oliwersdk.mff.lang.LanguageProps;

public final class Swedish implements LanguageProps {
  @Override
  public String selectNewLanguageTitle() {
    return "Välj ett nytt språk";
  }

  @Override
  public String chooseANewAvatarTitle() {
    return "Välj en ny profilbild";
  }

  @Override
  public String emailAddressLabel() {
    return "Mail Adress";
  }

  @Override
  public String passwordLabel() {
    return "Lösenord";
  }

  @Override
  public String loginButtonText() {
    return "Logga In";
  }

  @Override
  public String clickHereToRegisterText() {
    return "Har du inget konto? Klicka här.";
  }

  @Override
  public String settingsLanguageButtonText() {
    return "Språk";
  }

  @Override
  public String sendMessageChatButtonText() {
    return "Skicka";
  }

  @Override
  public String chatNavbarButtonText() {
    return "Chatt";
  }

  @Override
  public String settingsNavbarButtonText() {
    return "Inställningar";
  }

  @Override
  public String languageName(Language language) {
    switch (language) {
      case ENGLISH: return "Engelska";
      case SWEDISH: return "Svenska";
      default: return null;
    }
  }

  @Override
  public Language type() {
    return Language.SWEDISH;
  }
}