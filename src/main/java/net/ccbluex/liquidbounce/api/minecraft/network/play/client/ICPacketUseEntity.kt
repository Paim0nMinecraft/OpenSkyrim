package net.ccbluex.liquidbounce.api.minecraft.network.play.client

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket

interface ICPacketUseEntity : IPacket {
    val action: WAction

    enum class WAction {
        INTERACT, ATTACK, INTERACT_AT
    }

}