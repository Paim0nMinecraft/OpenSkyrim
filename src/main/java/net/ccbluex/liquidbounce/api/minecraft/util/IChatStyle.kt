package net.ccbluex.liquidbounce.api.minecraft.util

import net.ccbluex.liquidbounce.api.minecraft.event.IClickEvent

interface IChatStyle {
    var chatClickEvent: IClickEvent?
    var underlined: Boolean
    var color: WEnumChatFormatting?
}