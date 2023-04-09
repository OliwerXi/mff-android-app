package com.github.oliwersdk.mff.network;

import static com.github.oliwersdk.mff.GlobalSettings.IS_PRODUCTION;
import static com.github.oliwersdk.mff.SharedConstants.DEBUG_SERVER_HOST;
import static com.github.oliwersdk.mff.SharedConstants.JSON_MAPPER;
import static com.github.oliwersdk.mff.SharedConstants.stringifyJson;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static okhttp3.MediaType.get;
import static okhttp3.RequestBody.create;

import com.github.oliwersdk.mff.network.api.ChatMessage;
import com.github.oliwersdk.mff.network.api.LoginModel;

import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public final class ServerAPI {
  private static final short PORT = 8080;

  private static final OkHttpClient CLIENT      = new OkHttpClient();
  private static final MediaType    JSON_MEDIA  = get("application/json; charset=utf-8");
  private static final String       AUTH_HEADER = "Auth-Token";

  private ServerAPI() {
    throw new RuntimeException("must not instantiate ServerAPI");
  }

  public static LoginModel.Response requestLogin(String email, String password) {
    return send("/auth/login", LoginModel.Response.class, builder ->
      builder.post(create(stringifyJson(new LoginModel(email, password)), JSON_MEDIA))
    ).body;
  }

  public static boolean requestAvatarChange(String authToken, String extension, byte[] imageData) {
    return send("/users/avatar", Void.class, builder -> {
      final var multipart = new MultipartBody.Builder()
        .addFormDataPart(
          "avatar",
          format("avatar.%s", extension),
          create(imageData, get(format("image/%s", extension)))
        )
        .build();
      builder
        .addHeader(AUTH_HEADER, authToken)
        .patch(multipart);
    }).wasSuccessful();
  }

  public static ChatMessage[] getMessagesFromCursor(String authToken, long cursor) {
    final var response = send(
      "/chat/page",
      ChatMessage[].class,
      builder -> builder
        .addHeader(AUTH_HEADER, authToken)
        .addHeader("Cursor", valueOf(cursor))
        .get()
    );
    return response.wasSuccessful() ? response.body : new ChatMessage[0];
  }

  private static <R> Result<R> send(String path, Class<R> responseClass, Consumer<Request.Builder> mod) {
    final var request = new Request.Builder().url(
      IS_PRODUCTION
        ? format("https://api.mff.support%s", path)
        : format("http://%s:%s%s", DEBUG_SERVER_HOST, PORT, path)
    );

    // accept modification on the request builder
    mod.accept(request);

    try (final var response = CLIENT.newCall(request.build()).execute()) {
      R responseBody = null;

      try { responseBody = JSON_MAPPER.fromJson(response.body().string(), responseClass); }
      catch (Exception ignored) {}

      return new Result<>(response.code(), responseBody);
    } catch (Exception ex) {
      ex.printStackTrace();
      return new Result<>(-1, null);
    }
  }

  public static final class Result<T> {
    private final int statusCode;
    private final T body;

    private Result(int statusCode, T body) {
      this.statusCode = statusCode;
      this.body = body;
    }

    public int statusCode() {
      return this.statusCode;
    }

    public T body() {
      return this.body;
    }

    public boolean wasSuccessful() {
      return statusCode == 200 || statusCode == 202;
    }
  }
}