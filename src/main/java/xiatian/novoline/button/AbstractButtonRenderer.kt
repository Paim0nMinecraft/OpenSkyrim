/*
 Skid from FDPClient
 */
package net.ccbluex.liquidbounce.features.module.modules.client.button

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import xiatian.novoline.font.Fonts
import java.awt.Color

abstract class AbstractButtonRenderer(protected val button: GuiButton) {
    abstract fun render(mouseX: Int, mouseY: Int, mc: Minecraft)

    open fun drawButtonText(mc: Minecraft) {
        val hud = LiquidBounce.moduleManager.getModule(HUD::class.java) as HUD
        if (hud.ChineseFontButton.get())
        FontLoaders.F18.DisplayFonts(button.displayString,
            (button.x + button.width / 2 -
                    FontLoaders.F18.getStringWidth(button.displayString) / 2).toFloat(),
            button.y + (button.height - 5) / 2f - 1,
            if (button.enabled) Color.WHITE.rgb else Color.GRAY.rgb,
            FontLoaders.F18
        )else{
             Fonts.posterama.posterama18.posterama18.drawString(button.displayString,
                 (button.x + button.width / 2 -
                         FontLoaders.F18.getStringWidth(button.displayString) / 2).toFloat(),
                 (button.y + (button.height - 5) / 2f - 1),
                if (button.enabled) Color.WHITE.rgb else Color.GRAY.rgb)
        }
    }
}