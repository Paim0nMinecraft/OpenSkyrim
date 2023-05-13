package net.ccbluex.liquidbounce.api.minecraft.entity.player

import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.api.util.IWrappedArray

interface IInventoryPlayer {
    val mainInventory: IWrappedArray<IItemStack?>
    val armorInventory: IWrappedArray<IItemStack?>
    var currentItem: Int
    val offHandInventory: IWrappedArray<IItemStack?>

    fun getStackInSlot(slot: Int): IItemStack?
    fun armorItemInSlot(slot: Int): IItemStack?
    fun getCurrentItemInHand(): IItemStack?
}