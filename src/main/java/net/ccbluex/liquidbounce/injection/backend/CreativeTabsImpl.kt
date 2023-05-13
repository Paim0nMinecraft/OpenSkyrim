package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.creativetabs.ICreativeTabs
import net.minecraft.creativetab.CreativeTabs

class CreativeTabsImpl(val wrapped: CreativeTabs) : ICreativeTabs {
    override var backgroundImageName: String
        get() = wrapped.backgroundImageName
        set(value) {
            wrapped.backgroundImageName = value
        }

    override fun equals(other: Any?): Boolean {
        return other is CreativeTabsImpl && other.wrapped == this.wrapped
    }
}

inline fun ICreativeTabs.unwrap(): CreativeTabs = (this as CreativeTabsImpl).wrapped
inline fun CreativeTabs.wrap(): ICreativeTabs = CreativeTabsImpl(this)