/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
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

package de.kuschku.libquassel.protocol

import de.kuschku.libquassel.protocol.primitive.serializer.*

enum class QType(val typeName: String, val serializer: Serializer<*>,
                 val type: Type = Type.UserType) {
  BufferId("BufferId", IntSerializer),
  BufferInfo("BufferInfo", BufferInfoSerializer),
  DccConfig_IpDetectionMode("DccConfig::IpDetectionMode", DccConfig_IpDetectionModeSerializer),
  DccConfig_PortSelectionMode("DccConfig::PortSelectionMode",
                              DccConfig_PortSelectionModeSerializer),
  IrcUser("IrcUser", VariantMapSerializer),
  IrcChannel("IrcChannel", VariantMapSerializer),
  Identity("Identity", VariantMapSerializer),
  IdentityId("IdentityId", IntSerializer),
  Message("Message", MessageSerializer),
  MsgId("MsgId", SignedId64Serializer),
  NetworkId("NetworkId", IntSerializer),
  NetworkInfo("NetworkInfo", VariantMapSerializer),
  Network_Server("Network::Server", VariantMapSerializer),
  QHostAddress("QHostAddress", HostAddressSerializer),
  PeerPtr("PeerPtr", LongSerializer, type = Type.Long);

  override fun toString() = "QType($typeName, $type)"

  companion object {
    private val map = values().associateBy(QType::typeName)
    fun of(name: String) = map[name]
  }
}
