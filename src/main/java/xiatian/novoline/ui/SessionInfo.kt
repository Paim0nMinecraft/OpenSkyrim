package xiatian.novoline.ui

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.Minecraft
import xiatian.Palette
import java.awt.Color


@ElementInfo(name = "Session Info")
class SessionInfo(x: Double = 15.0, y: Double = 10.0, scale: Float = 1F,
                  side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP)
) : Element(x, y, scale, side) {


    private val colorModeValue = ListValue("Text-Color", arrayOf("Custom","Fade","Gident"), "Gident")
    private val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
    private val distanceValue = IntegerValue("Line-Distance", 0, 0, 400)
    private val gradientAmountValue = IntegerValue("Gradient-Amount", 25, 1, 50)
    private val radiusValue = FloatValue("Radius", 0f, 0f, 10f)
    private val outline = BoolValue("Outline",false)
    private val bgredValue = IntegerValue("Bg-R", 0, 0, 255)
    private val bggreenValue = IntegerValue("Bg-G", 0, 0, 255)
    private val bgblueValue = IntegerValue("Bg-B", 0, 0, 255)
    private val bgalphaValue = IntegerValue("Bg-Alpha", 0, 0, 255)

    val y2 = (net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.fontHeight * 5 + 11f) * 1.1f
    val x2 = 125f * 1.1f

    override fun shadow() {
        RenderUtils.drawRoundedRect2(0f, -2f, x2,y2, radiusValue.get(), Color(bgredValue.get(), bggreenValue.get(), bgblueValue.get(), bgalphaValue.get()).rgb)
    }


    override fun drawElement(): Border? {

        val y2 = (net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.fontHeight * 5 + 11f) * 1.1f
        val x2 = 125f * 1.1f


        val time: String
        time = if (Minecraft.getMinecraft().isSingleplayer) {
            "SinglePlayer"
        } else {
            val durationInMillis: Long = System.currentTimeMillis() - Recorder.startTime
            val second = durationInMillis / 1000 % 60
            val minute = durationInMillis / (1000 * 60) % 60
            val hour = durationInMillis / (1000 * 60 * 60) % 24
            String.format("%02d:%02d:%02d", hour, minute, second)
        }

        val hud = LiquidBounce.moduleManager[HUD::class.java] as HUD

        for (i in 0..(gradientAmountValue.get()-1)) {
            val colorMode = colorModeValue.get()
            val x3 = 120.56 * 1.1f
            val barStart = i.toDouble() / gradientAmountValue.get().toDouble() * x3
            val barEnd = (i + 1).toDouble() / gradientAmountValue.get().toDouble() * x3
            RenderUtils.drawGradientSideways(2 + barStart, net.ccbluex.liquidbounce.ui.font.Fonts.posterama40.fontHeight + 2.5 + 0.0,barEnd + 2, net.ccbluex.liquidbounce.ui.font.Fonts.posterama40.fontHeight + 2.5 + 1.4f, when {
                colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(hud.r.get(), hud.g.get(), hud.b.get()), i * distanceValue.get(), (x3 * 200).toInt()).rgb
                colorMode.equals("Gident", ignoreCase = true) ->  RenderUtils.getGradientOffset(
                    Color(hud.r.get(), hud.g.get(), hud.b.get()),
                    Color(hud.r2.get(), hud.g2.get(), hud.b2.get(),1),
                    (Math.abs(
                        System.currentTimeMillis() / gidentspeed.get()
                            .toDouble() + i *distanceValue.get()
                    ) / 10)
                ).rgb

                else -> Color.WHITE.rgb
            },
                when {
                    colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(hud.r.get(), hud.g.get(), hud.b.get()), i * distanceValue.get(),
                        (x3 * 200).toInt()
                    ).rgb
                    colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(
                            Color(hud.r.get(), hud.g.get(), hud.b.get()),
                            Color(hud.r2.get(), hud.g2.get(), hud.b2.get(),1),
                            (Math.abs(
                                System.currentTimeMillis() / gidentspeed.get()
                                    .toDouble() + i  * distanceValue.get()
                            ) / 10)
                        ).rgb

                    else -> Color.WHITE.rgb
                })
        }
        //val barLength = (fontRenderer.getSgetStringWidth(displayText) + 4F).toDouble()

       // RoundedUtil.drawRound(0f, 0f, x2,y2, radiusValue.get(), Color(bgredValue.get(), bggreenValue.get(), bgblueValue.get(), bgalphaValue.get()))
        RenderUtils.drawRoundedRect2(0f, -2f, x2,y2, radiusValue.get(), Color(bgredValue.get(), bggreenValue.get(), bgblueValue.get(), bgalphaValue.get()).rgb)
        if (outline.get()){
            RenderUtils.drawGidentOutlinedRoundedRect(0.0, -2.0, x2.toDouble(), y2.toDouble(), radiusValue.get().toDouble(),2.5F)
        }
        //RenderUtils.drawShadow(2f, -2f, x2, y2,)
        net.ccbluex.liquidbounce.ui.font.Fonts.posterama40.drawCenteredString("Session Infomation", x2 / 2f, 0f, Color.WHITE.rgb, true)

        //大神绘制字符串^_^
        net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.drawString("Play Time:",3F,
            (net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.fontHeight + 7).toFloat(), Color.WHITE.rgb,true)

       net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.drawString("$time",(x2  - net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.getStringWidth("$time") - 1f),
           net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.fontHeight + 7f,Color.WHITE.rgb,true)

       net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.drawString("Games Won" , 3F ,
           (net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.fontHeight * 2 + 9).toFloat(), Color.WHITE.rgb,true)
       net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.drawString("${Recorder.win}",(x2 -net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.getStringWidth("${Recorder.win}") -1F) ,
           net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.fontHeight * 2 + 9f,Color.WHITE.rgb,true)

       net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.drawString("Players Killed"  , 3F,
           (net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.fontHeight * 3 + 11).toFloat(), Color.WHITE.rgb,true)
       net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.drawString(
           LiquidBounce.combatManager.kills.toString(),(x2 -net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.getStringWidth(
            LiquidBounce.combatManager.kills.toString()
        )-1F),
           net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.fontHeight * 3 + 11f,Color.WHITE.rgb,true)

       net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.drawString("Staff/watchdogs Bans"  , 3F,
           (net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.fontHeight * 4 + 13).toFloat(), Color.WHITE.rgb,true)
       net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.drawString("0/${Recorder.ban}",(x2 -net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.getStringWidth("0/${Recorder.ban}")-1F),
           net.ccbluex.liquidbounce.ui.font.Fonts.posterama35.fontHeight * 4 + 13f,Color.WHITE.rgb,true)
        return Border(-2f, -2f, x2, y2,radiusValue.get())
    }
}