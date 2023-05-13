/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl

import me.utils.ColorUtils2
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.client.entity.player.IEntityPlayer
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Target
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.TargetStyle
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.BlendUtils
import net.ccbluex.liquidbounce.utils.extensions.darker
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs

class MoonLatest(inst: Target) : TargetStyle("MoonLatest", inst, true) {
    val borderColorMode = ListValue("Border-Color", arrayOf("Custom", "MatchBar", "None"), "None")

    private var lastTarget: IEntityLivingBase? = null

    override fun drawTarget(entity: IEntityLivingBase) {

        val font = Fonts.posterama35
        val healthString = "${decimalFormat2.format(entity.health)} "

        if (entity != lastTarget || easingHealth < 0 || easingHealth > entity.maxHealth ||
            abs(easingHealth - entity.health) < 0.01
        ) {
            easingHealth = entity.health
        }
        val width = (38 + Fonts.posterama40.getStringWidth(entity.name!!))
            .coerceAtLeast(118)
            .toFloat()

        // Draw rect box
        RenderUtils.drawRect(0F, 0F, width, 32F, targetInstance.bgColor.rgb)

        // Health bar
        val barLength = 69F * (entity.health / entity.maxHealth).coerceIn(0F, 1F)
        RenderUtils.drawRect(
            37F,
            25.5F,
            45F + 69F,
            26.5F,
            getColor(BlendUtils.getHealthColor(entity.health, entity.maxHealth).darker(0.3F)).rgb
        )
        RenderUtils.drawRect(
            37F,
            25.5F,
            45F + barLength,
            26.5F,
            getColor(BlendUtils.getHealthColor(entity.health, entity.maxHealth)).rgb
        )

        // Draw rect 1
        RenderUtils.drawRect(0F, 0F, width, 1F, ColorUtils2.skyRainbow(0, 1f, 1f))

        // Armor bar
        if (entity.totalArmorValue != 0) {
            RenderUtils.drawRect(
                37F,
                28.5F,
                30f + (entity.totalArmorValue) * 4.2F,
                29.5F,
                Color(36, 77, 255).rgb
            ) // Draw armor bar
        }

        updateAnim(entity.health)
        // Name
        Fonts.posterama40.drawString(entity.name!!, 37, 3, getColor(-1).rgb)

        // HP
        GL11.glPushMatrix()
        GL11.glScalef(1F, 1F, 1F)
        font.drawStringWithShadow(healthString + "HP", 37, 17.5F.toInt(), Color(255, 255, 255).rgb)
        GL11.glPopMatrix()

        GlStateManager.resetColor()
        RenderUtils.drawEntityOnScreen(18, 28, 12, entity)

        lastTarget = entity
    }

    override fun handleBlur(player: IEntityPlayer) {
        val width = (38 + Fonts.posterama40.getStringWidth(player.name!!))
            .coerceAtLeast(118)
            .toFloat()

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.quickDrawRect(0F, 0F, width, 32F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

    }

    override fun handleShadowCut(player: IEntityPlayer) = handleBlur(player)

    override fun handleShadow(player: IEntityPlayer) {
        val width = (38 + Fonts.posterama40.getStringWidth(player.name!!))
            .coerceAtLeast(118)
            .toFloat()

        RenderUtils.drawRect(0F, 0F, width, 32F, shadowOpaque.rgb)
    }

    override fun getBorder(entity: IEntityLivingBase?): Border {
        entity ?: return Border(0F, 0F, 118F, 32F, 0f)
        val width = (38 + Fonts.posterama40.getStringWidth(entity.name!!))
            .coerceAtLeast(118)
            .toFloat()
        return Border(0F, 0F, width, 32F, 0f)
    }

}