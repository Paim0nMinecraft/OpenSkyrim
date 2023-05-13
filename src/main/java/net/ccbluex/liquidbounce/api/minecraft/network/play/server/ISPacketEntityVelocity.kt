package net.ccbluex.liquidbounce.api.minecraft.network.play.server

interface ISPacketEntityVelocity {
    var motionX: Int
    var motionY: Int
    var motionZ: Int

    val entityID: Int
}