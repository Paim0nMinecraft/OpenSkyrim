package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketClientStatus
import net.minecraft.network.play.client.CPacketClientStatus

class CPacketClientStatusImpl<T : CPacketClientStatus>(wrapped: T) : PacketImpl<T>(wrapped), ICPacketClientStatus

inline fun ICPacketClientStatus.unwrap(): CPacketClientStatus = (this as CPacketClientStatusImpl<*>).wrapped
inline fun CPacketClientStatus.wrap(): ICPacketClientStatus = CPacketClientStatusImpl(this)