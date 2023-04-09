package com.github.oliwersdk.mff.network.netty;

import static com.github.oliwersdk.mff.SharedConstants.JSON_MAPPER;
import static com.github.oliwersdk.mff.network.netty.packet.Packet.In;
import static com.github.oliwersdk.mff.network.netty.packet.Packet.Out;
import static com.github.oliwersdk.mff.util.Numeric.tryParseByteOrElse;

import com.github.oliwersdk.mff.network.netty.packet.Packet;
import com.github.oliwersdk.mff.network.netty.packet.in.AuthenticatedPacket;
import com.github.oliwersdk.mff.network.netty.packet.in.AuthenticationExpiredPacket;
import com.github.oliwersdk.mff.network.netty.packet.in.InChatMessagePacket;
import com.github.oliwersdk.mff.network.netty.packet.in.InvalidTokenPacket;
import com.github.oliwersdk.mff.network.netty.packet.in.UserConnectPacket;
import com.github.oliwersdk.mff.network.netty.packet.in.UserDisconnectPacket;
import com.github.oliwersdk.mff.network.netty.packet.out.AuthenticatePacket;
import com.github.oliwersdk.mff.network.netty.packet.out.OutChatMessagePacket;

import java.util.HashMap;
import java.util.Map;

final class PacketManager {
  private static final PacketManager INSTANCE = new PacketManager();
  private final Map<Byte, Class<? extends Packet>> idToDataClass;

  private PacketManager() {
    this.idToDataClass = new HashMap<>();

    // OUTGOING
    register(Out.AUTHENTICATE, AuthenticatePacket.class);
    register(Out.CHAT_MESSAGE, OutChatMessagePacket.class);

    // INCOMING
    register(In.AUTHENTICATE_EXPIRED, AuthenticationExpiredPacket.class);
    register(In.AUTHENTICATED, AuthenticatedPacket.class);
    register(In.INVALID_TOKEN, InvalidTokenPacket.class);
    register(In.USER_DISCONNECT, UserDisconnectPacket.class);
    register(In.USER_CONNECT, UserConnectPacket.class);
    register(In.CHAT_MESSAGE, InChatMessagePacket.class);
  }

  static Packet parse(String rawValue) {
    if (rawValue.endsWith("\r\n"))
      rawValue = rawValue.substring(0, rawValue.length()-2);

    final var chunks = rawValue.split(": ", 2);
    final byte id;

    if (chunks.length != 2 || (id = tryParseByteOrElse(chunks[0], -1)) < 0)
      return null;

    final var dataClass = INSTANCE.idToDataClass.get(id);
    return dataClass == null ? null
      : JSON_MAPPER.fromJson(chunks[1], dataClass);
  }

  private boolean register(byte id, Class<? extends Packet> packetClass) {
    return idToDataClass.putIfAbsent(id, packetClass) == null;
  }
}