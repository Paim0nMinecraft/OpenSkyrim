package net.ccbluex.liquidbounce.api.minecraft.util

import com.mojang.authlib.GameProfile

interface ISession {
    val profile: GameProfile
    val username: String
    val playerId: String
    val sessionType: String

    val token: String
}