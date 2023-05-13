package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGui
import net.minecraft.client.gui.Gui

open class GuiImpl<T : Gui>(val wrapped: T) : IGui

inline fun IGui.unwrap(): Gui = (this as GuiImpl<*>).wrapped
inline fun Gui.wrap(): IGui = GuiImpl(this)