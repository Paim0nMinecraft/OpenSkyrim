package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.modules.render.BlurSettings
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.Horizontal
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.Vertical
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.misc.StringUtils
import net.ccbluex.liquidbounce.utils.render.*
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowFontShader
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowShader
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color


/**
 * CustomHUD Arraylist element
 *
 * Shows a list of enabled modules
 */
@ElementInfo(name = "Arraylist", single = true)
class Arraylist(
    x: Double = 1.0, y: Double = 2.0, scale: Float = 1F,
    side: Side = Side(Horizontal.RIGHT, Vertical.UP)
) : Element(x, y, scale, side) {
    private val blurValue = BoolValue("Blur", false)
    private val fadeDistanceValue = IntegerValue("Fade-Distance", 50, 1, 100)
    private val RianbowspeedValue = IntegerValue("BRainbowSpeed", 90, 1, 90)
    private val RianbowbValue = FloatValue("BRainbow-Saturation", 1f, 0f, 1f)
    private val RianbowsValue = FloatValue("BRainbow-Brightness", 1f, 0f, 1f)
    private val Rianbowr = IntegerValue("BRainbow-R", 0, 0, 255)
    private val Rianbowb = IntegerValue("BRainbow-B", 50, 0, 64)
    private val Rianbowg = IntegerValue("BRainbow-G", 50, 0, 64)
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)
    private val hAnimation = ListValue("HorizontalAnimation", arrayOf("Default", "None", "Slide", "Astolfo"), "Default")
    private val animationSpeed = FloatValue("Animation-Speed", 0.25F, 0.01F, 1F)
    private val colorModeValue = ListValue(
        "Text-Color",
        arrayOf(
            "Custom",
            "Random",
            "Rainbow",
            "OtherRainbow",
            "Bainbow",
            "OriginalRainbow",
            "LRainbow",
            "DoubleColor",
            "NovoFade"
        ),
        "Custom"
    )
    private val colorRedValue = IntegerValue("Text-R", 0, 0, 255)
    private val colorGreenValue = IntegerValue("Text-G", 111, 0, 255)
    private val colorBlueValue = IntegerValue("Text-B", 255, 0, 255)
    private val rectColorModeValue = ListValue(
        "Rect-Color",
        arrayOf(
            "Custom",
            "Random",
            "Rainbow",
            "OtherRainbow",
            "Bainbow",
            "OriginalRainbow",
            "LRainbow",
            "DoubleColor",
            "NovoFade"
        ),
        "Rainbow"
    )
    private val rectColorRedValue = IntegerValue("Rect-R", 255, 0, 255)
    private val rectColorGreenValue = IntegerValue("Rect-G", 255, 0, 255)
    private val rectColorBlueValue = IntegerValue("Rect-B", 255, 0, 255)
    private val rectColorBlueAlpha = IntegerValue("Rect-Alpha", 255, 0, 255)
    private val saturationValue = FloatValue("Random-Saturation", 0.9f, 0f, 1f)
    private val brightnessValue = FloatValue("Random-Brightness", 1f, 0f, 1f)
    private val tags = BoolValue("Tags", true)
    private val shadow = BoolValue("ShadowText", true)
    private val backgroundColorModeValue = ListValue(
        "Background-Color",
        arrayOf(
            "Custom",
            "Random",
            "Rainbow",
            "Bainbow",
            "OriginalRainbow",
            "LRainbow",
            "DoubleColor"
        ),
        "Custom"
    )
    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255)
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255)
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255)
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 0, 0, 255)
    private val rectValue = ListValue("Rect", arrayOf("None", "Left", "Right", "Outline", "Special", "Top"), "None")
    private val upperCaseValue = BoolValue("UpperCase", false)
    private val spaceValue = FloatValue("Space", 0F, 0F, 5F)
    private val textHeightValue = FloatValue("TextHeight", 11F, 1F, 20F)
    private val textYValue = FloatValue("TextY", 1F, 0F, 20F)
    private val tagsArrayColor = BoolValue("TagsArrayColor", false)
    private val Breakchange = BoolValue("NameBreak", false)
    private val fontValue = FontValue("Font", Fonts.posterama40)

    private var x2 = 0
    private var y2 = 0F

    private var modules = emptyList<Module>()

    override fun shadow() {
        val rectMode = rectValue.get()
        val textHeight = textHeightValue.get()
        modules.forEachIndexed { index, module ->
            val xPos = -module.slide - 2
            RenderUtils.drawRect(
                xPos - if (rectMode.equals("right", true)) 5 else 2,
                module.higt,
                if (rectMode.equals("right", true)) -3F else 0F,
                module.higt + textHeight, Color(0, 0, 0, backgroundColorAlphaValue.get())
            )
        }
    }

    override fun drawElement(): Border? {
        val fontRenderer = fontValue.get()
        val counter = intArrayOf(0)

        AWTFontRenderer.assumeNonVolatile = true

        // Slide animation - update every render
        val delta = RenderUtils.deltaTime

        for (module in LiquidBounce.moduleManager.modules) {
            if (!module.array || (!module.state && module.slide == 0F)) continue

            var displayString = if (module.nameBreak) {
                StringUtils.breakString(
                    if (!tags.get()) {
                        module.name
                    } else if (tagsArrayColor.get()) {
                        module.colorlessTagName
                    } else module.tagName
                )
            } else
                if (!tags.get()) {
                    module.name
                } else if (tagsArrayColor.get()) {
                    module.colorlessTagName
                } else module.tagName

            if (upperCaseValue.get())
                displayString = displayString.toUpperCase()

            val width = fontRenderer.getStringWidth(displayString)

            when (hAnimation.get()) {
                "Astolfo" -> {
                    if (module.state) {
                        module.slide = 1F
                        module.slideStep = 1F
                    } else if (module.slide > 0) {
                        module.slide = 0F
                        module.slideStep = 0F
                    }
                }

                "Slide" -> {
                    if (module.state) {
                        if (module.slide < width) {
                            module.slide = AnimationUtils.animate(
                                width.toDouble(),
                                module.slide.toDouble(),
                                animationSpeed.get().toDouble() * 0.025 * delta.toDouble()
                            ).toFloat()
                            module.slideStep = delta / 1F
                        }
                    } else if (module.slide > 0) {
                        module.slide = AnimationUtils.animate(
                            -width.toDouble(),
                            module.slide.toDouble(),
                            animationSpeed.get().toDouble() * 0.025 * delta.toDouble()
                        ).toFloat()
                        module.slideStep = 0F
                    }
                }

                "Default" -> {
                    if (module.state) {
                        if (module.slide < width) {
                            module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                            module.slideStep += delta / 4F
                        }
                    } else if (module.slide > 0) {
                        module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                        module.slideStep -= delta / 4F
                    }
                }

                else -> {
                    module.slide = if (module.state) width.toFloat() else 0f
                    module.slideStep += (if (module.state) delta else -delta).toFloat()
                }
            }

            module.slide = module.slide.coerceIn(0F, width.toFloat())
            module.slideStep = module.slideStep.coerceIn(0F, width.toFloat())
        }

        // Draw arraylist
        val colorMode = colorModeValue.get()
        val rectColorMode = rectColorModeValue.get()
        val backgroundColorMode = backgroundColorModeValue.get()
        val customColor = Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), 1).rgb
        val rectCustomColor = Color(
            rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(),
            rectColorBlueAlpha.get()
        ).rgb
        val space = spaceValue.get()
        val textHeight = textHeightValue.get()
        val textY = textYValue.get()
        val rectMode = rectValue.get()
        val backgroundCustomColor = Color(
            backgroundColorRedValue.get(), backgroundColorGreenValue.get(),
            backgroundColorBlueValue.get(), backgroundColorAlphaValue.get()
        ).rgb
        val textShadow = shadow.get()
        val textSpacer = textHeight + space
        val saturation = saturationValue.get()
        val brightness = brightnessValue.get()
        val Rsaturation = RianbowbValue.get()
        val Rbrightness = RianbowsValue.get()

        val hud = LiquidBounce.moduleManager[HUD::class.java] as HUD

        when (side.horizontal) {
            Horizontal.RIGHT, Horizontal.MIDDLE -> {
                val blur = LiquidBounce.moduleManager.getModule(BlurSettings::class.java) as BlurSettings
                if (blurValue.get()) {
                    GL11.glTranslated(-renderX, -renderY, 0.0)
                    GL11.glPushMatrix()
                    val floatX = renderX.toFloat()
                    val floatY = renderY.toFloat()
                    var yP = 0F
                    var xP = 0F
                    modules.forEachIndexed { index, module ->
                        val dString = if (module.nameBreak) {
                            StringUtils.breakString(
                                if (!tags.get()) {
                                    module.name
                                } else if (tagsArrayColor.get()) {
                                    module.colorlessTagName
                                } else module.tagName
                            )
                        } else
                            if (!tags.get()) {
                                module.name
                            } else if (tagsArrayColor.get()) {
                                module.colorlessTagName
                            } else module.tagName

                        val wid = fontRenderer.getStringWidth(dString) + 2F
                        val yPos = if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer *
                                if (side.vertical == Vertical.DOWN) index + 1 else index
                        yP += yPos
                        xP = Math.min(xP, -wid)
                    }

                    BlurUtils.blur(floatX, floatY, floatX + xP, floatY + yP, blur.blurRadius.get(), false) {
                        modules.forEachIndexed { index, module ->
                            val xPos = -module.slide - 2
                            RenderUtils.quickDrawRect(
                                floatX + xPos - if (rectValue.get().equals("right", true)) 3 else 2,
                                floatY + module.higt,
                                floatX + if (rectValue.get().equals("right", true)) -1F else 0F,
                                floatY + module.higt + textHeight
                            )
                        }
                    }
                    GL11.glPopMatrix()
                    GL11.glTranslated(renderX, renderY, 0.0)
                }
                modules.forEachIndexed { index, module ->
                    var displayString = if (module.nameBreak) {
                        StringUtils.breakString(
                            if (!tags.get()) {
                                module.name
                            } else if (tagsArrayColor.get()) {
                                module.colorlessTagName
                            } else module.tagName
                        )
                    } else
                        if (!tags.get()) {
                            module.name
                        } else if (tagsArrayColor.get()) {
                            module.colorlessTagName
                        } else module.tagName

                    module.nameBreak = Breakchange.get()

                    if (upperCaseValue.get())
                        displayString = displayString.toUpperCase()
                    val xPos = -module.slide - 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index
                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb
                    val LiquidSlowly = ColorUtils.LiquidSlowly(
                        System.nanoTime(),
                        index * RianbowspeedValue.get(),
                        Rsaturation,
                        Rbrightness
                    )?.rgb
                    val c: Int = LiquidSlowly!!
                    val col = Color(c)
                    val braibow = Color(
                        Rianbowr.get(),
                        col.green / 2 + Rianbowb.get(),
                        col.green / 2 + Rianbowb.get() + Rianbowg.get()
                    ).rgb
                    val backgroundRectRainbow = backgroundColorMode.equals("Rainbow", ignoreCase = true)

                    val size = modules.size * 2.0E-2f
                    if (module.state) {
                        if (module.higt < yPos) {
                            module.higt += (size -
                                    Math.min(
                                        module.higt * 0.002f, size - (module.higt * 0.0001f)
                                    )) * delta
                            module.higt = Math.min(yPos, module.higt)
                        } else {
                            module.higt -= (size -
                                    Math.min(
                                        module.higt * 0.002f, size - (module.higt * 0.0001f)
                                    )) * delta
                            module.higt = Math.max(module.higt, yPos)
                        }
                    }
                    val color2 = RenderUtils.getGradientOffset(
                        Color(hud.r.get(), hud.g.get(), hud.b.get(), backgroundColorAlphaValue.get()),
                        Color(hud.r2.get(), hud.g2.get(), hud.b2.get(), backgroundColorAlphaValue.get()),
                        (Math.abs(
                            System.currentTimeMillis() / hud.gradientSpeed.get()
                                .toDouble() + (module.higt / fontRenderer.fontHeight)
                        ) / 10)
                    ).rgb
                    val color4 = RenderUtils.getGradientOffset(
                        Color(hud.r.get(), hud.g.get(), hud.b.get()),
                        Color(hud.r2.get(), hud.g2.get(), hud.b2.get()),
                        (Math.abs(
                            System.currentTimeMillis() / hud.gradientSpeed.get()
                                .toDouble() + (module.higt / fontRenderer.fontHeight)
                        ) / 10)
                    ).rgb

                    val fadeColor = ColorUtils.fade(
                        Color(hud.r.get(), hud.g.get(), hud.b.get()),
                        index * fadeDistanceValue.get(),
                        100
                    ).rgb

                    RenderUtils.resetColor()
                    RainbowShader.begin(
                        backgroundRectRainbow,
                        if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                        if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                        System.currentTimeMillis() % 10000 / 10000F
                    ).use {
                        RenderUtils.drawRect(
                            xPos - if (rectMode.equals("right", true)) 5 else 2,
                            module.higt,
                            if (rectMode.equals("right", true)) -3F else 0F,
                            module.higt + textHeight, when {
                                backgroundRectRainbow -> 0xFF shl 24
                                backgroundColorMode.equals("Random", ignoreCase = true) -> moduleColor

                                backgroundColorMode.equals("Bainbow", ignoreCase = true) -> braibow

                                backgroundColorMode.equals(
                                    "OriginalRainbow",
                                    ignoreCase = true
                                ) -> ColorUtils.originalrainbow(400000000L * index).rgb

                                backgroundColorMode.equals("LRainbow", ignoreCase = true) -> Colors.getRainbow(
                                    -2000,
                                    (yPos * 8.toFloat()).toInt()
                                )

                                backgroundColorMode.equals("DoubleColor", ignoreCase = true) -> color2

                                backgroundColorMode.equals("NovoFade", ignoreCase = true) -> fadeColor

                                else -> backgroundCustomColor
                            }
                        )
                    }

                    val rainbow = colorMode.equals("Rainbow", ignoreCase = true)

                    RainbowFontShader.begin(
                        rainbow,
                        if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                        if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                        System.currentTimeMillis() % 10000 / 10000F
                    ).use {
                        fontRenderer.drawString(
                            displayString,
                            xPos - if (rectMode.equals("right", true)) 3 else 0,
                            module.higt + textY,
                            when {
                                rainbow -> 0
                                colorMode.equals("Random", ignoreCase = true) -> moduleColor

                                colorMode.equals("Bainbow", ignoreCase = true) -> braibow

                                colorMode.equals("OriginalRainbow", ignoreCase = true) -> ColorUtils.originalrainbow(
                                    400000000L * index
                                ).rgb

                                colorMode.equals("LRainbow", ignoreCase = true) -> Colors.getRainbow(
                                    -2000,
                                    (yPos * 8.toFloat()).toInt()
                                )

                                colorMode.equals("DoubleColor", ignoreCase = true) -> color4

                                colorMode.equals("NovoFade", ignoreCase = true) -> fadeColor

                                else -> customColor
                            },
                            textShadow
                        )
                    }

                    if (!rectMode.equals("none", true)) {
                        val rectRainbow = rectColorMode.equals("Rainbow", ignoreCase = true)

                        RainbowShader.begin(
                            rectRainbow,
                            if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                            if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                            System.currentTimeMillis() % 10000 / 10000F
                        ).use {
                            val rectColor = when {
                                rectRainbow -> 0
                                rectColorMode.equals("Random", ignoreCase = true) -> moduleColor

                                rectColorMode.equals("Bainbow", ignoreCase = true) -> braibow

                                rectColorMode.equals(
                                    "OriginalRainbow",
                                    ignoreCase = true
                                ) -> ColorUtils.originalrainbow(400000000L * index).rgb

                                rectColorMode.equals("LRainbow", ignoreCase = true) -> Colors.getRainbow(
                                    -2000,
                                    (yPos * 8.toFloat()).toInt()
                                )

                                rectColorMode.equals("DoubleColor", ignoreCase = true) -> color4

                                rectColorMode.equals("NovoFade", ignoreCase = true) -> fadeColor


                                else -> rectCustomColor
                            }

                            when {
                                rectMode.equals("left", true) -> RenderUtils.drawRect(
                                    xPos - 3, module.higt, xPos - 2, module.higt + textHeight,
                                    rectColor
                                )

                                rectMode.equals("right", true) -> RenderUtils.drawRect(
                                    0F, module.higt, 1F,
                                    module.higt + textHeight, rectColor
                                )

                            }
                            if (rectMode.equals("outline", true)) {
                                RenderUtils.drawRect(
                                    -1F, module.higt - 1F, 0F,
                                    module.higt + textHeight, rectColor
                                )
                                RenderUtils.drawRect(
                                    xPos - 3, module.higt, xPos - 2, module.higt + textHeight,
                                    rectColor
                                )
                                if (module != modules[0]) {
                                    var displayStrings = if (module.nameBreak) {
                                        StringUtils.breakString(
                                            if (!tags.get()) {
                                                modules[index - 1].name
                                            } else if (tagsArrayColor.get()) {
                                                modules[index - 1].colorlessTagName
                                            } else modules[index - 1].tagName
                                        )
                                    } else
                                        if (!tags.get())
                                            modules[index - 1].name
                                        else if (tagsArrayColor.get())
                                            modules[index - 1].colorlessTagName
                                        else modules[index - 1].tagName

                                    RenderUtils.drawRect(
                                        xPos - 3 - (fontRenderer.getStringWidth(displayStrings) - fontRenderer.getStringWidth(
                                            displayString
                                        )), module.higt, xPos - 2, module.higt + 1,
                                        rectColor
                                    )
                                    if (module == modules[modules.size - 1]) {
                                        RenderUtils.drawRect(
                                            xPos - 3, module.higt + textHeight, 0.0F, module.higt + textHeight + 1,
                                            rectColor
                                        )
                                    }
                                } else {
                                    RenderUtils.drawRect(xPos - 3, module.higt, 0F, module.higt - 1, rectColor)
                                }
                            }
                            if (rectMode.equals("special", true)) {
                                if (module == modules[0]) {
                                    RenderUtils.drawRect(xPos - 2, module.higt, 0F, module.higt - 1, rectColor)
                                }
                                if (module == modules[modules.size - 1]) {
                                    RenderUtils.drawRect(
                                        xPos - 2,
                                        module.higt + textHeight,
                                        0F,
                                        module.higt + textHeight + 1.2F,
                                        rectColor
                                    )
                                }
                            }
                            if (rectMode.equals("top", true)) {
                                if (module == modules[0]) {
                                    RenderUtils.drawRect(xPos - 2, module.higt, 0F, module.higt + 1.2F, rectColor)
                                }
                            }
                        }
                    }
                }
            }

            Horizontal.LEFT -> {
                modules.forEachIndexed { index, module ->
                    var displayString = if (!tags.get())
                        module.name
                    else if (tagsArrayColor.get())
                        module.colorlessTagName
                    else module.tagName

                    if (upperCaseValue.get())
                        displayString = displayString.toUpperCase()

                    val width = fontRenderer.getStringWidth(displayString)
                    val xPos = -(width - module.slide) + if (rectMode.equals("left", true)) 5 else 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index
                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb
                    val LiquidSlowly = ColorUtils.LiquidSlowly(
                        System.nanoTime(),
                        index * RianbowspeedValue.get(),
                        Rsaturation,
                        Rbrightness
                    )?.rgb
                    val c: Int = LiquidSlowly!!
                    val col = Color(c)

                    val backgroundRectRainbow = backgroundColorMode.equals("Rainbow", ignoreCase = true)

                    val size = modules.size * 2.0E-2f
                    if (module.state) {
                        if (module.higt < yPos) {
                            module.higt += (size -
                                    Math.min(
                                        module.higt * 0.002f, size - (module.higt * 0.0001f)
                                    )) * delta
                            module.higt = Math.min(yPos, module.higt)
                        } else {
                            module.higt -= (size -
                                    Math.min(
                                        module.higt * 0.002f, size - (module.higt * 0.0001f)
                                    )) * delta
                            module.higt = Math.max(module.higt, yPos)
                        }
                    }
                    RainbowShader.begin(
                        backgroundRectRainbow,
                        if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                        if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                        System.currentTimeMillis() % 10000 / 10000F
                    ).use {
                        RenderUtils.drawRect(
                            0F,
                            module.higt,
                            xPos + width + if (rectMode.equals("right", true)) 5 else 2,
                            module.higt + textHeight, when {
                                backgroundRectRainbow -> 0
                                backgroundColorMode.equals("Random", ignoreCase = true) -> moduleColor
                                else -> backgroundCustomColor
                            }
                        )
                    }

                    val rainbow = colorMode.equals("Rainbow", ignoreCase = true)

                    RainbowFontShader.begin(
                        rainbow,
                        if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                        if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                        System.currentTimeMillis() % 10000 / 10000F
                    ).use {
                        fontRenderer.drawString(
                            displayString, xPos, module.higt + textY, when {
                                rainbow -> 0
                                colorMode.equals("Random", ignoreCase = true) -> moduleColor
                                else -> customColor
                            }, textShadow
                        )
                    }

                    val rectColorRainbow = rectColorMode.equals("Rainbow", ignoreCase = true)

                    RainbowShader.begin(
                        rectColorRainbow,
                        if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                        if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                        System.currentTimeMillis() % 10000 / 10000F
                    ).use {
                        if (!rectMode.equals("none", true)) {
                            val rectColor = when {
                                rectColorRainbow -> 0
                                rectColorMode.equals("Random", ignoreCase = true) -> moduleColor
                                else -> rectCustomColor
                            }

                            when {
                                rectMode.equals("left", true) -> RenderUtils.drawRect(
                                    0F,
                                    module.higt - 1, 3F, module.higt + textHeight, rectColor
                                )

                                rectMode.equals("right", true) ->
                                    RenderUtils.drawRect(
                                        xPos + width + 2, module.higt, xPos + width + 2 + 3,
                                        module.higt + textHeight, rectColor
                                    )
                            }
                        }
                    }
                }
            }
        }

        // Draw border

        x2 = Int.MIN_VALUE

        if (modules.isEmpty()) {
            return if (side.horizontal == Horizontal.LEFT)
                Border(0F, -1F, 20F, 20F, 0F)
            else
                Border(0F, -1F, -20F, 20F, 0F)
        }

        for (module in modules) {
            when (side.horizontal) {
                Horizontal.RIGHT, Horizontal.MIDDLE -> {
                    val xPos = -module.slide.toInt() - 2
                    if (x2 == Int.MIN_VALUE || xPos < x2) x2 = xPos
                }

                Horizontal.LEFT -> {
                    val xPos = module.slide.toInt() + 14
                    if (x2 == Int.MIN_VALUE || xPos > x2) x2 = xPos
                }
            }
        }
        y2 = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) * modules.size

        return Border(0F, 0F, x2 - 7F, y2 - if (side.vertical == Vertical.DOWN) 1F else 0F, 0F)


        AWTFontRenderer.assumeNonVolatile = false
        GlStateManager.resetColor()

        return null
    }

    override fun updateElement() {
        modules = LiquidBounce.moduleManager.modules
            .filter { it.array && it.slide > 0 }
            .sortedBy {
                -fontValue.get().getStringWidth(
                    if (Breakchange.get()) StringUtils.breakString(
                        if (!tags.get()) {
                            it.name
                        } else if (tagsArrayColor.get()) {
                            it.colorlessTagName
                        } else it.tagName
                    ) else if (upperCaseValue.get()) (if (!tags.get()) it.name else if (tagsArrayColor.get()) it.colorlessTagName else it.tagName).toUpperCase() else if (!tags.get()) it.name else if (tagsArrayColor.get()) it.colorlessTagName else it.tagName
                )
            }
    }

}