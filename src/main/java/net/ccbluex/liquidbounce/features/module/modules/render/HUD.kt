package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.client.button.AbstractButtonRenderer
import net.ccbluex.liquidbounce.features.module.modules.client.button.RiseButtonRenderer
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.GuiButton
import java.awt.Color
import java.text.SimpleDateFormat
import kotlin.math.hypot
import kotlin.math.roundToLong

@ModuleInfo(
    name = "HUD",
    description = "Toggles visibility of the HUD.",
    category = ModuleCategory.RENDER,
    array = false
)
class HUD : Module() {
    private val colorModeValue = ListValue("Text-Color", arrayOf("Custom", "Fade", "Gident"), "Gident")
    val fontChatValue = BoolValue("FontChat", true)
    val effectsValue = BoolValue("mceffects", false)
    val ChineseFontButton = BoolValue("ChineseFontButton", true)
    val ChineseScore = BoolValue("val ChineseScore ", true)
    val chatRect = BoolValue("ChatRect", true)
    val waterMark = BoolValue("Watermark", true)
    val otherRender = BoolValue("OtherRender", false)

    val chatAnimValue = BoolValue("ChatAnimation", false)
    val blurchat = BoolValue("blurchat", false)
    val blurStrength = IntegerValue("GlobalBlurStrength", 1, 1, 20)
    val inventoryParticle = BoolValue("InventoryParticle", false)
    val shadowValue = ListValue("TextShadowMode", arrayOf("LiquidBounce", "Outline", "Default", "Autumn"), "Autumn")
    val r = IntegerValue("DoubleColor-R1", 247, 0, 255)
    val g = IntegerValue("DoubleColor-G1", 255, 0, 255)
    val b = IntegerValue("DoubleColor-B1", 152, 0, 255)
    val r2 = IntegerValue("DoubleColor-R2", 146, 0, 255)
    val g2 = IntegerValue("DoubleColor-G2", 227, 0, 255)
    val b2 = IntegerValue("DoubleColor-B2", 218, 0, 255)
    val gradientSpeed = IntegerValue("DoubleColor-Speed", 10, 10, 1000)
    val hueInterpolation = BoolValue("DoubleColor-Interpolate", false)
    private val buttonValue = ListValue("Button", arrayOf("Rise"), "Rise")
    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        drawInfo()
        if (classProvider.isGuiHudDesigner(mc.currentScreen))
            return
        LiquidBounce.hud.render(false, 0, 0)
        if (otherRender.get()) {
            LiquidBounce.hud.render(false, 0, 0)

        }

    }

    fun getButtonRenderer(button: GuiButton): AbstractButtonRenderer? {
        return when (buttonValue.get().toLowerCase()) {
            "rise" -> RiseButtonRenderer(button)
            else -> null // vanilla or unknown
        }
    }

    private fun calculateBPS(): Double {
        val bps = hypot(
            mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX,
            mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ
        ) * mc.timer.timerSpeed * 20
        return (bps * 100.0).roundToLong() / 100.0
    }

    private fun drawInfo() {
        if (waterMark.get()) {
            RenderUtils.drawRoundedRect(
                5f,
                5f,
                63f + FontLoaders.TITLE18.getStringWidth(" | V${LiquidBounce.CLIENT_VERSION} | ${mc.session.username} | ${mc.debugFPS}FPS"),
                20f,
                1f,
                Color(0, 0, 0, 100).rgb
            )
            var length11 = 10
            val counter11 = intArrayOf(0)
            for (charIndex in "SKYRIM".toCharArray()) {
                xiatian.novoline.font.Fonts.tenacityblod.tenacityblod22.tenacityblod22.drawString(
                    charIndex.toString(), length11.toFloat(), 8f, RenderUtils.getGradientOffset(
                        Color(r.get(), g.get(), b.get()),
                        Color(r2.get(), g2.get(), b2.get(), 1),
                        (Math.abs(System.currentTimeMillis() / gradientSpeed.get().toDouble() + counter11[0]) / 10)
                    ).rgb
                )
                counter11[0] += 1
                counter11[0] = counter11[0].coerceIn(0, "SKYRIM".length)
                length11 += xiatian.novoline.font.Fonts.tenacityblod.tenacityblod22.tenacityblod22.stringWidth(charIndex.toString())
            }
            FontLoaders.TITLE18.drawString(
                " | V${LiquidBounce.CLIENT_VERSION} | ${mc.session.username.toUpperCase()} | ${mc.debugFPS}FPS",
                xiatian.novoline.font.Fonts.tenacityblod.tenacityblod22.tenacityblod22.stringWidth("SKYRIM") + 12,
                8,
                -1
            )
        }

    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        LiquidBounce.hud.update()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        LiquidBounce.hud.handleKey('a', event.key)
    }

    init {
        state = true
    }
}