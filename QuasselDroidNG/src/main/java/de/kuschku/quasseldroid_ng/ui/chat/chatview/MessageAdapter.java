package de.kuschku.quasseldroid_ng.ui.chat.chatview;

import android.content.Context;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.observables.AutoScroller;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.lists.IObservableList;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;
import de.kuschku.util.observables.lists.ObservableSortedList;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;

@UiThread
public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private final AutoScroller scroller;
    private IObservableList<UICallback, Message> messageList = new ObservableComparableSortedList<>(Message.class);
    private ChatMessageRenderer renderer;
    private LayoutInflater inflater;
    private UICallback callback;

    public MessageAdapter(Context ctx, AutoScroller scroller) {
        this.scroller = scroller;
        this.inflater = LayoutInflater.from(ctx);
        this.renderer = new ChatMessageRenderer(ctx);
        this.callback = new AdapterUICallbackWrapper(this, scroller);
    }

    public void setClient(Client client) {
        renderer.setClient(client);
    }

    public void setMessageList(ObservableSortedList<Message> messageList) {
        this.messageList.removeCallback(callback);
        this.messageList = messageList;
        this.messageList.addCallback(callback);
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(inflater.inflate(R.layout.widget_chatmessage, parent, false));
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message msg = messageList.get(position);
        renderer.onBind(holder, msg);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}