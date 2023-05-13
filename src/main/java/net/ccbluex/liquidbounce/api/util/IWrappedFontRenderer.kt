package net.ccbluex.liquidbounce.api.util

interface IWrappedFontRenderer {
    fun drawString(s: String?, x: Float, y: Float, color: Int): Int
    fun drawStringWithShadow(text: String?, x: Float, y: Float, color: Int): Int
    fun drawCenteredString(s: String, x: Float, y: Float, color: Int, shadow: Boolean): Int
    fun drawCenteredString(s: String, x: Float, y: Float, color: Int): Int
    fun drawString(text: String?, x: Float, y: Float, color: Int, shadow: Boolean): Int
    fun drawCenteredStringWithShadow(text: String, x: Float, y: Float, color: Int): Int
    fun getColorCode(charCode: Char): Int
    fun getStringWidth(text: String?): Int
    fun getCharWidth(character: Char): Int
}