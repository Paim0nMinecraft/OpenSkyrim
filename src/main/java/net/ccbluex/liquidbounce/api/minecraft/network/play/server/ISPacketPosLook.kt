package net.ccbluex.liquidbounce.api.minecraft.network.play.server

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket

interface ISPacketPosLook : IPacket {
    var yaw: Float
    var pitch: Float
}