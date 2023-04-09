package com.github.oliwersdk.mff.ui.authenticated.fragment;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;
import static com.github.oliwersdk.mff.ui.authenticated.AuthenticatedActivity.getOnlineUserCount;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.oliwersdk.mff.R;
import com.github.oliwersdk.mff.lang.LanguageProps;
import com.github.oliwersdk.mff.ui.authenticated.AuthenticatedActivity.Page;
import com.github.oliwersdk.mff.ui.authenticated.adapter.ChatRecyclerAdapter;

import java.util.function.Consumer;

public final class ChatFragment extends AuthorizedFragment {
  private Consumer<String> messageSender;
  private ChatRecyclerAdapter recyclerAdapter;
  private TextView onlineUserCount;
  private EditText messageToSend;
  private Button sendMessageButton;

  @Override
  protected void init(View view) {
    this.onlineUserCount = view.findViewById(R.id.boxInfoOnlineUserCount);
    this.messageToSend = view.findViewById(R.id.chatMessageBox);
    this.sendMessageButton = view.findViewById(R.id.sendChatMessageButton);

    { // recycler
      final var context = getContext();
      final var messageRecycler = (RecyclerView) view.findViewById(R.id.chatMessageRecycler);
      messageRecycler.setAdapter(recyclerAdapter = new ChatRecyclerAdapter(context, this.authTokenSupplier, messageRecycler::scrollToPosition));
      messageRecycler.setLayoutManager(new LinearLayoutManager(context, VERTICAL, false));
    }

    // button listeners
    sendMessageButton.setOnClickListener(ignored -> {
      final var textToForward = messageToSend.getText().toString();
      if (textToForward.isEmpty() || textToForward.isBlank())
        return;

      messageSender.accept(textToForward);
      messageToSend.clearFocus();
      messageToSend.setText("");
    });

    // initial updates
    updateOnlineUserCount();
  }

  @Override
  protected int layoutId() {
    return R.layout.chat_view;
  }

  @Override
  public Page page() {
    return Page.CHAT;
  }

  @Override
  public void update(LanguageProps props) {
    sendMessageButton.setText(props.sendMessageChatButtonText());
  }

  public void updateOnlineUserCount() {
    onlineUserCount.setText(String.valueOf(getOnlineUserCount()));
  }

  public boolean push(ChatRecyclerAdapter.MessageData messageData) {
    return recyclerAdapter.push(messageData);
  }

  public void setMessageSender(Consumer<String> messageSender) {
    this.messageSender = messageSender;
  }
}