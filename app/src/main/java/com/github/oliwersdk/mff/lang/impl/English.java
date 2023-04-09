package com.github.oliwersdk.mff.lang.impl;

import com.github.oliwersdk.mff.lang.Language;
import com.github.oliwersdk.mff.lang.LanguageProps;

public final class English implements LanguageProps {
  @Override
  public String selectNewLanguageTitle() {
    return "Select a new language";
  }

  @Override
  public String chooseANewAvatarTitle() {
    return "Choose a new avatar";
  }

  @Override
  public String emailAddressLabel() {
    return "Email Address";
  }

  @Override
  public String passwordLabel() {
    return "Password";
  }

  @Override
  public String loginButtonText() {
    return "Sign In";
  }

  @Override
  public String clickHereToRegisterText() {
    return "Don't own an account? Click here.";
  }

  @Override
  public String settingsLanguageButtonText() {
    return "Language";
  }

  @Override
  public String sendMessageChatButtonText() {
    return "Send";
  }

  @Override
  public String chatNavbarButtonText() {
    return "Chat";
  }

  @Override
  public String settingsNavbarButtonText() {
    return "Settings";
  }

  @Override
  public String languageName(Language language) {
    return language.prettified();
  }

  @Override
  public Language type() {
    return Language.ENGLISH;
  }
}