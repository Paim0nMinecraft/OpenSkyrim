package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.item.IItemArmor
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.api.minecraft.minecraft.IArmorMaterial
import net.minecraft.item.ItemArmor

class ItemArmorImpl(wrapped: ItemArmor) : ItemImpl<ItemArmor>(wrapped), IItemArmor {
    override val armorMaterial: IArmorMaterial
        get() = wrapped.armorMaterial.wrap()
    override val armorType: Int
        get() = wrapped.armorType.index
    override val unlocalizedName: String
        get() = wrapped.unlocalizedName

    override fun getColor(stack: IItemStack): Int = wrapped.getColor(stack.unwrap())


}