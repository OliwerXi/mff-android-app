package com.github.oliwersdk.mff.network.netty.packet.in;

import com.github.oliwersdk.mff.model.User;
import com.github.oliwersdk.mff.network.netty.packet.Packet;

public final class AuthenticatedPacket implements Packet {
  private User user;
  private long onlineUserCount;

  public AuthenticatedPacket() {}

  public User user() {
    return this.user;
  }

  public long onlineUserCount() {
    return this.onlineUserCount;
  }

  @Override
  public byte code() {
    return In.AUTHENTICATED;
  }
}