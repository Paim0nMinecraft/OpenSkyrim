package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiButton
import net.minecraft.client.gui.GuiButton

class GuiButtonImpl(val wrapped: GuiButton) : IGuiButton {
    override var displayString: String
        get() = wrapped.displayString
        set(value) {
            wrapped.displayString = value
        }
    override val id: Int
        get() = wrapped.id
    override var enabled: Boolean
        get() = wrapped.enabled
        set(value) {
            wrapped.enabled = value
        }
}

inline fun IGuiButton.unwrap(): GuiButton = (this as GuiButtonImpl).wrapped
inline fun GuiButton.wrap(): IGuiButton = GuiButtonImpl(this)