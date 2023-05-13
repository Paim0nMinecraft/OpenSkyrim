package net.ccbluex.liquidbounce.api.minecraft.entity.player

interface IPlayerCapabilities {
    val allowFlying: Boolean
    var isFlying: Boolean
    val isCreativeMode: Boolean
}