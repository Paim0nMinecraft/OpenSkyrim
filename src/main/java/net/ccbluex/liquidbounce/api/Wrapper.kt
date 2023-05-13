package net.ccbluex.liquidbounce.api

import net.ccbluex.liquidbounce.api.minecraft.client.IMinecraft
import net.ccbluex.liquidbounce.api.util.IWrappedUser

interface Wrapper {
    val classProvider: IClassProvider
    val minecraft: IMinecraft
    val microsoftUser: IWrappedUser
    val functions: IExtractedFunctions
}