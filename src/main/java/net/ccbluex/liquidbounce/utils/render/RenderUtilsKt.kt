package net.ccbluex.liquidbounce.utils.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
@author ChengFeng
@since 2022/12/1
 */

object RenderUtilsKt {

    @JvmStatic
    fun drawCircle(x: Float, y: Float, radius: Float, start: Float, end: Float) {
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(2f)
        GL11.glBegin(GL11.GL_LINE_STRIP)
        var i = end
        val hud = LiquidBounce.moduleManager[HUD::class.java] as HUD
        while (i >= start) {
            val c = RenderUtils.getGradientOffset(
                Color(hud.r.get(), hud.g.get(), hud.b.get()),
                Color(hud.r2.get(), hud.g2.get(), hud.b2.get(), 1),
                (abs(System.currentTimeMillis() / 360.0 + (i * 34 / 360) * 56 / 100) / 10)
            ).rgb
            val f2 = (c shr 24 and 255).toFloat() / 255.0f
            val f22 = (c shr 16 and 255).toFloat() / 255.0f
            val f3 = (c shr 8 and 255).toFloat() / 255.0f
            val f4 = (c and 255).toFloat() / 255.0f
            GlStateManager.color(f22, f3, f4, f2)
            GL11.glVertex2f(
                (x + cos(i * Math.PI / 180) * (radius * 1.001f)).toFloat(),
                (y + sin(i * Math.PI / 180) * (radius * 1.001f)).toFloat()
            )
            i -= 360f / 90.0f
        }
        GL11.glEnd()
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }
}