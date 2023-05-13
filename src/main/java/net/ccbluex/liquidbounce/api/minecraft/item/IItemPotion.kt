package net.ccbluex.liquidbounce.api.minecraft.item

import net.ccbluex.liquidbounce.api.minecraft.potion.IPotionEffect

interface IItemPotion : IItem {
    fun getEffects(stack: IItemStack): Collection<IPotionEffect>
}