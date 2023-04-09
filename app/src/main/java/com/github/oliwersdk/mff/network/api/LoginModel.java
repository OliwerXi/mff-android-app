package com.github.oliwersdk.mff.network.api;

public final class LoginModel {
  public static final class Response {
    private String token;

    public Response() {
      this.token = null;
    }

    public void setToken(String token) {
      this.token = token;
    }

    public String token() {
      return this.token;
    }
  }

  private final String email;
  private final String password;

  public LoginModel(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String email() {
    return this.email;
  }

  public String password() {
    return this.password;
  }
}