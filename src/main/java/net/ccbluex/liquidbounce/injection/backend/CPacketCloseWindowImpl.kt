package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketCloseWindow
import net.minecraft.network.play.client.CPacketCloseWindow

class CPacketCloseWindowImpl<T : CPacketCloseWindow>(wrapped: T) : PacketImpl<T>(wrapped), ICPacketCloseWindow

inline fun ICPacketCloseWindow.unwrap(): CPacketCloseWindow = (this as CPacketCloseWindowImpl<*>).wrapped
inline fun CPacketCloseWindow.wrap(): ICPacketCloseWindow = CPacketCloseWindowImpl(this)