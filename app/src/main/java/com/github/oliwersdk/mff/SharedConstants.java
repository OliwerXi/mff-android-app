package com.github.oliwersdk.mff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface SharedConstants {
  // server endpoint(s)
  String DEBUG_SERVER_HOST = "192.168.1.8";

  // SERIALIZATION & DESERIALIZATION
  Gson JSON_MAPPER = new GsonBuilder()
    .disableHtmlEscaping()
    .create();

  // "session"-global settings
  GlobalSettings GLOBAL_SETTINGS =
    new GlobalSettings();

  // json stringify an unknown object
  static String stringifyJson(Object object) {
    return JSON_MAPPER.toJson(object);
  }
}