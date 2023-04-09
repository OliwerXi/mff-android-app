package com.github.oliwersdk.mff.network.netty.packet.in;

import com.github.oliwersdk.mff.network.netty.packet.Packet;

public final class InvalidTokenPacket implements Packet {
  @Override
  public byte code() {
    return In.INVALID_TOKEN;
  }
}