package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerBlockPlacement
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock

class CPacketPlayerBlockPlacementImpl<T : CPacketPlayerTryUseItemOnBlock>(wrapped: T) : PacketImpl<T>(wrapped),
    ICPacketPlayerBlockPlacement

inline fun ICPacketPlayerBlockPlacement.unwrap(): CPacketPlayerTryUseItemOnBlock =
    (this as CPacketPlayerBlockPlacementImpl<*>).wrapped

inline fun CPacketPlayerTryUseItemOnBlock.wrap(): ICPacketPlayerBlockPlacement = CPacketPlayerBlockPlacementImpl(this)