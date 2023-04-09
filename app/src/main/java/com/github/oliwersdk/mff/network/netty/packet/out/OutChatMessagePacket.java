package com.github.oliwersdk.mff.network.netty.packet.out;

import com.github.oliwersdk.mff.network.netty.packet.Packet;

public final class OutChatMessagePacket implements Packet {
  private final String text;

  public OutChatMessagePacket(String text) {
    this.text = text;
  }

  public String text() {
    return this.text;
  }

  @Override
  public byte code() {
    return Out.CHAT_MESSAGE;
  }
}