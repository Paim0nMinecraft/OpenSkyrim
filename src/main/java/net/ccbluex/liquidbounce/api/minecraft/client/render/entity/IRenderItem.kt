package net.ccbluex.liquidbounce.api.minecraft.client.render.entity

import net.ccbluex.liquidbounce.api.minecraft.client.gui.IFontRenderer
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack

interface IRenderItem {
    var zLevel: Float

    fun renderItemAndEffectIntoGUI(stack: IItemStack, x: Int, y: Int)
    fun renderItemIntoGUI(stack: IItemStack, x: Int, y: Int)
    fun renderItemOverlays(fontRenderer: IFontRenderer, stack: IItemStack, x: Int, y: Int)
}