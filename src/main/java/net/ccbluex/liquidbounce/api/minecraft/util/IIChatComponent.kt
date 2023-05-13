package net.ccbluex.liquidbounce.api.minecraft.util

interface IIChatComponent {
    val unformattedText: String
    val chatStyle: IChatStyle
    val formattedText: String

    fun appendText(text: String)
    fun appendSibling(component: IIChatComponent)
}