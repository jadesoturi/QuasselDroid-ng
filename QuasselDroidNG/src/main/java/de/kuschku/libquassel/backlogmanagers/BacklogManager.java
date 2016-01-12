package de.kuschku.libquassel.backlogmanagers;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.util.observablelists.AutoScroller;
import de.kuschku.util.observablelists.ObservableSortedList;

public abstract class BacklogManager extends SyncableObject {
    public abstract void requestBacklog(int bufferId, int from, int to, int count, int extra);

    public abstract void receiveBacklog(int bufferId, int from, int to, int count, int extra, List<Message> messages);

    public abstract void displayMessage(int bufferId, Message message);

    public abstract ObservableSortedList<Message> get(int bufferId);

    public abstract void bind(int bufferId, @Nullable RecyclerView.Adapter adapter, AutoScroller scroller);

    public abstract void requestMoreBacklog(int bufferId, int count);
}