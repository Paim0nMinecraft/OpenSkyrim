package cc.paimon.ui.client


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MinecraftInstance.mc2
import net.ccbluex.liquidbounce.utils.render.EaseUtils.easeOutQuart
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiYesNoCallback
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.io.IOException

class NewGuiWelcome : GuiScreen(), GuiYesNoCallback {
    var curAlpha = 0
    override fun initGui() {
        mc.displayGuiScreen(MainMenu())

        lastMS = System.currentTimeMillis()
        progress = 0f
    }
    private var shoudSkip = false
    private var progress = 0f
    private var lastMS = 0L

    @Throws(IOException::class)
    override fun actionPerformed(p_actionPerformed_1_: GuiButton) {
        super.actionPerformed(p_actionPerformed_1_)
    }

    override fun drawScreen(p_drawScreen_1_: Int, p_drawScreen_2_: Int, p_drawScreen_3_: Float) {
//        drawBackground(0)
        drawWelcome(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_)
        if (shoudSkip) {
            mc2.displayGuiScreen(MainMenu())
        }
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_)
    }

    @Throws(IOException::class)
    override fun mouseClicked(p_mouseClicked_1_: Int, p_mouseClicked_2_: Int, p_mouseClicked_3_: Int) {
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_)
        shoudSkip = true
//        alpha = 0
    }

    fun drawWelcome(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val Height: Int
        Height = ScaledResolution(mc).scaledHeight
        val Width: Int
        Width = ScaledResolution(mc).scaledWidth
        var text: String
        var Scale: Float = 1f

        if (progress >= 1f) progress = 1f else progress = (System.currentTimeMillis() - lastMS).toFloat() / 750f

        val trueAnim = easeOutQuart(progress.toDouble())

        GL11.glTranslated((1 - trueAnim) * (width / 2.0), (1 - trueAnim) * (height / 2.0), 0.0)
        GL11.glScaled(trueAnim, trueAnim, trueAnim)
        GL11.glPushMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        drawRect(0, 0, Width, Height, Color(0, 0, 0,255).rgb)
        text =
            "Hello "  + mc.session.username+" Welcome back to ${LiquidBounce.CLIENT_NAME}"
        Scale = 2.0f
        GL11.glScaled(Scale.toDouble(), Scale.toDouble(), Scale.toDouble())
        Fonts.posterama40.drawString(
            text, (Width / 2f - Fonts.posterama40.getStringWidth(text)) / Scale,
            (Height / 2 - 9) / Scale, Color(255,255,255,255).rgb
        )
        GL11.glScaled(1.0 / Scale, 1.0 / Scale, 1.0 / Scale)
        text = "Click here to continue..."
        Fonts.posterama40.drawString(
            text, Width / 2.0f - Fonts.posterama40.getStringWidth(text) / 2.0f,
            (Height / 2 + 11).toFloat(), Color(255,255,255,255).rgb
        )
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        GL11.glPopMatrix()
    }
}