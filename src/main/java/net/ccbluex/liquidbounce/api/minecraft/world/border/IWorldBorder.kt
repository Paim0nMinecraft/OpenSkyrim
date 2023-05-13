package net.ccbluex.liquidbounce.api.minecraft.world.border

import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos

interface IWorldBorder {
    fun contains(blockPos: WBlockPos): Boolean
}