package com.github.oliwersdk.mff.network.netty.packet.in;

import com.github.oliwersdk.mff.model.User;
import com.github.oliwersdk.mff.network.netty.packet.Packet;

public final class InChatMessagePacket implements Packet {
  private User sender;
  private long id;
  private String text;
  private long sentAt;

  public InChatMessagePacket() {}

  public User sender() {
    return this.sender;
  }

  public long id() {
    return this.id;
  }

  public String text() {
    return this.text;
  }

  public long sentAt() {
    return this.sentAt;
  }

  @Override
  public byte code() {
    return In.CHAT_MESSAGE;
  }
}