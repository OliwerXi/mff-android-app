package com.github.oliwersdk.mff.lang;

public interface LanguageProps {
  // titles
  String selectNewLanguageTitle();
  String chooseANewAvatarTitle();

  // labels
  String emailAddressLabel();
  String passwordLabel();

  // buttons
  String loginButtonText();
  String clickHereToRegisterText();
  String settingsLanguageButtonText();
  String sendMessageChatButtonText();

  // navbar
  String chatNavbarButtonText();
  String settingsNavbarButtonText();

  // lang
  String languageName(Language language);

  // extra
  Language type();
}