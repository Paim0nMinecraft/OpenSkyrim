package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IAbstractClientPlayer
import net.minecraft.client.entity.AbstractClientPlayer

open class AbstractClientPlayerImpl<T : AbstractClientPlayer>(wrapped: T) : EntityPlayerImpl<T>(wrapped),
    IAbstractClientPlayer