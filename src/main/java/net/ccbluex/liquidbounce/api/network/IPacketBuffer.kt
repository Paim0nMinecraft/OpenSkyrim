package net.ccbluex.liquidbounce.api.network

import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack

interface IPacketBuffer {
    fun writeBytes(payload: ByteArray)
    fun writeItemStackToBuffer(itemStack: IItemStack)
    fun writeString(vanilla: String): IPacketBuffer
}