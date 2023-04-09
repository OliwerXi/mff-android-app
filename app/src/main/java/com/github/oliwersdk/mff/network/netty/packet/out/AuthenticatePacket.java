package com.github.oliwersdk.mff.network.netty.packet.out;

import com.github.oliwersdk.mff.network.netty.packet.Packet;

public final class AuthenticatePacket implements Packet {
  private final String token;

  public AuthenticatePacket(String token) {
    this.token = token;
  }

  public String token() {
    return this.token;
  }

  @Override
  public byte code() {
    return Out.AUTHENTICATE;
  }
}