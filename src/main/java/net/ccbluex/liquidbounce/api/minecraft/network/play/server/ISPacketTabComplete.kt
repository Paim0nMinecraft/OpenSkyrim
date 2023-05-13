package net.ccbluex.liquidbounce.api.minecraft.network.play.server

interface ISPacketTabComplete {
    val completions: Array<String>
}