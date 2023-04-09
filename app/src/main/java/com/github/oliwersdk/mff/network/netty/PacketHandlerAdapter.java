package com.github.oliwersdk.mff.network.netty;

import com.github.oliwersdk.mff.network.netty.packet.Packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class PacketHandlerAdapter extends SimpleChannelInboundHandler<String> {
  protected void onIncomingPacket(ChannelHandlerContext ctx, Packet packet) throws Exception {
    // NO OP BY DEFAULT
  }

  @Override
  protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
    final var packet = PacketManager.parse(msg);
    if (packet == null)
      return;
    this.onIncomingPacket(ctx, packet);
  }
}