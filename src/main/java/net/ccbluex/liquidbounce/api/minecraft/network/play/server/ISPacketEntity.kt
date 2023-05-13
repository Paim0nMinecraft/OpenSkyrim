package net.ccbluex.liquidbounce.api.minecraft.network.play.server

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.api.minecraft.world.IWorld

interface ISPacketEntity : IPacket {
    val onGround: Boolean

    fun getEntity(world: IWorld): IEntity?
}