package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.IClassProvider
import net.ccbluex.liquidbounce.api.IExtractedFunctions
import net.ccbluex.liquidbounce.api.Wrapper
import net.ccbluex.liquidbounce.api.minecraft.client.IMinecraft
import net.ccbluex.liquidbounce.api.util.IWrappedUser
import net.minecraft.client.Minecraft

object WrapperImpl : Wrapper {
    override val classProvider: IClassProvider = ClassProviderImpl
    override val minecraft: IMinecraft
        get() = MinecraftImpl(Minecraft.getMinecraft())
    override val microsoftUser: IWrappedUser
        get() = IWrappedUser.INSTANCE
    override val functions: IExtractedFunctions = ExtractedFunctionsImpl
}