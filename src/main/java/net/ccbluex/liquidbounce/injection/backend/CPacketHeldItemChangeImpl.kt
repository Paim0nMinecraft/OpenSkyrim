package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketHeldItemChange
import net.minecraft.network.play.client.CPacketHeldItemChange

class CPacketHeldItemChangeImpl<T : CPacketHeldItemChange>(wrapped: T) : PacketImpl<T>(wrapped),
    ICPacketHeldItemChange {
    override val slotId: Int
        get() = wrapped.slotId
}

inline fun ICPacketHeldItemChange.unwrap(): CPacketHeldItemChange = (this as CPacketHeldItemChangeImpl<*>).wrapped
inline fun CPacketHeldItemChange.wrap(): ICPacketHeldItemChange = CPacketHeldItemChangeImpl(this)