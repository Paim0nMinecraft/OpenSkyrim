package net.ccbluex.liquidbounce.api.minecraft.inventory

import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack

interface ISlot {
    val slotNumber: Int
    val stack: IItemStack?
    val hasStack: Boolean

}