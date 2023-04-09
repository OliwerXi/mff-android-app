package com.github.oliwersdk.mff.ui.authenticated.adapter;

import static android.os.AsyncTask.execute;
import static android.view.LayoutInflater.from;
import static com.github.oliwersdk.mff.network.ServerAPI.getMessagesFromCursor;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.github.oliwersdk.mff.R;
import com.github.oliwersdk.mff.task.AsyncImageLoader;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ChatRecyclerAdapter extends Adapter<ChatRecyclerAdapter.Item> {
  private final Context context;
  private final Supplier<String> authToken;
  private final List<MessageData> messages;
  private final Consumer<Integer> scrollToIndex;
  private int cursor = 0; // TODO handle loading previous message for cursor + 50 for every scroll to top

  public ChatRecyclerAdapter(Context context, Supplier<String> authTokenSupplier, Consumer<Integer> scrollToIndex) {
    this.context = context;
    this.authToken = authTokenSupplier;
    this.messages = new LinkedList<>();
    this.scrollToIndex = scrollToIndex;
    init();
  }

  @NonNull
  @Override
  public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Item(
      from(context).inflate(
        R.layout.chat_message_item,
        parent, false
      )
    );
  }

  @Override
  public void onBindViewHolder(@NonNull Item holder, int position) {
    if (position >= this.getItemCount() || position < 0)
      return;
    holder.applyChanges(messages.get(position));
  }

  @Override
  public int getItemCount() {
    return messages.size();
  }

  public boolean push(MessageData messageData) {
    if (!messages.add(messageData))
      return false;

    final var index = getItemCount() - 1;
    notifyItemInserted(index);
    scrollToIndex.accept(index);
    return true;
  }

  private void init() {
    execute(() -> {
      final var fromCursor = getMessagesFromCursor(authToken.get(), cursor);
      ((Activity) context).runOnUiThread(() -> {
        for (final var m : fromCursor) {
          final var s = m.sender();
          push(new MessageData(m.id(), s.getAvatarLink(), s.displayOrUserName(), m.text(), m.createdAt()));
        }
      });
    });
  }

  public static final class Item extends ViewHolder {
    private final ImageView avatar;
    private final TextView username;
    private final TextView text;

    public Item(@NonNull View view) {
      super(view);

      this.avatar = view.findViewById(R.id.chatMessageUserAvatar);
      avatar.setClipToOutline(true);

      this.username = view.findViewById(R.id.chatMessageUserName);
      this.text = view.findViewById(R.id.chatMessageText);
    }

    public void applyChanges(MessageData from) {
      AsyncImageLoader.load(this.avatar, from.senderAvatarUrl); // FIXME: does not properly load all the time ( sometimes misses random views )
      username.setText(from.senderName);
      text.setText(from.text);
    }
  }

  public static final class MessageData {
    private final long id;
    private final String senderName;
    private final String senderAvatarUrl;
    private final String text;
    private final long sentAt;

    public MessageData(long id, String senderAvatarUrl, String senderName, String text, long sentAt) {
      this.id = id;
      this.senderAvatarUrl = senderAvatarUrl;
      this.senderName = senderName;
      this.text = text;
      this.sentAt = sentAt;
    }

    @Override
    public boolean equals(Object o) {
      return this == o || o != null
        && getClass() == o.getClass()
        && this.id == ((MessageData) o).id;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }
  }
}