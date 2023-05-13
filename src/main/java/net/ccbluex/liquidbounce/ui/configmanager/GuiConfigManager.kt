package net.ccbluex.liquidbounce.ui.configmanager

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import java.io.IOException

object GuiConfigManager : GuiScreen() {

    override fun drawScreen(p_drawScreen_1_: Int, p_drawScreen_2_: Int, p_drawScreen_3_: Float) {
        drawWorldBackground(0)
        val sr = ScaledResolution(Minecraft.getMinecraft())
        var y = 0
        for (listFile in LiquidBounce.fileManager.configsDir.listFiles()) {
            //RenderUtils.drawRect(sr.scaledWidth / 2 - FontLoaders.F18.getStringWidth(listFile.name) / 2 - 15,y + 3,20 + FontLoaders.F18.getStringWidth(listFile.name),FontLoaders.F18.height + 4,Color(0,0,0,50).rgb)
            FontLoaders.F18.drawString(
                listFile.name,
                sr.scaledWidth / 2 - FontLoaders.F18.getStringWidth(listFile.name) / 2,
                y + 5,
                -1
            )
            y += FontLoaders.F18.height + 2
        }
    }

    @Throws(IOException::class)
    override fun mouseClicked(p_mouseClicked_1_: Int, p_mouseClicked_2_: Int, p_mouseClicked_3_: Int) {

    }
}