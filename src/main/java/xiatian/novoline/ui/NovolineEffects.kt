package xiatian.novoline.ui

import net.ccbluex.liquidbounce.api.minecraft.potion.IPotion
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.HanaBiColors
import net.ccbluex.liquidbounce.utils.PotionData
import net.ccbluex.liquidbounce.utils.Translate
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "NewEffects")
class NovolineEffects : Element() {
    private val potionMap: MutableMap<IPotion, PotionData?> = HashMap()

    /**
     * Draw the entity.
     */
    override fun drawElement(): Border? {
        GlStateManager.pushMatrix()
        var y = 0
        for (potionEffect in mc.thePlayer!!.activePotionEffects) {
            val potion = functions.getPotionById(potionEffect.potionID)
            val name = functions.formatI18n(potion.name)
            val potionData: PotionData?
            val x2 =  (Fonts.posterama35.getStringWidth(name + " " + intToRomanByGreedy(potionEffect.amplifier + 1)) * 1.75).toFloat()
            if (potionMap.containsKey(potion) && potionMap[potion]!!.level == potionEffect.amplifier) potionData =
                potionMap[potion] else potionMap[potion] =
                PotionData(potion, Translate(0f, -40f + y), potionEffect.amplifier).also { potionData = it }
            var flag = true
            for (checkEffect in mc.thePlayer!!.activePotionEffects) if (checkEffect.amplifier == potionData!!.level) {
                flag = false
                break
            }
            if (flag) potionMap.remove(potion)
            var potionTime: Int
            var potionMaxTime: Int
            try {
                potionTime = potionEffect.getDurationString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0].toInt()
                potionMaxTime = potionEffect.getDurationString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1].toInt()
            } catch (ignored: Exception) {
                potionTime = 100
                potionMaxTime = 1000
            }
            val lifeTime = potionTime * 60 + potionMaxTime
            if (potionData!!.getMaxTimer() == 0 || lifeTime > potionData.getMaxTimer().toDouble()) potionData.maxTimer =
                lifeTime
            var state = 0.0f
            if (lifeTime >= 0.0) state = (lifeTime / potionData.getMaxTimer().toFloat().toDouble() * 100.0).toFloat()
            val position = Math.round(potionData.translate.y + 5)
            state = Math.max(state, 2.0f)
            potionData.translate.interpolate(0f, y.toFloat(), 0.1)

            potionData.animationX = RenderUtils.getAnimationState2(
                potionData.getAnimationX().toDouble(), (1.2f * state).toDouble(), (Math.max(
                    10.0f, Math.abs(
                        potionData.animationX - 1.2f * state
                    ) * 15.0f
                ) * 0.3f).toDouble()
            ).toFloat()

            RenderUtils.drawRectPotion(
                0f,
                potionData.translate.y,
                x2,
                potionData.translate.y + 30f,
                ClientUtils.reAlpha(
                    Color(34, 24, 20).brighter().rgb, 0.3f
                )
            )
            //RenderUtils.drawRectPotion(0, potionData.translate.getY(), potionData.animationX, potionData.translate.getY() + 30F, ClientUtils.reAlpha((new Color(34, 24, 20)).brighter().getRGB(), 0.3F));
            //RenderUtils.drawShadowWithCustomAlpha(0, Math.round(potionData.translate.getY()), 120, 30, 200);
            val posY = potionData.translate.y + 13f
            Fonts.posterama35.drawString(
                name + " " + intToRomanByGreedy(potionEffect.amplifier + 1),
                29f,
                posY - mc.fontRendererObj.fontHeight,
                ClientUtils.reAlpha(HanaBiColors.WHITE.c, 0.8f)
            )
            Fonts.posterama35.drawString(
                potionEffect.getDurationString(), 29f, posY + 4.0f, ClientUtils.reAlpha(
                    Color(200, 200, 200).rgb, 0.5f
                )
            )
            if (potion.hasStatusIcon) {
                GlStateManager.pushMatrix()
                GL11.glDisable(2929)
                GL11.glEnable(3042)
                GL11.glDepthMask(false)
                OpenGlHelper.glBlendFunc(770, 771, 1, 0)
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                val statusIconIndex = potion.statusIconIndex
                mc.textureManager.bindTexture(classProvider.createResourceLocation("textures/gui/container/inventory.png"))
                mc2.ingameGUI.drawTexturedModalRect(
                    6f,
                    (position + 40).toFloat(),
                    statusIconIndex % 8 * 18,
                    198 + statusIconIndex / 8 * 18,
                    18,
                    18
                )
                GL11.glDepthMask(true)
                GL11.glDisable(3042)
                GL11.glEnable(2929)
                GlStateManager.popMatrix()
            }
            y -= 35
        }
        GlStateManager.popMatrix()
        return Border(0f, 0f, 120f, 30f,0F)
    }

    private fun intToRomanByGreedy(num: Int): String {
        var num = num
        val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val symbols = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        val stringBuilder = StringBuilder()
        var i = 0
        while (i < values.size && num >= 0) {
            while (values[i] <= num) {
                num -= values[i]
                stringBuilder.append(symbols[i])
            }
            i++
        }
        return stringBuilder.toString()
    }

}