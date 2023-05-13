package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MinecraftInstance.classProvider
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max


/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications", single = true)
class Notifications(
    x: Double = 0.0, y: Double = 0.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {
    /**
     * Example notification for CustomHUD designer
     */

    private val exampleNotification = Notification("Notification", "This is example", NotifyType.INFO)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val notifications = mutableListOf<Notification>()
        //FUCK YOU java.util.ConcurrentModificationException
        for ((index, notify) in LiquidBounce.hud.notifications.withIndex()) {
            GL11.glPushMatrix()

            if (notify.drawNotification(index, this.renderX.toFloat(), this.renderY.toFloat(), scale)) {
                notifications.add(notify)
            }

            GL11.glPopMatrix()
        }
        for (notify in notifications) {
            LiquidBounce.hud.notifications.remove(notify)
        }

        if (classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (!LiquidBounce.hud.notifications.contains(exampleNotification))
                LiquidBounce.hud.addNotification(exampleNotification)

            exampleNotification.fadeState = FadeState.STAY
            exampleNotification.displayTime = System.currentTimeMillis()
//            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(-exampleNotification.width.toFloat(), -exampleNotification.height.toFloat(), 0F, 0F, 0f)
        }

        return null
    }

}


class Notification(
    val title: String,
    val content: String,
    val type: NotifyType,
    val time: Int = 1000,
    val animeTime: Int = 350
) {
    private var s: String? = null
    var n2: Int = Fonts.posterama35.getStringWidth(content)
    var textLength = Math.max(n2, 0)
    val width = this.textLength.toFloat() + 50.0f
    val height = 30
    var fadeState = FadeState.IN
    var nowY = -height
    var displayTime = System.currentTimeMillis()
    var animeXTime = System.currentTimeMillis()
    var x = 0F

    var animeYTime = System.currentTimeMillis()


    /**
     * Draw notification
     */
    fun drawNotification(index: Int, blurRadius: Float, y: Float, scale: Float): Boolean {

        val realY = -(index + 1) * (height + 2)
        var color: Int? = null
        var img: IResourceLocation? = null
        val nowTime = System.currentTimeMillis()

        var transY = nowY.toDouble()
        //Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = EaseUtils.easeOutQuart(pct)
            }
            GL11.glTranslated(0.0, (realY - nowY) * pct, 0.0)
        } else {
            animeYTime = nowTime

        }
        GL11.glTranslated(1.0, nowY.toDouble(), 0.0)

        //X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            FadeState.IN -> {
                if (pct > 1) {
                    fadeState = FadeState.STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = EaseUtils.easeOutQuart(pct)
                transY += (realY - nowY) * pct
            }

            FadeState.STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = FadeState.OUT
                    animeXTime = nowTime
                }
            }

            FadeState.OUT -> {
                if (pct > 1) {
                    fadeState = FadeState.END
                    animeXTime = nowTime
                    pct = 2.0
                }
                pct = 1 - EaseUtils.easeInQuart(pct)
            }

            FadeState.END -> {
                return true
            }
        }
        val transX = width - (width * pct) - width
        GL11.glTranslated(width - (width * pct), 0.0, 0.0)
        GL11.glTranslatef(-width.toFloat(), 0F, 0F)
        if (type == NotifyType.WARNING) {
            img = classProvider.createResourceLocation("paimon/notification/exhi/warn.png")
            color = Color(253, 252, 126).rgb
        }
        if (type == NotifyType.INFO) {
            img = classProvider.createResourceLocation("paimon/notification/exhi/info.png")
            color = Color(127, 174, 210).rgb

        }
        if (type == NotifyType.SUCCESS) {
            img = classProvider.createResourceLocation("paimon/notification/exhi/okay.png")
            color = Color(65, 252, 65).rgb

        }
        if (type == NotifyType.ERROR) {
            img = classProvider.createResourceLocation("paimon/notification/exhi/error.png")
            color = Color(226, 87, 76).rgb

        }

        //draw notify
//        GL11.glPushMatrix()
//        GL11.glEnable(GL11.GL_SCISSOR_TEST)

        RenderUtils.drawRect(0f, 0F, width, 29F, Color(32, 32, 32, 200))
        RenderUtils.drawImage(img, 4, 4, 18, 18)
        Fonts.posterama40.drawString(title, 63F - 38f, 4f, Color.white.rgb, true)
        Fonts.posterama35.drawString(
            content,
            63f - 38f,
            4f + Fonts.posterama40.fontHeight + 2f + 2f,
            Color.white.rgb,
            true
        )
//        RenderUtils.drawRect(0f, 4f+Fonts.posterama40.fontHeight+2f+Fonts.posterama35.fontHeight+2f+2f, width,4f+Fonts.posterama40.fontHeight+2f+Fonts.posterama35.fontHeight+2f+2f+2f,ColorUtils.)
        RenderUtils.drawRect(
            width, 4f + Fonts.posterama40.fontHeight + 2f + Fonts.posterama35.fontHeight + 2f + 2f,
            max(width * ((nowTime - displayTime) / (animeTime * 2F + time)), 0F), 26f, color!!
        )
        GlStateManager.resetColor()
        return false
    }
}

enum class NotifyType(var renderColor: Color) {
    SUCCESS(Color(0, 0, 0, 200)),
    ERROR(Color(0, 0, 0, 200)),
    WARNING(Color(0xF5FD00)),
    INFO(Color(137, 214, 255));
}


enum class FadeState { IN, STAY, OUT, END }
