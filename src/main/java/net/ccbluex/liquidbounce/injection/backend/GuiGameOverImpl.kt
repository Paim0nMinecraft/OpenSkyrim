package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiGameOver
import net.minecraft.client.gui.GuiGameOver

class GuiGameOverImpl<T : GuiGameOver>(wrapped: T) : GuiScreenImpl<T>(wrapped), IGuiGameOver {
    override val enableButtonsTimer: Int
        get() = wrapped.enableButtonsTimer
}

inline fun IGuiGameOver.unwrap(): GuiGameOver = (this as GuiGameOverImpl<*>).wrapped
inline fun GuiGameOver.wrap(): IGuiGameOver = GuiGameOverImpl(this)