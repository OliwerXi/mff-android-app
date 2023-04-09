package com.github.oliwersdk.mff.network.netty.packet.in;

import com.github.oliwersdk.mff.model.User;
import com.github.oliwersdk.mff.network.netty.packet.Packet;

public final class UserDisconnectPacket implements Packet {
  private User user;

  public UserDisconnectPacket() {}

  public User user() {
    return this.user;
  }

  @Override
  public byte code() {
    return In.USER_DISCONNECT;
  }
}