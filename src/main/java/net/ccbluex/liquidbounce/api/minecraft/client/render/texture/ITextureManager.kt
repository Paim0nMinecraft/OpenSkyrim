package net.ccbluex.liquidbounce.api.minecraft.client.render.texture

import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.minecraft.util.ResourceLocation

interface ITextureManager {
    fun loadTexture(textureLocation: IResourceLocation, textureObj: IAbstractTexture): Boolean
    fun bindTexture(image: IResourceLocation)
    fun bindTexture2(image: ResourceLocation)
}