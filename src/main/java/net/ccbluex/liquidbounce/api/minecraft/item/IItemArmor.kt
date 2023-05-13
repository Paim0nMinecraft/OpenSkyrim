package net.ccbluex.liquidbounce.api.minecraft.item

import net.ccbluex.liquidbounce.api.minecraft.minecraft.IArmorMaterial

interface IItemArmor : IItem {
    val armorMaterial: IArmorMaterial
    val armorType: Int

    fun getColor(stack: IItemStack): Int
}