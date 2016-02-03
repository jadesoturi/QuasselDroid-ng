/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.localtypes.buffers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;

public class ChannelBuffer implements Buffer {
    @NonNull
    private BufferInfo info;
    @Nullable
    private QIrcChannel channel;

    public ChannelBuffer(@NonNull BufferInfo info, @Nullable QIrcChannel channel) {
        this.info = info;
        this.channel = channel;
    }

    @NonNull
    @Override
    public BufferInfo getInfo() {
        return info;
    }

    @Override
    public void setInfo(@NonNull BufferInfo info) {
        this.info = info;
    }

    @Nullable
    @Override
    public String getName() {
        return getInfo().name();
    }

    @NonNull
    @Override
    public BufferInfo.BufferStatus getStatus() {
        return channel == null ? BufferInfo.BufferStatus.OFFLINE : BufferInfo.BufferStatus.ONLINE;
    }

    @Override
    public void renameBuffer(@NonNull String newName) {

    }

    @Nullable
    public QIrcChannel getChannel() {
        return channel;
    }

    public void setChannel(@Nullable QIrcChannel channel) {
        this.channel = channel;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChannelBuffer{" +
                "info=" + info +
                ", channel=" + channel +
                '}';
    }
}