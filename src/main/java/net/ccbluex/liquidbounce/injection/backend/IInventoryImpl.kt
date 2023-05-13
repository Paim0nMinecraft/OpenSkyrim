package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.client.gui.inventory.IIInventory
import net.minecraft.inventory.IInventory

class IInventoryImpl(val wrapped: IInventory) : IIInventory {
    override val name: String
        get() = wrapped.name

    override fun equals(other: Any?): Boolean {
        return other is IInventoryImpl && other.wrapped == this.wrapped
    }
}

inline fun IIInventory.unwrap(): IInventory = (this as IInventoryImpl).wrapped
inline fun IInventory.wrap(): IIInventory = IInventoryImpl(this)