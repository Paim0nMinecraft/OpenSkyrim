package net.ccbluex.liquidbounce.api.minecraft.client.render

import java.awt.image.BufferedImage

interface WIImageBuffer {
    fun parseUserSkin(image: BufferedImage?): BufferedImage?
    fun skinAvailable()
}