package net.ccbluex.liquidbounce.api.minecraft.network.handshake.client

import net.ccbluex.liquidbounce.api.minecraft.network.IEnumConnectionState
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket

interface ICPacketHandshake : IPacket {
    val port: Int
    var ip: String
    val requestedState: IEnumConnectionState
}