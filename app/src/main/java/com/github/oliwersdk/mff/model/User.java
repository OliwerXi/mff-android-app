package com.github.oliwersdk.mff.model;

import static com.github.oliwersdk.mff.GlobalSettings.IS_PRODUCTION;
import static com.github.oliwersdk.mff.SharedConstants.DEBUG_SERVER_HOST;
import static java.lang.String.format;

import java.util.UUID;

public final class User {
  private UUID id;
  private String username;
  private String displayName;
  private String email;
  private boolean hasAvatar;
  private long createdAt;

  public User() {}

  public UUID id() {
    return this.id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String username() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String displayName() {
    return this.displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String displayOrUserName() {
    return displayName != null && !displayName.isEmpty() ? displayName : username;
  }

  public String email() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean hasAvatar() {
    return this.hasAvatar;
  }

  public String getAvatarLink() {
    return avatarLinkOf(this.id, this.hasAvatar);
  }

  public void setHasAvatar(boolean hasAvatar) {
    this.hasAvatar = hasAvatar;
  }

  public long createdAt() {
    return this.createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public static String avatarLinkOf(UUID id, boolean hasAvatar) {
    return !hasAvatar ? "https://stonegatesl.com/wp-content/uploads/2021/01/avatar.jpg"
      : format(
        "%s://%s/user/avatar/%s",
        IS_PRODUCTION ? "https" : "http",
        IS_PRODUCTION ? "assets.mff.support" : format("%s:%s", DEBUG_SERVER_HOST, 8082),
        id
      );
  }
}