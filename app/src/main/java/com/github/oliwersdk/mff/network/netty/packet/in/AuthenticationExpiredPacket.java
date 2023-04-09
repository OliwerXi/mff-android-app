package com.github.oliwersdk.mff.network.netty.packet.in;

import com.github.oliwersdk.mff.network.netty.packet.Packet;

public final class AuthenticationExpiredPacket implements Packet {
  @Override
  public byte code() {
    return In.AUTHENTICATE_EXPIRED;
  }
}