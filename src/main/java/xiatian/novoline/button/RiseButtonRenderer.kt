/*
 Skid from FDPClient
 */
package net.ccbluex.liquidbounce.features.module.modules.client.button

import net.ccbluex.liquidbounce.LiquidBounce
import cc.paimon.utils.AnimationUtils2
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.tenacity.ColorUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import org.lwjgl.opengl.GL11
import java.awt.Color

class RiseButtonRenderer(button: GuiButton) : AbstractButtonRenderer(button) {
    var animH = 0.0

    var hud = LiquidBounce.moduleManager.getModule(HUD::class.java) as HUD
    override fun render(mouseX: Int, mouseY: Int, mc: Minecraft) {
        val startX = button.x.toFloat()
        val endX = button.x + button.width.toFloat()
        val endY = button.y + button.height.toFloat()

        RenderUtils.drawRect(startX, button.y.toFloat(), endX, endY,
            (if(button.hovered) { Color(60, 60, 60, 150) } else { Color(31, 31, 31, 150) }).rgb)
        if (button.enabled) {
            if(button.hovered){
                animH = AnimationUtils2.animate(20.0,animH,0.01*RenderUtils.deltaTime)

            }else{
                animH = AnimationUtils2.animate(1.0,animH,0.01*RenderUtils.deltaTime)
            }
            GL11.glEnable(3042)
            GL11.glDisable(3553)
            GL11.glBlendFunc(770, 771)
            GL11.glEnable(2848)
            GL11.glShadeModel(7425)
            val color1 = Color(hud.r.get(), hud.g.get(),hud.b.get(), 255)
            val color2 = Color(hud.r2.get(), hud.g.get(), hud.b.get(), 255)

            val cock: Color = ColorUtil.mixColors(color1, color2, (Math.sin((4F + 0 * 0.4f).toDouble()) + 1) * 0.5f)
            val cock2: Color = ColorUtil.mixColors(color1, color2, (Math.sin((4F + button.width * 0.4f).toDouble()) + 1) * 0.5f)

           RenderUtils.gradientSideways(button.x.toDouble(),
                (button.y + button.height).toDouble(), button.width.toDouble(), -animH, cock, cock2)
            for (i in button.x..button.x + button.width step 1) {
            }
            GL11.glEnable(3553)
            GL11.glDisable(3042)
            GL11.glDisable(2848)
            GL11.glShadeModel(7424)
            GL11.glColor4f(1f, 1f, 1f, 1f)
        }
    }
}