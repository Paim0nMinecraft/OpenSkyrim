package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.enchantments.IEnchantment
import net.minecraft.enchantment.Enchantment

class EnchantmentImpl(val wrapped: Enchantment) : IEnchantment {
    override val effectId: Int
        get() = Enchantment.getEnchantmentID(wrapped)

    override fun getTranslatedName(level: Int): String = wrapped.getTranslatedName(level)

}

inline fun IEnchantment.unwrap(): Enchantment = (this as EnchantmentImpl).wrapped
inline fun Enchantment.wrap(): IEnchantment = EnchantmentImpl(this)