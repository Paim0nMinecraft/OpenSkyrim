package cc.paimon.skid.lbp.newVer.element.module.value.impl

import cc.paimon.skid.lbp.newVer.element.components.Checkbox
import cc.paimon.skid.lbp.newVer.element.module.value.ValueElement
import net.ccbluex.liquidbounce.ui.font.Fonts
import cc.paimon.skid.lbp.newVer.MouseUtils
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.value.BoolValue

import java.awt.Color

class BooleanElement(value: BoolValue): ValueElement<Boolean>(value) {
    private val checkbox = Checkbox()

    override fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, bgColor: Color, accentColor: Color): Float {
        checkbox.state = value.get()
        checkbox.onDraw(x + 10F, y + 5F, 10F, 10F, bgColor, accentColor)
        Fonts.posterama40.drawString(value.name, x + 25F, y + 10F - Fonts.posterama40.fontHeight / 2F + 2F, Color(26, 26, 26).getRGB())
        return valueHeight
    }

    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        if (isDisplayable() && MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y, x + width, y + 20F))
            value.set(!value.get())
    }
}