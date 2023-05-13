package cc.paimon.skid.lbp.newVer.element

import net.ccbluex.liquidbounce.injection.backend.FontRendererImpl
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.gui.GuiTextField

class SearchBox(componentId: Int, x: Int, y: Int, width: Int, height: Int): GuiTextField(componentId,
    (Fonts.posterama40 as FontRendererImpl).wrapped, x, y, width, height) {
    override fun getEnableBackgroundDrawing() = false
}