package cc.paimon.skid.lbp.newVer.element.components

import cc.paimon.skid.lbp.newVer.ColorManager
import cc.paimon.skid.lbp.newVer.extensions.animSmooth
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import java.awt.Color

class Slider {
    private var smooth = 0F
    private var value = 0F

    fun onDraw(x: Float, y: Float, width: Float, accentColor: Color) {
        smooth = smooth.animSmooth(value, 0.5F)
        RenderUtils.drawRoundedRect(x - 1F, y - 1F, x + width + 1F, y + 1F, 1F, ColorManager.unusedSlider.rgb)
        RenderUtils.drawRoundedRect(x - 1F, y - 1F, x + width * (smooth / 100F) + 1F, y + 1F, 1F, accentColor.rgb)
        RenderUtils.drawFilledCircle((x + width * (smooth / 100F)).toInt(), y.toInt(), 5F, Color(0, 140, 255))
        RenderUtils.drawFilledCircle((x + width * (smooth / 100F)).toInt(), y.toInt(), 3F, ColorManager.background)
    }

    fun setValue(desired: Float, min: Float, max: Float) {
        value = (desired - min) / (max - min) * 100F
    }
}
