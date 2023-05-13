package net.ccbluex.liquidbounce.api.minecraft.scoreboard

import net.ccbluex.liquidbounce.api.minecraft.util.WEnumChatFormatting

interface ITeam {
    val chatFormat: WEnumChatFormatting

    fun formatString(name: String): String
    fun isSameTeam(team: ITeam): Boolean
}