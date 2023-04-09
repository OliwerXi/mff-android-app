package com.github.oliwersdk.mff.network.netty;

import com.github.oliwersdk.mff.network.netty.packet.Packet;
import com.github.oliwersdk.mff.network.netty.packet.PacketProcessor;
import com.github.oliwersdk.mff.network.netty.packet.in.AuthenticatedPacket;
import com.github.oliwersdk.mff.network.netty.packet.in.AuthenticationExpiredPacket;
import com.github.oliwersdk.mff.network.netty.packet.in.InChatMessagePacket;
import com.github.oliwersdk.mff.network.netty.packet.in.InvalidTokenPacket;
import com.github.oliwersdk.mff.network.netty.packet.in.UserConnectPacket;
import com.github.oliwersdk.mff.network.netty.packet.in.UserDisconnectPacket;

import io.netty.channel.ChannelHandlerContext;

public final class NetworkHandler extends PacketHandlerAdapter {
  private final PacketProcessor processor;

  public NetworkHandler(PacketProcessor processor) {
    this.processor = processor;
  }

  @Override
  protected void onIncomingPacket(ChannelHandlerContext context, Packet packet) throws Exception {
    if (packet instanceof AuthenticatedPacket)
      processor.onAuthenticated(context, (AuthenticatedPacket) packet);
    else if (packet instanceof AuthenticationExpiredPacket)
      processor.onAuthExpired(context);
    else if (packet instanceof InvalidTokenPacket)
      processor.onAuthInvalidToken(context);
    else if (packet instanceof UserConnectPacket)
      processor.onUserConnect(context, ((UserConnectPacket) packet).user());
    else if (packet instanceof UserDisconnectPacket)
      processor.onUserDisconnect(context, ((UserDisconnectPacket) packet).user());
    else if (packet instanceof InChatMessagePacket)
      processor.onChatMessage(context, (InChatMessagePacket) packet);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    processor.onInit(ctx);
    ctx.fireChannelActive();
  }
}