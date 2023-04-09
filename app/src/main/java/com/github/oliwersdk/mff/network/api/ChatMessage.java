package com.github.oliwersdk.mff.network.api;

import com.github.oliwersdk.mff.model.User;

public final class ChatMessage {
  private long id;
  private String text;
  private long createdAt;
  private User sender;

  public ChatMessage() {}

  public User sender() {
    return this.sender;
  }

  public long id() {
    return this.id;
  }

  public String text() {
    return this.text;
  }

  public long createdAt() {
    return this.createdAt;
  }
}