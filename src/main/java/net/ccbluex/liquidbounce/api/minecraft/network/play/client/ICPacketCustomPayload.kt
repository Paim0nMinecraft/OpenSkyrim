package net.ccbluex.liquidbounce.api.minecraft.network.play.client

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.api.network.IPacketBuffer

interface ICPacketCustomPayload : IPacket {
    var data: IPacketBuffer
    val channelName: String
}