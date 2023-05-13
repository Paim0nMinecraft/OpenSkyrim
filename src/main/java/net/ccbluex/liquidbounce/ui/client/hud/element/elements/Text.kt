package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.CPSCounter
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowFontShader
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ChatAllowedCharacters
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import xiatian.Palette
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.sqrt

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@ElementInfo(name = "Text")
class Text(
    x: Double = 10.0, y: Double = 10.0, scale: Float = 1F,
    side: Side = Side.default()
) : Element(x, y, scale, side) {
    companion object {

        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")
        val DECIMAL_FORMAT = DecimalFormat("0.00")
        val DECIMAL_FORMAT_INT = DecimalFormat("0")

        /**
         * Create default element
         */
        fun defaultClient(): Text {
            val text = Text(x = 2.0, y = 2.0, scale = 2F)

            text.displayString.set("%clientName%")
            text.shadow.set(true)
            text.fontValue.set(Fonts.posterama40)
            text.setColor(Color(0, 111, 255))

            return text
        }

    }

    private val Mode = ListValue("Border-Mode", arrayOf("Slide", "Skeet"), "Slide")
    private val radiusValue = FloatValue("Radius", 0f, 0f, 10f)
    private val displayString = TextValue("DisplayText", "")
    private val redValue = IntegerValue("Text-R", 255, 0, 255)
    private val greenValue = IntegerValue("Text-G", 255, 0, 255)
    private val blueValue = IntegerValue("Text-B", 255, 0, 255)
    private val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
    private val colorModeValue = ListValue("Text-Color", arrayOf("Custom", "Fade", "Gident"), "Gident")
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)
    private val shadow = BoolValue("Shadow", true)
    private val bord = BoolValue("Border", false)
    private val slide = BoolValue("Slide", false)
    private val char = BoolValue("NotChar", false)
    private val slidedelay = IntegerValue("SlideDelay", 100, 0, 1000)
    private val balpha = IntegerValue("BordAlpha", 255, 0, 255)
    private val distanceValue = IntegerValue("Distance", 0, 0, 400)
    private val amountValue = IntegerValue("Amount", 25, 1, 50)
    private var fontValue = FontValue("Font", Fonts.posterama40)

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var speedStr = ""
    private var displayText: String = ""


    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode)
                "${LiquidBounce.CLIENT_NAME} | Fps:%fps% | %serverip% | User:%username%"
            else
                displayString.get()


            return multiReplace(textContent)
        }

    override fun shadow() {
        val fontRenderer = fontValue.get()
        var length2 = 4.5f
        val charArray = displayText.toCharArray()
        if (char.get()) {
            length2 = fontRenderer.getStringWidth(displayText).toFloat()
        } else {
            for (charIndex in charArray) {
                length2 += fontRenderer.getStringWidth(charIndex.toString())
            }
        }
        RenderUtils.drawRect(
            -4.0f,
            -4.5f,
            (length2).toFloat(),
            fontRenderer.fontHeight.toFloat(),
            Color(0, 0, 0, balpha.get()).rgb
        )
    }

    private fun getReplacement(str: String): String? {
        if (mc.thePlayer != null) {
            when (str) {
                "x" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.posX)
                "y" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.posY)
                "z" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.posZ)
                "xInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.posX)
                "yInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.posY)
                "zInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.posZ)
                "xdp" -> return mc.thePlayer!!.posX.toString()
                "ydp" -> return mc.thePlayer!!.posY.toString()
                "zdp" -> return mc.thePlayer!!.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(mc.thePlayer!!.motionX * mc.thePlayer!!.motionX + mc.thePlayer!!.motionZ * mc.thePlayer!!.motionZ))
                "ping" -> return EntityUtils.getPing(mc.thePlayer).toString()
                "health" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.health)
                "maxHealth" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.maxHealth)
                "healthInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.health)
                "maxHealthInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.maxHealth)
                "yaw" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.rotationYaw)
                "pitch" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.rotationPitch)
                "yawInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.rotationYaw)
                "pitchInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.rotationPitch)
                "bps" -> return speedStr
                "hurtTime" -> return mc.thePlayer!!.hurtTime.toString()
                "onGround" -> return mc.thePlayer!!.onGround.toString()
            }
        }

        return when (str) {
            "username" -> mc2.getSession().username
            "clientname" -> LiquidBounce.CLIENT_NAME
            "clientversion" -> "b${LiquidBounce.CLIENT_VERSION}"
            "clientcreator" -> LiquidBounce.CLIENT_CREATOR
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverip" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()
            "userName" -> mc.session.username
            "clientName" -> LiquidBounce.CLIENT_NAME
            "clientVersion" -> LiquidBounce.CLIENT_VERSION.toString()
            "clientCreator" -> LiquidBounce.CLIENT_CREATOR
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverIp" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()
//            "watchdogLastMin" -> BanChecker.WATCHDOG_BAN_LAST_MIN.toString()
//            "staffLastMin" -> BanChecker.STAFF_BAN_LAST_MIN.toString()
//            "sessionTime" -> return SessionUtils.getFormatSessionTime()
//            "worldTime" -> return SessionUtils.getFormatWorldTime()
            else -> null // Null = don't replace
        }
    }

    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()
        for (i in str.indices) {
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }

    /**
     * Draw element
     */
    var slidetext: Int = 0
    var slidetimer: MSTimer = MSTimer()
    var doslide = true
    override fun drawElement(): Border {
        val hud = LiquidBounce.moduleManager[HUD::class.java] as HUD
        val fontRenderer = fontValue.get()
        var length2 = 4.5f
        val charArray = displayText.toCharArray()
        if (char.get()) {
            length2 = fontRenderer.getStringWidth(displayText).toFloat()
        } else {
            for (charIndex in charArray) {
                length2 += fontRenderer.getStringWidth(charIndex.toString())
            }
        }
        if (slide.get() && !classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (slidetimer.hasTimePassed(slidedelay.get().toLong())) {
                if (slidetext <= display.length && doslide) {
                    slidetext += 1
                    slidetimer.reset()
                } else {
                    if (!doslide && slidetext >= 0) {
                        slidetext -= 1
                        slidetimer.reset()
                    }
                }
            }
            if (slidetext == display.length && doslide) {
                doslide = false
            } else {
                if (slidetext == 0 && !doslide) {
                    doslide = true
                }
            }
            displayText = display.substring(0, slidetext)
        } else {
            displayText = display
        }
        val colorMode = colorModeValue.get()
        val color = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb
        val rainbow = colorMode.equals("1", ignoreCase = true)
        if (bord.get()) {
            if (Mode.get() == "Skeet") {
                val counter = intArrayOf(0)
                val barLength = (length2).toDouble()
                for (i in 0..(amountValue.get() - 1)) {
                    val barStart = i.toDouble() / amountValue.get().toDouble() * barLength
                    val barEnd = (i + 1).toDouble() / amountValue.get().toDouble() * barLength
                    RenderUtils.drawGradientSideways(
                        -1.4 + barStart, -2.7, -1.4 + barEnd, -2.0,
                        when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                                Color(
                                    hud.r.get(),
                                    hud.g.get(),
                                    hud.b.get()
                                ), i * distanceValue.get(), displayText.length * 200
                            ).rgb

                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(
                                Color(hud.r.get(), hud.g.get(), hud.b.get()),
                                Color(hud.r2.get(), hud.g2.get(), hud.b2.get(), 1),
                                (Math.abs(
                                    System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()
                                ) / 10)
                            ).rgb

                            else -> color
                        },
                        when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                                Color(
                                    hud.r.get(),
                                    hud.g.get(),
                                    hud.b.get()
                                ), i * distanceValue.get(), displayText.length * 200
                            ).rgb

                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(
                                Color(hud.r.get(), hud.g.get(), hud.b.get()),
                                Color(hud.r2.get(), hud.g2.get(), hud.b2.get(), 1),
                                (Math.abs(
                                    System.currentTimeMillis() / hud.gradientSpeed.get()
                                        .toDouble() + i * distanceValue.get()
                                ) / 10)
                            ).rgb

                            else -> color
                        }
                    )
                }
            }
            if (Mode.get() == "Slide") {
                RenderUtils.drawRect(
                    -4.0f,
                    -4.5f,
                    (length2).toFloat(),
                    fontRenderer.fontHeight.toFloat(),
                    Color(0, 0, 0, balpha.get()).rgb
                )
                val barLength = (length2 + 1).toDouble()
                val counter = intArrayOf(0)
                for (i in 0..(amountValue.get() - 1)) {
                    val barStart = i.toDouble() / amountValue.get().toDouble() * barLength
                    val barEnd = (i + 1).toDouble() / amountValue.get().toDouble() * barLength
                    RenderUtils.drawGradientSideways(
                        -4.0 + barStart, -4.2, -1.0 + barEnd, -3.0,
                        when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                                Color(
                                    hud.r.get(),
                                    hud.g.get(),
                                    hud.b.get()
                                ), i * distanceValue.get(), displayText.length * 200
                            ).rgb

                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(
                                Color(hud.r.get(), hud.g.get(), hud.b.get()),
                                Color(hud.r2.get(), hud.g2.get(), hud.b2.get(), 1),
                                (Math.abs(
                                    System.currentTimeMillis() / hud.gradientSpeed.get()
                                        .toDouble() + i * distanceValue.get()
                                ) / 10)
                            ).rgb

                            else -> color
                        },
                        when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                                Color(
                                    hud.r.get(),
                                    hud.g.get(),
                                    hud.b.get()
                                ), i * distanceValue.get(), displayText.length * 200
                            ).rgb

                            colorMode.equals("Gident", ignoreCase = true) ->
                                RenderUtils.getGradientOffset(
                                    Color(hud.r.get(), hud.g.get(), hud.b.get()),
                                    Color(hud.r2.get(), hud.g2.get(), hud.b2.get(), 1),
                                    (Math.abs(
                                        System.currentTimeMillis() / hud.gradientSpeed.get()
                                            .toDouble() + i * distanceValue.get()
                                    ) / 10)
                                ).rgb

                            else -> color
                        }
                    )
                }
            }
        }
        val counter = intArrayOf(0)
        if (char.get()) {
            val rainbow = colorMode.equals("1", ignoreCase = true)

            RainbowFontShader.begin(
                rainbow,
                if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                System.currentTimeMillis() % 10000 / 10000F
            ).use {
                fontRenderer.drawString(
                    displayText, 0F, 0F, when {
                        rainbow -> 0
                        colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                            Color(
                                hud.r.get(),
                                hud.g.get(),
                                hud.b.get()
                            ), counter[0] * 100, displayText.length * 200
                        ).rgb

                        colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(
                            Color(hud.r.get(), hud.g.get(), hud.b.get()),
                            Color(hud.r2.get(), hud.g2.get(), hud.b2.get(), 1),
                            (Math.abs(
                                System.currentTimeMillis() / hud.gradientSpeed.get().toDouble() + counter[0]
                            ) / 10)
                        ).rgb

                        else -> color
                    }, shadow.get()
                )
                if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen) && editTicks <= 40)
                    fontRenderer.drawString(
                        "_", fontRenderer.getStringWidth(displayText).toFloat(),
                        0F, when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                                Color(
                                    hud.r.get(),
                                    hud.g.get(),
                                    hud.b.get()
                                ), counter[0] * 100, displayText.length * 200
                            ).rgb

                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(
                                Color(hud.r.get(), hud.g.get(), hud.b.get()),
                                Color(hud.r2.get(), hud.g2.get(), hud.b2.get(), 1),
                                (Math.abs(
                                    System.currentTimeMillis() / hud.gradientSpeed.get().toDouble() + counter[0]
                                ) / 10)
                            ).rgb

                            else -> color
                        }, shadow.get()
                    )
                counter[0] += 1
            }
        } else {
            var length = 0
            RainbowFontShader.begin(
                rainbow,
                if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                System.currentTimeMillis() % 10000 / 10000F
            ).use {
                for (charIndex in charArray) {
                    val rainbow = colorMode.equals("1", ignoreCase = true)
                    fontRenderer.drawString(
                        charIndex.toString(), length.toFloat(), 0F, when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                                Color(
                                    hud.r.get(),
                                    hud.g.get(),
                                    hud.b.get()
                                ), counter[0] * 100, displayText.length * 200
                            ).rgb

                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(
                                Color(hud.r.get(), hud.g.get(), hud.b.get()),
                                Color(hud.r2.get(), hud.g2.get(), hud.b2.get(), 1),
                                (Math.abs(
                                    System.currentTimeMillis() / hud.gradientSpeed.get().toDouble() + counter[0]
                                ) / 10)
                            ).rgb

                            else -> color
                        }, shadow.get()
                    )
                    counter[0] += 1
                    counter[0] = counter[0].coerceIn(0, displayText.length)
                    length += fontRenderer.getStringWidth(charIndex.toString())
                }
                if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen) && editTicks <= 40)
                    fontRenderer.drawString(
                        "_", length2,
                        0F, when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                                Color(
                                    hud.r.get(),
                                    hud.g.get(),
                                    hud.b.get()
                                ), counter[0] * 100, displayText.length * 200
                            ).rgb

                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(
                                Color(hud.r.get(), hud.g.get(), hud.b.get()),
                                Color(hud.r2.get(), hud.g2.get(), hud.b2.get(), 1),
                                (Math.abs(
                                    System.currentTimeMillis() / hud.gradientSpeed.get().toDouble() + counter[0]
                                ) / 10)
                            ).rgb

                            else -> color
                        }, shadow.get()
                    )
            }
        }

        if (editMode && !classProvider.isGuiHudDesigner(mc.currentScreen)) {
            editMode = false
            updateElement()
        }
        return Border(
            -2F,
            -2F,
            length2,
            fontRenderer.fontHeight.toFloat(), 0F
        )
    }

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        displayText = if (editMode) displayString.get() else display
    }

    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L)
                editMode = true

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty())
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))

                updateElement()
                return
            }

            if (ChatAllowedCharacters.isAllowedCharacter(c) || c == '§')
                displayString.set(displayString.get() + c)

            updateElement()
        }
    }

    fun setColor(c: Color): Text {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }


    fun drawRect(x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y2.toDouble())
        GL11.glVertex2d(x2.toDouble(), y2.toDouble())
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun drawRect(x: Double, y: Double, x2: Double, y2: Double, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2, y)
        GL11.glVertex2d(x, y)
        GL11.glVertex2d(x, y2)
        GL11.glVertex2d(x2, y2)
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun glColor(red: Int, green: Int, blue: Int, alpha: Int) {
        GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
    }

    fun glColor(color: Color) {
        val red = color.red / 255f
        val green = color.green / 255f
        val blue = color.blue / 255f
        val alpha = color.alpha / 255f
        GlStateManager.color(red, green, blue, alpha)
    }

    fun glColor(hex: Int) {
        val alpha = (hex shr 24 and 0xFF) / 255f
        val red = (hex shr 16 and 0xFF) / 255f
        val green = (hex shr 8 and 0xFF) / 255f
        val blue = (hex and 0xFF) / 255f
        GlStateManager.color(red, green, blue, alpha)
    }

}