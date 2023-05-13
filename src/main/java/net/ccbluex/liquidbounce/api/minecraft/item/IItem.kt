package net.ccbluex.liquidbounce.api.minecraft.item

import net.minecraft.item.Item

interface IItem {
    val unlocalizedName: String

    fun asItemArmor(): IItemArmor
    fun asItemPotion(): IItemPotion
    fun asItemBlock(): IItemBlock
    fun asItemSword(): IItemSword
    fun asItemBucket(): IItemBucket

    fun getItemByID(id: Int): IItem = Item.getItemById(id) as IItem


}