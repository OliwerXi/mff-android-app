package com.github.oliwersdk.mff.network.netty.packet;

import com.github.oliwersdk.mff.model.User;
import com.github.oliwersdk.mff.network.netty.packet.in.AuthenticatedPacket;
import com.github.oliwersdk.mff.network.netty.packet.in.InChatMessagePacket;

import io.netty.channel.ChannelHandlerContext;

public interface PacketProcessor {
  void onInit(ChannelHandlerContext context) throws Exception;

  void onAuthenticated(ChannelHandlerContext context, AuthenticatedPacket packet) throws Exception;

  void onAuthExpired(ChannelHandlerContext context) throws Exception;

  void onAuthInvalidToken(ChannelHandlerContext context) throws Exception;

  void onUserConnect(ChannelHandlerContext context, User user) throws Exception;

  void onUserDisconnect(ChannelHandlerContext context, User user) throws Exception;

  void onChatMessage(ChannelHandlerContext context, InChatMessagePacket packet) throws Exception;
}