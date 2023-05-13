package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.client.entity.player.IEntityOtherPlayerMP
import net.minecraft.client.entity.EntityOtherPlayerMP

class EntityOtherPlayerMPImpl<T : EntityOtherPlayerMP>(wrapped: T) : EntityPlayerImpl<T>(wrapped), IEntityOtherPlayerMP

inline fun IEntityOtherPlayerMP.unwrap(): EntityOtherPlayerMP = (this as EntityOtherPlayerMPImpl<*>).wrapped
inline fun EntityOtherPlayerMP.wrap(): IEntityOtherPlayerMP = EntityOtherPlayerMPImpl(this)