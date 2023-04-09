package com.github.oliwersdk.mff.ui.authenticated;

import static com.github.oliwersdk.mff.GlobalSettings.IS_PRODUCTION;
import static com.github.oliwersdk.mff.R.color.dark_nav_toggled;
import static com.github.oliwersdk.mff.R.color.dark_nav_untoggled;
import static com.github.oliwersdk.mff.R.id.chatNavbarButton;
import static com.github.oliwersdk.mff.R.id.settingsNavbarButton;
import static com.github.oliwersdk.mff.SharedConstants.DEBUG_SERVER_HOST;
import static com.github.oliwersdk.mff.SharedConstants.GLOBAL_SETTINGS;
import static com.github.oliwersdk.mff.ui.authenticated.AuthenticatedActivity.Page.CHAT;
import static com.github.oliwersdk.mff.ui.authenticated.AuthenticatedActivity.Page.SETTINGS;
import static com.github.oliwersdk.mff.util.Action.modify;
import static com.github.oliwersdk.mff.util.Predicates.doIf;
import static com.github.oliwersdk.mff.util.Predicates.firstIfOrElse;
import static com.github.oliwersdk.mff.util.ViewHelper.setBackgroundTint;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.oliwersdk.mff.R;
import com.github.oliwersdk.mff.lang.Language;
import com.github.oliwersdk.mff.lang.LanguageProps;
import com.github.oliwersdk.mff.model.User;
import com.github.oliwersdk.mff.network.netty.NetworkHandler;
import com.github.oliwersdk.mff.network.netty.packet.PacketProcessor;
import com.github.oliwersdk.mff.network.netty.packet.in.AuthenticatedPacket;
import com.github.oliwersdk.mff.network.netty.packet.in.InChatMessagePacket;
import com.github.oliwersdk.mff.network.netty.packet.out.AuthenticatePacket;
import com.github.oliwersdk.mff.network.netty.packet.out.OutChatMessagePacket;
import com.github.oliwersdk.mff.ui.authenticated.adapter.ChatRecyclerAdapter;
import com.github.oliwersdk.mff.ui.authenticated.fragment.AuthorizedFragment;
import com.github.oliwersdk.mff.ui.authenticated.fragment.ChatFragment;
import com.github.oliwersdk.mff.ui.authenticated.fragment.LanguageFragment;
import com.github.oliwersdk.mff.ui.authenticated.fragment.SettingsFragment;
import com.github.oliwersdk.mff.ui.login.LoginActivity;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public final class AuthenticatedActivity extends AppCompatActivity
  implements Language.RefreshableComponent {
  private static final AtomicLong ONLINE_USER_COUNT = new AtomicLong();
  private static final short RTC_PORT = 8081;

  // data
  private final EventLoopGroup netGroup = new NioEventLoopGroup();
  private Thread netThread;
  private String token;
  private volatile Channel channel;
  private volatile User user;

  // ui
  private Button navChatButton;
  private Button navSettingsButton;
  private int activePageNavButton;
  private AuthorizedFragment activePageFragment;
  private FrameLayout pageFrame;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (supportRequestWindowFeature(Window.FEATURE_NO_TITLE)) {
      doIf(getSupportActionBar(), Objects::nonNull, ActionBar::hide);
    }

    init();
    while (!isAuthenticated()); // await authentication

    setContentView(R.layout.authenticated_activity);
    initUI();

    GLOBAL_SETTINGS.setLanguageListener(this::update);
  }

  @Override
  protected void onDestroy() {
    if (isNetworkAlive())
      netThread.interrupt();
    super.onDestroy();
  }

  @Override
  public void update(LanguageProps props) {
    navChatButton.setText(props.chatNavbarButtonText());
    navSettingsButton.setText(props.settingsNavbarButtonText());

    // perform language component update on the child page if it's initialized
    if (activePageFragment.initialized())
      activePageFragment.update(props);
  }

  private void init() {
    if (isNetworkAlive())
      return;

    this.token = getIntent().getStringExtra("token");
    this.netThread = new Thread(() -> {
      try {
        final var bootstrap = new Bootstrap()
          .group(netGroup)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
              ch.pipeline().addLast(
                new StringEncoder(),
                new StringDecoder(),
                new NetworkHandler(new PacketProcessor() {
                  @Override
                  public void onInit(ChannelHandlerContext context) throws Exception {
                    new AuthenticatePacket(token)
                      .writeAndFlush(context)
                      .sync();
                  }

                  @Override
                  public void onAuthenticated(ChannelHandlerContext context, AuthenticatedPacket packet) {
                    channel = context.channel();
                    ONLINE_USER_COUNT.set(packet.onlineUserCount());
                    user = packet.user();
                    updateFragment();
                  }

                  @Override
                  public void onAuthExpired(ChannelHandlerContext context) {
                    final Intent intent = new Intent(AuthenticatedActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                  }

                  @Override
                  public void onAuthInvalidToken(ChannelHandlerContext context) {
                    final Intent intent = new Intent(AuthenticatedActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                  }

                  @Override
                  public void onUserConnect(ChannelHandlerContext context, User user) {
                    ONLINE_USER_COUNT.incrementAndGet();
                    updateFragment();
                  }

                  @Override
                  public void onUserDisconnect(ChannelHandlerContext context, User user) {
                    ONLINE_USER_COUNT.decrementAndGet();
                    updateFragment();
                  }

                  @Override
                  public void onChatMessage(ChannelHandlerContext context, InChatMessagePacket packet) {
                    if (!(activePageFragment instanceof ChatFragment))
                      return;

                    final var sender = packet.sender();
                    runOnUiThread(() -> ((ChatFragment) activePageFragment).push(
                      new ChatRecyclerAdapter.MessageData(
                        packet.id(),
                        sender.getAvatarLink(),
                        firstIfOrElse(it -> it != null && !it.isEmpty(), sender.displayName(), sender::username),
                        packet.text(),
                        packet.sentAt()
                      )
                    ));
                  }
                })
              );
            }
          });

        final var channelFuture = IS_PRODUCTION
          ? bootstrap.connect(new DomainSocketAddress("rtc.mff.support")).sync()
          : bootstrap.connect(DEBUG_SERVER_HOST, RTC_PORT).sync();
        channelFuture.channel().closeFuture().sync();
      } catch (Exception ex) {
        ex.printStackTrace();
      } finally {
        try { netGroup.shutdownGracefully().sync(); }
        catch (InterruptedException ignored) { }
      }
    });

    netThread.start();
  }

  private boolean isAuthenticated() {
    return this.user != null;
  }

  private boolean isNetworkAlive() {
    return netThread != null && netThread.isAlive();
  }

  private void initUI() {
    this.pageFrame = findViewById(R.id.pageFrame);
    switchPage(CHAT);
    this.navChatButton = initNavbarButton(chatNavbarButton, CHAT);
    this.navSettingsButton = initNavbarButton(settingsNavbarButton, SETTINGS);
    update(GLOBAL_SETTINGS.language());
  }

  private void switchPage(Page page) {
    final AuthorizedFragment to;
    switch (page) {
      case CHAT:
        to = modify(
          new ChatFragment(),
          f -> f.setMessageSender(text -> channel.writeAndFlush(new OutChatMessagePacket(text).asString()))
        );

        if (activePageNavButton != 0)
          setBackgroundTint(this, activePageNavButton, dark_nav_untoggled);

        activePageNavButton = chatNavbarButton;
        setBackgroundTint(this, chatNavbarButton, dark_nav_toggled);
        break;
      case SETTINGS:
        to = modify(new SettingsFragment(), f -> f.setPageToggle(this::switchPage));

        if (activePageNavButton != 0)
          setBackgroundTint(this, activePageNavButton, dark_nav_untoggled);

        activePageNavButton = settingsNavbarButton;
        setBackgroundTint(this, settingsNavbarButton, dark_nav_toggled);
        break;
      case LANGUAGE:
        to = modify(
          new LanguageFragment(),
          f -> f.setGoBackCaller(() -> switchPage(SETTINGS))
        );
        break;
      default:
        return;
    }

    // data suppliers
    to.setUserSupplier(() -> this.user);
    to.setAuthTokenSupplier(() -> this.token);

    // commit the replacement
    getFragmentManager()
      .beginTransaction()
      .replace(R.id.pageFrame, activePageFragment = to)
      .commitNow();
  }

  private void updateFragment() {
    if (activePageFragment instanceof ChatFragment) {
      final var chatFragment = (ChatFragment) activePageFragment;
      runOnUiThread(chatFragment::updateOnlineUserCount);
    }
  }

  private Button initNavbarButton(int viewId, Page page) {
    final var button = (Button) findViewById(viewId);
    button.setOnClickListener(ignored -> switchPage(page));
    return button;
  }

  public static long getOnlineUserCount() {
    return ONLINE_USER_COUNT.get();
  }

  public enum Page {
    CHAT(false),
    SETTINGS(false),
    LANGUAGE(true); // extension of 'settings'

    private final boolean extension;

    Page(boolean extension) {
      this.extension = extension;
    }

    public boolean isExtension() {
      return this.extension;
    }
  }
}