package de.kuschku.util.observables.lists;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiElementCallbackWrapper;

public class ObservableElementList<T> extends ArrayList<T> implements IObservableList<ElementCallback<T>, T> {
    MultiElementCallbackWrapper<T> callback = MultiElementCallbackWrapper.<T>of();

    public ObservableElementList(int capacity) {
        super(capacity);
    }

    public ObservableElementList() {
        super();
    }

    public ObservableElementList(Collection<? extends T> collection) {
        super(collection);
    }

    public void addCallback(ElementCallback<T> callback) {
        this.callback.addCallback(callback);
    }

    public void removeCallback(ElementCallback<T> callback) {
        this.callback.removeCallback(callback);
    }

    private int getPosition() {
        return isEmpty() ? 0 : size() - 1;
    }

    @Override
    public boolean add(T object) {
        add(getPosition(), object);
        return true;
    }

    @Override
    public void add(int index, T object) {
        super.add(index, object);
        callback.notifyItemInserted(object);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        return addAll(getPosition(), collection);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> collection) {
        boolean result = super.addAll(index, collection);
        if (result)
            for (T element : collection)
                callback.notifyItemInserted(element);
        return result;
    }

    @Override
    public T remove(int index) {
        T result = super.remove(index);
        callback.notifyItemRemoved(result);
        return result;
    }

    @Override
    public boolean remove(Object object) {
        int position = indexOf(object);
        if (position == -1) {
            return false;
        } else {
            remove(position);
            callback.notifyItemRemoved((T) object);
            return true;
        }
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++) {
            remove(get(i));
        }
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return super.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return super.retainAll(collection);
    }

    @Override
    public int indexOf(Object object) {
        for (int i = 0; i < size(); i++) {
            if (get(i) == object) return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object object) {
        for (int i = size() - 1; i >= 0; i--) {
            if (get(i) == object) return i;
        }
        return -1;
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new CallbackedArrayListIterator<>(super.iterator());
    }

    class CallbackedArrayListIterator<E> implements Iterator<E> {
        final Iterator<E> iterator;
        E current;

        public CallbackedArrayListIterator(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public E next() {
            current = iterator.next();
            return current;
        }

        @Override
        public void remove() {
            iterator.remove();
            callback.notifyItemRemoved((T) current);
        }
    }
}