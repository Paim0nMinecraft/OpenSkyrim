/*
 Skid from FDPClient
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "Crosshair", category = ModuleCategory.RENDER, description = "233")
class Crosshair : Module() {
    // Color
    private val colorModeValue = ListValue("Color", arrayOf("Custom", "Novoline"), "Novoline")
    private val colorRedValue = IntegerValue("Red", 255, 0, 255)
    private val colorGreenValue = IntegerValue("Green", 255, 0, 255)
    private val colorBlueValue = IntegerValue("Blue", 255, 0, 255)
    private val colorAlphaValue = IntegerValue("Alpha", 255, 0, 255)

    // Size, width, hitmarker
    private val widthValue = FloatValue("Width", 0.5f, 0.25f, 10f)
    private val sizeValue = FloatValue("Length", 7f, 0.25f, 15f)
    private val gapValue = FloatValue("Gap", 5f, 0.25f, 15f)
    private val dynamicValue = BoolValue("Dynamic", true)
    private val hitMarkerValue = BoolValue("HitMarker", true)

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val sr = ScaledResolution(minecraft)
        val width = widthValue.get()
        val size = sizeValue.get()
        val gap = gapValue.get()
        GL11.glPushMatrix()
        RenderUtils.drawBorderedRect(
            sr.scaledWidth / 2f - width,
            sr.scaledHeight / 2f - gap - size - 0,
            sr.scaledWidth / 2f + 1.0f + width,
            sr.scaledHeight / 2f - gap - 0,
            0.5f,
            Color(0, 0, 0).rgb,
            crosshairColor.rgb
        )
        RenderUtils.drawBorderedRect(
            sr.scaledWidth / 2f - width,
            sr.scaledHeight / 2f + gap + 1 + 0 - 0.15f,
            sr.scaledWidth / 2f + 1.0f + width,
            sr.scaledHeight / 2f + 1 + gap + size + 0 - 0.15f,
            0.5f,
            Color(0, 0, 0).rgb,
            crosshairColor.rgb
        )
        RenderUtils.drawBorderedRect(
            sr.scaledWidth / 2f - gap - size - 0 + 0.15f,
            sr.scaledHeight / 2f - width,
            sr.scaledWidth / 2f - gap - 0 + 0.15f,
            sr.scaledHeight / 2 + 1.0f + width,
            0.5f,
            Color(0, 0, 0).rgb,
            crosshairColor.rgb
        )
        RenderUtils.drawBorderedRect(
            sr.scaledWidth / 2f + 1 + gap + 0,
            sr.scaledHeight / 2f - width,
            sr.scaledWidth / 2f + size + gap + 1.0f + 0,
            sr.scaledHeight / 2 + 1.0f + width,
            0.5f,
            Color(0, 0, 0).rgb,
            crosshairColor.rgb
        )
        GL11.glPopMatrix()
        GlStateManager.resetColor()
        val target = LiquidBounce.combatManager.target/* ?: RaycastUtils.raycastEntity(Reach.hitReach.toDouble()) {
            it is EntityLivingBase
        } as EntityLivingBase? */
        if (hitMarkerValue.get() && target != null && target.hurtTime > 0) {
            GL11.glPushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()
            GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ZERO
            )
            GL11.glColor4f(1f, 1f, 1f, target.hurtTime.toFloat() / target.maxhurtTime.toFloat())
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glLineWidth(1f)
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap, sr.scaledHeight / 2f + gap)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap + size, sr.scaledHeight / 2f + gap + size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap, sr.scaledHeight / 2f - gap)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap - size, sr.scaledHeight / 2f - gap - size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap, sr.scaledHeight / 2f + gap)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap - size, sr.scaledHeight / 2f + gap + size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap, sr.scaledHeight / 2f - gap)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap + size, sr.scaledHeight / 2f - gap - size)
            GL11.glEnd()
            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
            GL11.glPopMatrix()
        }
    }

    val hud = LiquidBounce.moduleManager[HUD::class.java] as HUD

    private val crosshairColor: Color
        get() =
            when (colorModeValue.get().toLowerCase()) {
                "custom" -> Color(
                    colorRedValue.get(),
                    colorGreenValue.get(),
                    colorBlueValue.get(),
                    colorAlphaValue.get()
                )

                "novoline" -> RenderUtils.getGradientOffset(
                    Color(hud.r.get(), hud.g.get(), hud.b.get()),
                    Color(hud.r2.get(), hud.g2.get(), hud.b2.get()), 114514.0
                )

                else -> Color.WHITE
            }
}