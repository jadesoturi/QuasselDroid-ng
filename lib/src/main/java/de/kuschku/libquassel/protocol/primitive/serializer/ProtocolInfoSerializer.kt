/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Protocol_Features
import de.kuschku.libquassel.quassel.ProtocolInfo
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object ProtocolInfoSerializer : Serializer<ProtocolInfo> {
  override fun serialize(buffer: ChainedByteBuffer, data: ProtocolInfo,
                         features: QuasselFeatures) {
    ByteSerializer.serialize(buffer, data.flags.toByte(), features)
    ShortSerializer.serialize(buffer, data.data, features)
    ByteSerializer.serialize(buffer, data.version, features)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): ProtocolInfo {
    return ProtocolInfo(
      Protocol_Features.of(ByteSerializer.deserialize(buffer, features).toInt()),
      ShortSerializer.deserialize(buffer, features),
      ByteSerializer.deserialize(buffer, features)
    )
  }
}
