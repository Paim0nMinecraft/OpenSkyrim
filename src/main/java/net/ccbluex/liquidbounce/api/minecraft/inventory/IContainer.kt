package net.ccbluex.liquidbounce.api.minecraft.inventory

interface IContainer {
    val windowId: Int

    fun getSlot(id: Int): ISlot

}
