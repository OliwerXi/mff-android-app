package com.github.oliwersdk.mff.network.netty.packet;

import static com.github.oliwersdk.mff.SharedConstants.JSON_MAPPER;

import android.annotation.SuppressLint;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public interface Packet {
  interface Out {
    byte AUTHENTICATE = 0;
    byte CHAT_MESSAGE = 6;
  }

  interface In {
    byte AUTHENTICATE_EXPIRED = 1;
    byte AUTHENTICATED = 2;
    byte INVALID_TOKEN = 3;
    byte USER_DISCONNECT = 4;
    byte USER_CONNECT = 5;
    byte CHAT_MESSAGE = 7;
  }

  byte code();

  @SuppressLint("DefaultLocale")
  default String asString() {
    return String.format("%d: %s\r\n", code(), JSON_MAPPER.toJson(this));
  }

  default ChannelFuture writeAndFlush(ChannelHandlerContext context) {
    return context.writeAndFlush(asString());
  }
}